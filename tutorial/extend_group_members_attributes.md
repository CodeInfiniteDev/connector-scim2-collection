### 我幫你做了什麼修改（邏輯設計）

用你的需求來說，就是：

- **讀取 Group 時**：
    - 把原本 SCIM `members` 陣列裡的 complex object，拆成兩個簡單 multi-valued attribute：
        - **`user_member`**：只放 `type == "User"` 的成員 id
        - **`group_member`**：只放 `type == "Group"` 的成員 id
- **寫入 Group（尤其是 UPDATE\_DELTA）時**：
    - 如果 midPoint 在 group 上改動 `user_member` / `group_member`，connector 會自動把這些 id 轉成真正的 SCIM `members` patch（新增/移除/取代），並帶上對應的 `$ref` 與 `type`。

---

### 1. DTO：讓成員知道自己的 type（User / Group）

檔案：`BaseResourceReference`

現在多了一個欄位：

```16:90:/Volumes/blackcat/workspace/connector/ConnIdSCIMBundle/src/main/java/net/tirasa/connid/bundles/scim/common/dto/BaseResourceReference.java
protected String type;

public String getType() { return type; }
public void setType(final String type) { this.type = type; }
```

以及 Builder 也支援：

```27:48:.../BaseResourceReference.java
public Builder type(final String type) {
    instance.setType(type);
    return this;
}
```

這樣 SCIM 回傳的 `"type": "User"` / `"type": "Group"` 就會被 Jackson 反序列化進來，我們也可以在送 PATCH 時自己設定。

---

### 2. Schema：在 Group objectClass 中加上兩個新屬性

檔案：`SCIMAttributeUtils`

- 新增常數：

```115:120:/Volumes/blackcat/workspace/connector/ConnIdSCIMBundle/src/main/java/net/tirasa/connid/bundles/scim/common/utils/SCIMAttributeUtils.java
public static final String SCIM_GROUP_MEMBERS = "members";

public static final String SCIM_GROUP_USER_MEMBERS = "user_member";

public static final String SCIM_GROUP_GROUP_MEMBERS = "group_member";
```

- 在 Group schema 中宣告這兩個欄位是 multi-valued string：

```330:337:.../SCIMAttributeUtils.java
ObjectClassInfoBuilder groupBuilder = new ObjectClassInfoBuilder().setType(ObjectClass.GROUP_NAME);
LOG.ok("SCHEMA: building GROUP object class");
groupBuilder.addAttributeInfo(
        AttributeInfoBuilder.define(SCIMAttributeUtils.SCIM_GROUP_DISPLAY_NAME).setMultiValued(false).build());
groupBuilder.addAttributeInfo(
        AttributeInfoBuilder.define(SCIMAttributeUtils.SCIM_GROUP_MEMBERS).setMultiValued(true).build());
groupBuilder.addAttributeInfo(
        AttributeInfoBuilder.define(SCIM_GROUP_USER_MEMBERS).setMultiValued(true).build());
groupBuilder.addAttributeInfo(
        AttributeInfoBuilder.define(SCIM_GROUP_GROUP_MEMBERS).setMultiValued(true).build());
```

所以從 midPoint 看這個 connector schema 時，Group 物件會多出 `user_member` / `group_member` 兩個多值屬性可以用在 mapping / search / filter。

---

### 3. 讀取 Group：自動算出 `user_member` / `group_member`

檔案：`AbstractSCIMGroup`

在原本的 `toAttributes(...)` 最後，我加了一段邏輯：

```66:101:/Volumes/blackcat/workspace/connector/ConnIdSCIMBundle/src/main/java/net/tirasa/connid/bundles/scim/common/dto/AbstractSCIMGroup.java
// expose synthetic attributes for user and group members based on SCIM members list
if (!members.isEmpty()) {
    List<String> userMemberIds = new ArrayList<>();
    List<String> groupMemberIds = new ArrayList<>();

    for (BaseResourceReference member : members) {
        if (member == null || member.getValue() == null) {
            continue;
        }
        String id = member.getValue();

        String resolvedType = member.getType();
        if (resolvedType == null && member.getRef() != null) {
            String ref = member.getRef();
            if (ref.contains("/Groups/")) {
                resolvedType = "Group";
            } else if (ref.contains("/Users/")) {
                resolvedType = "User";
            }
        }

        if ("Group".equalsIgnoreCase(resolvedType)) {
            groupMemberIds.add(id);
        } else if ("User".equalsIgnoreCase(resolvedType)) {
            userMemberIds.add(id);
        } else {
            // fallback: if type is unknown, consider it as user to not lose information
            userMemberIds.add(id);
        }
    }

    if (!userMemberIds.isEmpty()) {
        attrs.add(AttributeBuilder.build(SCIMAttributeUtils.SCIM_GROUP_USER_MEMBERS, userMemberIds));
    }
    if (!groupMemberIds.isEmpty()) {
        attrs.add(AttributeBuilder.build(SCIMAttributeUtils.SCIM_GROUP_GROUP_MEMBERS, groupMemberIds));
    }
}
```

