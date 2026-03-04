### 1. SCIM 2.0「get user by id」完整流程（從 ConnId 到 SCIM、再回來）

以 midPoint/ConnId 呼叫 `getObject(ObjectClass.ACCOUNT, uid, options)` 為例，「用 `id` 取單一 user」走的是這條路：

1. **ConnId → Connector（`AbstractSCIMConnector.executeQuery`）**
    - ConnId 會把 `getObject` 轉成 `executeQuery(ObjectClass.ACCOUNT, EqualsFilter(Uid), handler, options)`。
    - 在 `AbstractSCIMConnector` 中，如果 filter 是 `Uid` 或 `id`，就走單一查詢分支：

      ```193:219:/Volumes/blackcat/workspace/connector/ConnIdSCIMBundle/src/main/java/net/tirasa/connid/bundles/scim/common/AbstractSCIMConnector.java
      if (ObjectClass.ACCOUNT.equals(objectClass)) {
          ...
          } else {
              UT result = null;
              if (Uid.NAME.equals(key.getName()) || SCIMAttributeUtils.ATTRIBUTE_ID.equals(key.getName())) {
                  result = null;
                  try {
                      result = client.getUser(AttributeUtil.getAsStringValue(key));
                  } ...
              }
              if (result != null) {
                  handler.handle(fromUser(result, attributesToGet));
              }
          }
      }
      ```

    - 也就是：**ConnId UID → `client.getUser(id)` → `fromUser(...)` → 回傳 `ConnectorObject`**。

2. **Connector → SCIM client（`SCIMv2Client.getUser`）**

   ```47:51:/Volumes/blackcat/workspace/connector/ConnIdSCIMBundle/src/main/java/net/tirasa/connid/bundles/scim/v2/service/SCIMv2Client.java
   @Override
   public SCIMv2User getUser(final String userId) {
       return doGetUser(
               getWebclient("Users", null).path(SCIMUtils.getPath(userId, config)),
               SCIMv2User.class, SCIMv2Attribute.class);
   }
   ```

    - 這裡會建立一個 `WebClient` 指向 `baseAddress/Users/{id}`，然後交給共用的 `doGetUser(...)`。

3. **SCIM client → HTTP GET（`AbstractSCIMService.doGetUser`）**

   ```596:617:/Volumes/blackcat/workspace/connector/ConnIdSCIMBundle/src/main/java/net/tirasa/connid/bundles/scim/common/service/AbstractSCIMService.java
   protected <T extends SCIMBaseAttribute<T>> UT doGetUser(final WebClient webClient, final Class<UT> userType,
           final Class<T> attrType) {
       UT user = null;
       JsonNode node = doGet(webClient);
       ...
       user = SCIMUtils.MAPPER.readValue(node.toString(), userType);
       ...
       // custom attributes
       readCustomAttributes(user, node, attrType);
       return user;
   }
   ```

    - `doGet(webClient)` 會真的發出 HTTP GET，做錯誤檢查後回傳 JSON：
      ```205:231:.../AbstractSCIMService.java
      JsonNode result = null;
      Response response = invokeWithLogging(..., WebClient::get);
      String responseAsString = checkServiceErrors(...);
      result = SCIMUtils.MAPPER.readTree(responseAsString);
      ```
    - 然後用 Jackson 把整個 JSON 反序列化成 `SCIMv2User`。

4. **SCIM JSON → `SCIMv2User` → ConnId Attributes（`AbstractSCIMUser.toAttributes`）**

   `SCIMv2User` 繼承 `AbstractSCIMUser`，真正負責「把 Java 欄位轉成 ConnId `Attribute`」的是：

   ```914:987:/Volumes/blackcat/workspace/connector/ConnIdSCIMBundle/src/main/java/net/tirasa/connid/bundles/scim/common/dto/AbstractSCIMUser.java
   public Set<Attribute> toAttributes(final Class<?> type, final SCIMConnectorConfiguration conf)
           throws IllegalArgumentException, IllegalAccessException {

       Set<Attribute> attrs = new HashSet<>();

       SCIMUtils.getAllFieldsList(type).stream()
           ...
           else if (field.getGenericType().toString().contains(EmailCanonicalType.class.getName())) {
               if (field.getType().equals(List.class)) {
                   List<SCIMGenericComplex<EmailCanonicalType>> list =
                           (List<SCIMGenericComplex<EmailCanonicalType>>) objInstance;
                   for (SCIMGenericComplex<EmailCanonicalType> complex : list) {
                       addAttribute(
                               complex.toAttributes(SCIMAttributeUtils.SCIM_USER_EMAILS,
                                       conf),
                               attrs, field.getType());
                   }
               } ...
           }
           ...
   }
   ```

    - `emails` 欄位是 `List<SCIMGenericComplex<EmailCanonicalType>>`，每一筆 email（含 `type`, `value`, `primary`）會呼叫 `complex.toAttributes("emails", conf)`，再轉成一組 ConnId attributes（例如 `emails.work.value`, `emails.work.primary`）。