重點：

- 每個 `members` 項目（`BaseResourceReference`）：
    - 先看 `member.getType()`，如果是 `"User"` 就進 `user_member`，如果 `"Group"` 就進 `group_member`。
    - 如果資料沒帶 type，就 fallback 用 `$ref` 判斷 `/Users/` vs `/Groups/`。
    - 如果完全判不出來，預設當作 user（避免遺失資訊）。

**結果：**

- midPoint 在讀取 group 時，會同時看到：
    - `members`（原本就有、complex 的，多半你可以忽略）
    - **`user_member`**：所有 user 成員的 id 清單
    - **`group_member`**：所有 nested group 成員的 id 清單

這樣你可以在 midPoint 的 association / filter 裡直接用這兩個簡單欄位。

---

### 4. 更新 Group（UPDATE\_DELTA）：用 `user_member` / `group_member` 來驅動真正的 SCIM `members` 變更

檔案：`SCIMv2Connector.buildGroupPatch(...)`

原本這個方法只認得 `members` 這個 attribute delta，並且假設所有 id 都是 user。現在我做了擴充：

1. **保留舊的 `members` 行為（相容）**

   原來這段還在，只是補上 `type("User")` 讓發出去的 `members` item 多帶 `type`：

```253:304:/Volumes/blackcat/workspace/connector/ConnIdSCIMBundle/src/main/java/net/tirasa/connid/bundles/scim/v2/SCIMv2Connector.java
modifications.stream().filter(mod -> SCIMAttributeUtils.SCIM_GROUP_MEMBERS.equalsIgnoreCase(mod.getName()))
        .findFirst().ifPresent(mod -> {
            // remove ops
            ...
            // add ops (user only)
            ...
            // replace ops
            if (!CollectionUtil.isEmpty(mod.getValuesToReplace())) {
                memberOperations.add(new SCIMv2PatchOperation.Builder()
                        .op(SCIMAttributeUtils.SCIM_REPLACE)
                        .path(SCIMAttributeUtils.SCIM_GROUP_MEMBERS)
                        .value(mod.getValuesToReplace().stream().map(vtr -> {
                            SCIMv2User user = client.getUser(vtr.toString());
                            BaseResourceReference resRef = null;
                            ...
                            else {
                                resRef = new BaseResourceReference.Builder().value(user.getId())
                                        .ref(configuration.getBaseAddress() + "User/" + user.getId())
                                        .display(user.getDisplayName())
                                        .type("User")
                                        .build();
                            }
                            return resRef;
                        }).filter(Objects::nonNull).collect(Collectors.toList()))
                        .build());
            }
        });
```

2. **新增支援 `user_member`（user 成員）**

    - 當 midPoint 對 group 的 `user_member` 做 ADD / REMOVE / REPLACE 時：

```304:343:.../SCIMv2Connector.java
modifications.stream()
        .filter(mod -> SCIMAttributeUtils.SCIM_GROUP_USER_MEMBERS.equalsIgnoreCase(mod.getName()))
        .findFirst().ifPresent(mod -> {
            // remove ops -> 轉成 members[value eq "id"] 形式
            if (!CollectionUtil.isEmpty(mod.getValuesToRemove())) {
                memberOperations.add(new SCIMv2PatchOperation.Builder()
                        .op(SCIMAttributeUtils.SCIM_REMOVE)
                        .path(buildFilteredPath(
                                SCIMAttributeUtils.SCIM_GROUP_MEMBERS,
                                null,
                                mod.getValuesToRemove(),
                                "or",
                                "eq"))
                        .build());
            }
            // add ops -> 用 id 找 SCIMv2User，組成 BaseResourceReference(type=User)
            if (!CollectionUtil.isEmpty(mod.getValuesToAdd())) {
                memberOperations.add(new SCIMv2PatchOperation.Builder()
                        .op(SCIMAttributeUtils.SCIM_ADD)
                        .path(SCIMAttributeUtils.SCIM_GROUP_MEMBERS)
                        .value(mod.getValuesToAdd().stream().map(vta -> {
                            SCIMv2User user = client.getUser(vta.toString());
                            BaseResourceReference resRef = null;
                            if (user == null) {
                                LOG.error("Unable to add member {0} to the group, user does not exist", vta);
                            } else {
                                resRef = new BaseResourceReference.Builder()
                                        .value(user.getId())
                                        .ref(configuration.getBaseAddress() + "Users/" + user.getId())
                                        .display(user.getDisplayName())
                                        .type("User")
                                        .build();
                            }
                            return resRef;
                        }).filter(Objects::nonNull).collect(Collectors.toList()))
                        .build());
            }
            // replace ops -> REPLACE 整個 members（針對 user 部分）
            if (!CollectionUtil.isEmpty(mod.getValuesToReplace())) {
                memberOperations.add(new SCIMv2PatchOperation.Builder()
                        .op(SCIMAttributeUtils.SCIM_REPLACE)
                        .path(SCIMAttributeUtils.SCIM_GROUP_MEMBERS)
                        .value(mod.getValuesToReplace().stream().map(vtr -> {
                            SCIMv2User user = client.getUser(vtr.toString());
                            BaseResourceReference resRef = null;
                            ...
                            else {
                                resRef = new BaseResourceReference.Builder()
                                        .value(user.getId())
                                        .ref(configuration.getBaseAddress() + "Users/" + user.getId())
                                        .display(user.getDisplayName())
                                        .type("User")
                                        .build();
                            }
                            return resRef;
                        }).filter(Objects::nonNull).collect(Collectors.toList()))
                        .build());
            }
        });
```

3. **新增支援 `group_member`（nested group 成員）**

    - 類似，但改成用 `client.getGroup(...)`，並設定 `type("Group")`：

```343:383:.../SCIMv2Connector.java
modifications.stream()
        .filter(mod -> SCIMAttributeUtils.SCIM_GROUP_GROUP_MEMBERS.equalsIgnoreCase(mod.getName()))
        .findFirst().ifPresent(mod -> {
            // remove ops
            if (!CollectionUtil.isEmpty(mod.getValuesToRemove())) {
                memberOperations.add(new SCIMv2PatchOperation.Builder()
                        .op(SCIMAttributeUtils.SCIM_REMOVE)
                        .path(buildFilteredPath(
                                SCIMAttributeUtils.SCIM_GROUP_MEMBERS,
                                null,
                                mod.getValuesToRemove(),
                                "or",
                                "eq"))
                        .build());
            }
            // add ops
            if (!CollectionUtil.isEmpty(mod.getValuesToAdd())) {
                memberOperations.add(new SCIMv2PatchOperation.Builder()
                        .op(SCIMAttributeUtils.SCIM_ADD)
                        .path(SCIMAttributeUtils.SCIM_GROUP_MEMBERS)
                        .value(mod.getValuesToAdd().stream().map(vta -> {
                            SCIMv2Group group = client.getGroup(vta.toString());
                            BaseResourceReference resRef = null;
                            if (group == null) {
                                LOG.error("Unable to add member group {0} to the group, group does not exist",
                                        vta);
                            } else {
                                resRef = new BaseResourceReference.Builder()
                                        .value(group.getId())
                                        .ref(configuration.getBaseAddress() + "Groups/" + group.getId())
                                        .display(group.getDisplayName())
                                        .type("Group")
                                        .build();
                            }
                            return resRef;
                        }).filter(Objects::nonNull).collect(Collectors.toList()))
                        .build());
            }
            // replace ops
            if (!CollectionUtil.isEmpty(mod.getValuesToReplace())) {
                memberOperations.add(new SCIMv2PatchOperation.Builder()
                        .op(SCIMAttributeUtils.SCIM_REPLACE)
                        .path(SCIMAttributeUtils.SCIM_GROUP_MEMBERS)
                        .value(mod.getValuesToReplace().stream().map(vtr -> {
                            SCIMv2Group group = client.getGroup(vtr.toString());
                            BaseResourceReference resRef = null;
                            if (group == null) {
                                LOG.error(
                                        "Unable to replace member group {0} on the group, group does not exist",
                                        vtr);
                            } else {
                                resRef = new BaseResourceReference.Builder()
                                        .value(group.getId())
                                        .ref(configuration.getBaseAddress() + "Groups/" + group.getId())
                                        .display(group.getDisplayName())
                                        .type("Group")
                                        .build();
                            }
                            return resRef;
                        }).filter(Objects::nonNull).collect(Collectors.toList()))
                        .build());
            }
        });
```

**總結：**

- **midPoint 如果在 group 的 `user_member` 屬性加上一個 user 的 id**：
    - connector 會在 PATCH 中做 `members ADD`，實際發給 SCIM 的 JSON 長得像：
      ```json
      {
        "op": "add",
        "path": "members",
        "value": [
          { "value": "<user-id>", "$ref": ".../Users/<user-id>", "display": "...", "type": "User" }
        ]
      }
      ```
- **midPoint 在 `group_member` 加 group id** 也一樣，只是 `type: "Group"`，`$ref` 指的是 `/Groups/...`。

---

### 5. 你接下來怎麼在 midPoint 用這兩個欄位？

大致可以這樣用：

- 在 Resource schema / association 定義裡，把：
    - **user association** 指到 group 的 `user_member`（而不是原本的 `members`）。
    - **group-nesting association** 指到 group 的 `group_member`。
- 在 outbound/inbound mapping 中，用 `user_member` / `group_member` 作為 multi-valued string 屬性，就不用處理 complex JSON。

---

如果你願意，我可以幫你下一步：

- 示範一段 midPoint resource schema / association 定義（XML）怎麼配置 `user_member` / `group_member`，
- 或幫你加一個 JUnit 測試：建立一個含 nested groups 的 SCIM group，確認讀出來的 `user_member` / `group_member` 內容正確，並測試 UPDATE\_DELTA（往 `user_member` / `group_member` 加/刪 id）實際 PATCH 給 SCIM 的 JSON。