5. **Connector 將 `SCIMv2User` 包成 `ConnectorObject`（`AbstractSCIMConnector.fromUser`）**

   ```714:743:/Volumes/blackcat/workspace/connector/ConnIdSCIMBundle/src/main/java/net/tirasa/connid/bundles/scim/common/AbstractSCIMConnector.java
   protected ConnectorObject fromUser(final UT user, final Set<String> attributesToGet) {
       ConnectorObjectBuilder builder = new ConnectorObjectBuilder();
       builder.setObjectClass(ObjectClass.ACCOUNT);
       builder.setUid(user.getId());
       builder.setName(user.getUserName());

       Set<Attribute> userAttributes = user.toAttributes(user.getClass(), configuration);

       for (Attribute toAttribute : userAttributes) {
           String attributeName = toAttribute.getName();
           for (String attributeToGetName : attributesToGet) {
               if (attributeName.equals(attributeToGetName)) {
                   builder.addAttribute(toAttribute);
                   break;
               }
           }
       }

       // custom attributes 省略

       return builder.build();
   }
   ```

    - 這裡就呼應 ConnId 開發指南的概念：
        - `__UID__` = `user.getId()`（SCIM `id`）
        - `__NAME__` = `user.getUserName()`
        - 其他像 `emails.work.value`, `emails.work.primary` 都是普通 attributes。

---

### 2. `emails` / `emails.work.primary` 在這條 read 流程中的型別變化

你給的 SCIM 回應大概長這樣：

```json
"emails": [
  {
    "type": "work",
    "primary": true,
    "value": "example@example.com"
  }
]
```

在這個 connector 裡，對應流程是：

1. **SCIM JSON → `SCIMv2User.emails`**

    - Jackson 會把 `emails` array 反序列化成 `List<SCIMGenericComplex<EmailCanonicalType>>`：

      ```60:73:/Volumes/blackcat/workspace/connector/ConnIdSCIMBundle/src/main/java/net/tirasa/connid/bundles/scim/common/dto/AbstractSCIMUser.java
      protected List<SCIMGenericComplex<EmailCanonicalType>> emails = new ArrayList<>();
      ...
      @JsonSetter(nulls = Nulls.AS_EMPTY)
      public void setEmails(final List<SCIMGenericComplex<EmailCanonicalType>> emails) {
          this.emails = emails;
      }
      ```

    - `SCIMGenericComplex` 裡 `primary` 是 **Boolean**：

      ```72:104:/Volumes/blackcat/workspace/connector/ConnIdSCIMBundle/src/main/java/net/tirasa/connid/bundles/scim/common/dto/SCIMGenericComplex.java
      @JsonProperty
      private Boolean primary;
      ...
      public Boolean isPrimary() { return primary; }
      public void setPrimary(final Boolean primary) { this.primary = primary; }
      ```

   所以 SCIM JSON 的 `"primary": true` → 直接變成 Java 的 `Boolean true`。

2. **`SCIMv2User` → ConnId Attributes（emails.*）**

    - `toAttributes(...)` 會對每一個 `SCIMGenericComplex<EmailCanonicalType>` 呼叫 `toAttributes("emails", conf)`，裡面會利用 `SCIMAttributeUtils.doBuildAttributeFromClassField` 看 field 型別決定值型別：

      ```348:373:/Volumes/blackcat/workspace/connector/ConnIdSCIMBundle/src/main/java/net/tirasa/connid/bundles/scim/common/utils/SCIMAttributeUtils.java
      public static AttributeBuilder doBuildAttributeFromClassField(final Object value, final String name,
              final Class<?> clazz) {
          AttributeBuilder attributeBuilder = new AttributeBuilder();
          if (value != null) {
              if (clazz == boolean.class || clazz == Boolean.class) {
                  attributeBuilder.addValue(Boolean.class.cast(value));
              } else if (value instanceof List<?>) {
                  ...
              } else {
                  attributeBuilder.addValue(value.toString());
              }
          }
          if (name != null) {
              attributeBuilder.setName(name);
          }
          return attributeBuilder;
      }
      ```

    - 因為 `primary` 欄位在 `SCIMGenericComplex` 裡是 `Boolean`，這裡會走 `Boolean.class` 分支：  
      → **ConnId `Attribute("emails.work.primary")` 的值是 Boolean，而不是 String**。

3. **Schema 定義也把它標成 Boolean**

    - 在 connector 提供給 midPoint 的 ConnId schema 裡，`emails.work.primary` 是明確標記為 `Boolean.class`：

      ```155:165:/Volumes/blackcat/workspace/connector/ConnIdSCIMBundle/src/main/java/net/tirasa/connid/bundles/scim/common/utils/SCIMAttributeUtils.java
      userBuilder.addAttributeInfo(AttributeInfoBuilder.define("emails.work.value").build());
      userBuilder.addAttributeInfo(AttributeInfoBuilder.define("emails.work.operation").build());
      userBuilder.addAttributeInfo(AttributeInfoBuilder.define("emails.work.primary").setType(Boolean.class).build());
      ...
      userBuilder.addAttributeInfo(
              AttributeInfoBuilder.define("emails.other.primary").setType(Boolean.class).build());
      ```

   這跟你用 Postman 看到的 SCIM 回應（`primary: true`）是完全對齊的。

---

### 3. 你記得的錯誤「emails.work.primary expect Boolean, but get String」可能從哪裡來？

從上面流程可以看到：**在「讀取 single user」這條路，connector 一路都把 `primary` 當 Boolean 處理**，不會自己把它轉成 String。因此：

- 如果錯誤訊息是 Java `ClassCastException` 類型（例如「java.lang.String cannot be cast to java.lang.Boolean」），很有可能是發生在 **「寫入」路徑（create/update/updateDelta）」**，而不是 `get user`。
    - 在 `AbstractSCIMUser.doSetAttribute`，對 `emails.work.primary` 是這樣寫的：

      ```403:415:/Volumes/blackcat/workspace/connector/ConnIdSCIMBundle/src/main/java/net/tirasa/connid/bundles/scim/common/dto/AbstractSCIMUser.java
      case "emails.work.primary":
          handleSCIMComplexObject(EmailCanonicalType.work, this.emails,
                  s -> s.setPrimary(Boolean.class.cast(value)));
          break;
      ```

    - 如果上層（midPoint 映射或自訂 connector client）給 `emails.work.primary` 的值是 `"true"`（String），這裡做 `Boolean.class.cast(value)` 就會炸掉，訊息會很接近你記得的那句話。

- 另一種常見狀況是：**midPoint 端的 schema 把這個屬性宣告成 string**，但 connector 實際回傳 Boolean：
    - Connector schema 已經定義 `emails.work.primary` 是 Boolean；  
      如果你在 midPoint 裡手動改 schema 或自訂 mapping，用 String 來描述它，就會在 midPoint schema 驗證時看到「expected Boolean, got String」類似的錯誤。

---

### 4. 對你目前這個 case，我建議你這樣檢查：

- **(1) 確認實際 SCIM 回應**  
  你已經用 Postman 查過，看起來 `primary` 是 `true`（Boolean），這是正確的。  
  建議再看一次 connector 的 TRACE log 中 `SCIM RESP ...` 那行，確認 connector 看到的 JSON 也是 `primary: true`，不是 `"true"` 字串。

- **(2) 檢查 midPoint 產生的 resource schema**  
  在 midPoint：
    - 打開該 resource → Schema / 或原始 XML 看 `__ACCOUNT__` objectClass（或你實際用的 object class）。
    - 找到 `emails.work.primary` 那個屬性，確認它的 `type` 是 boolean，而不是 string。

- **(3) 檢查你自訂的 mapping / expressions**（如果有）
    - 如果你在 object template / outbound mapping 有自己設定 `emails.work.primary`，確保 mapping 所產出的值是 boolean（例如 `true()` / `false()`），而不是 `"true"` 這種 string。
    - 若你是做 UPDATE/UPDATE_DELTA 並手動塞這個 attribute，一樣要給 Boolean 值。

---

### 5. 接下來可以做什麼？

如果你願意，下一步我可以幫你做其中一個：

- 幫你把 **「update / updateDelta 時 emails.work.primary」的完整流程** 也畫出來，讓你看清楚 Boolean 在寫入時怎麼被處理，特別是 PATCH（`buildPatchValue` 那一段）。
- 或是你可以貼出你在 midPoint 看到的實際錯誤訊息（stack trace / log 片段），我可以直接對照這個程式碼幫你 pinpoint 錯在「讀」、「寫」、還是 midPoint schema/mapping。