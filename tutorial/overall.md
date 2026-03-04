# 整體架構（先有大概印象）

## 高層概念

- 專案是一個 Maven module，主程式在 `net.tirasa.connid.bundles.scim` 底下。
- 分成三層/模組：
    - `common`：ConnId SPI 實作的抽象、SCIM 資料模型與 HTTP client 抽象（版本通用）。
    - `v2`：SCIM 2.0 版本的實作（Connector + DTO + client）。
    - `v11`：SCIM 1.1 版本的實作。
- 對外經由 ConnId 的 SPI 提供操作：
    - `CreateOp`, `SearchOp`, `UpdateOp`, `DeleteOp`, `SchemaOp`, `TestOp`, `UpdateDeltaOp`
- 底層由 Apache CXF `WebClient` 呼叫 SCIM REST API。

## 真正的 ConnId 入口點

### 抽象 Connector

- `AbstractSCIMConnector`
    - implements：
        - `Connector`, `CreateOp`, `DeleteOp`, `SchemaOp`, `SearchOp<Filter>`,
          `TestOp`, `UpdateOp`, `UpdateDeltaOp`
    - 封裝完整流程：
        - `init`, `executeQuery`, `create`, `update`, `updateDelta`, `delete`, `test`

### 具體版本 Connector

- `SCIMv2Connector`（SCIM 2.0）
- `SCIMv11Connector`（SCIM 1.1）
- 主要差異：
    - SCIM 資源 DTO 型別
    - PATCH 支援
    - provider 特化行為

---

# 各層負責什麼（重要類別/套件）

## ConnId SPI 層（邏輯中樞）

### `AbstractSCIMConnector`

- 負責把 ConnId 的呼叫（`create/update/delete/search/schema/test`）轉成呼叫 `SCIMService client`。
- 內部處理：
    - ConnId `Attribute` ↔ SCIM `User/Group DTO` 的相互轉換
    - `groups / entitlements / enterprise user extension / custom attributes` 的封裝

### `SCIMv2Connector` / `SCIMv11Connector`

- 提供版本特有的 schema 定義與 PATCH 邏輯：
    - `schema()`：呼叫 `SCIMAttributeUtils.buildSchema(...)` 建立 ConnId Schema
    - `buildSCIMClient(...)`：建立 `SCIMv2Client` 或 `SCIMv11Client`
    - `build*Patch*`, `manageEntitlements` 等版本差異

---

## HTTP / SCIM Client 層

### `AbstractSCIMService`

- 負責 HTTP 細節：
    - 建立 `WebClient`
    - 處理 basic/bearer auth、proxy、redirect、TLS、header、query string
- 共用操作：
    - `doGet`, `doCreate`, `doUpdate`, `doUpdatePatch`
    - `doDeleteUser`, `doDeleteGroup`
- OAuth2 token 取得與快取：`getBearerToken`
- 錯誤與重試邏輯

### `SCIMv2Client` / `SCIMv11Client`

- 封裝成高階方法：
    - `getUser`, `getAllUsers`, `createUser`, `updateUser`, `deleteUser`
    - `getGroup`, `getAllGroups`, `createGroup`, `updateGroup`, `deleteGroup`
- 內部直接調用 `AbstractSCIMService` 的 `protected` 方法

---

## SCIM 資料模型與 Attribute 映射

### 通用 DTO 介面與抽象

- `SCIMUser`, `SCIMGroup`, `SCIMBaseMeta`, `SCIMEnterpriseUser`,
  `BaseResourceReference` 等：描述 SCIM 規格的核心欄位

### 版本專屬 DTO

- `SCIMv2User`, `SCIMv2Group`, `SCIMv2Entitlement`, `SCIMv2EnterpriseUser` 等

### 負責事項

- 把 ConnId 的 `Attribute` 填入 SCIM DTO：
    - `fromAttributes`, `fillSCIMCustomAttributes`, `fillEnterpriseUser`
- 反向從 SCIM DTO 產生 ConnId `Attribute`：
    - `toAttributes`, `entitlementsToAttribute`

### Schema 工具：`SCIMAttributeUtils`

- `buildSchema(...)`：定義 ConnId `ObjectClass.ACCOUNT / ObjectClass.GROUP` 的所有欄位  
  （例如 `userName`, `emails.work.value`, `displayName`, `members` 等）
    - 含 custom attributes 與 enterprise user extension
- 提供幫助組 PATCH path 與判斷 multi-valued 的工具方法

---

## 設定與 Provider 行為

### `SCIMConnectorConfiguration`

- 包含：
    - `baseAddress / username/password / bearerToken`
    - OAuth2 `clientId/clientSecret/token URL`
    - proxy 設定、redirect 開關、header 擴充
    - `customAttributes` JSON、是否用 `:` 或 `.` 分隔 extension、update method (PUT/PATCH) 等
- `validate()` 做完整檢查

### `SCIMProvider`

- 描述不同 SCIM server（AWS, Salesforce, WSO2, Keycloak, ...）的特性
- 在 connector 內用來決定特殊 PATCH / group 行為

---

## 測試（學習範本）

- `SCIMv2ConnectorTests`
- `SCIMv11ConnectorTests`
- `SCIMv2ConnectorTestsUtils` / `SCIMv11ConnectorTestsUtils`

使用 Testcontainers 跑真實 SCIM 伺服器，把整條鏈路跑過一遍：

`ConnId Facade → Connector → Client → HTTP → SCIM server`

這是非常好的學習素材。

---

# 一個實際流程：搜尋與建立使用者（幫助你腦中畫流程圖）

## Search User（`ConnectorFacade.search(ObjectClass.ACCOUNT, ...)`）

1. ConnId 呼叫 `executeQuery` → 進到 `AbstractSCIMConnector.executeQuery(...)`
2. `executeQuery` 判斷 `ObjectClass` 與 `Filter`
    - 無 filter：呼叫 `client.getAllUsers(...)`
    - 有 filter：組成 SCIM filter string，仍呼叫 `client.getAllUsers(filter, ...)`
3. `SCIMv2Client.getAllUsers`：
    - 使用 `AbstractSCIMService.getWebclient("Users", params)`
    - → `doGetAllUsers`
    - → HTTP `GET /Users?...`
4. 回傳 `PagedResults<SCIMv2User>`
5. 每個 `SCIMv2User` 經 `fromUser(...)` 轉成 `ConnectorObject` 交給 `ResultsHandler`

## Create User（`ConnectorFacade.create(ObjectClass.ACCOUNT, attrs, ...)`）

1. 進到 `AbstractSCIMConnector.create(...)`
2. 用 `AttributesAccessor` 抽出：
    - `userName`, `password`, `groups`, `entitlements`, enterprise user 等
3. 建 `SCIMv2User`：
    - 填入基本欄位與 custom / enterprise 欄位
4. 呼叫 `client.createUser(user)`：
    - `SCIMv2Client.createUser`
    - → `AbstractSCIMService.doCreate`
    - → HTTP `POST /Users`
5. 回應 body 取 `id`，設回 `user.setId(...)`
6. `create` 回傳 `Uid(user.getId())` 給 ConnId

---

# 建議你「學習時」的閱讀順序

你可以依序打開以下檔案閱讀，並自己畫筆記或流程圖：

## 1) 先掌握高層：Connector 角色

- 看 `SCIMv2Connector`
    - 了解這個 class 如何被 ConnId 作為 connector 使用
    - 重點：`@ConnectorClass`、`extends AbstractSCIMConnector`

## 2) 看 `AbstractSCIMConnector`

重點看：

- `init`, `dispose`, `getConfiguration`
- `executeQuery`, `create`, `update`, `updateDelta`, `delete`, `test`
- `fromUser`, `fromGroup`
- 抽象方法：`buildNewUserEntity`, `buildNewGroupEntity`, `build*Patch*`

## 3) 看資料模型與 Schema 映射

- `SCIMAttributeUtils`
    - 了解 ConnId 的 attribute 名稱（上層看到的 key）怎樣對應到 SCIM JSON 欄位
- `SCIMUser`, `SCIMGroup`（`common.dto`）
- `SCIMv2User`, `SCIMv2Group`（`v2.dto`）
- 重點方法：
    - `fillSCIMCustomAttributes`, `fillEnterpriseUser`
    - `entitlementsToAttribute`, `toAttributes`

## 4) 看 HTTP client 與錯誤處理

- `AbstractSCIMService`
    - `getWebclient`, `doGet`, `doCreate`, `doUpdate`, `doUpdatePatch`
    - `doDeleteUser`, `doDeleteGroup`
    - `getBearerToken`, `invokeWithLogging`, `executeAndRetry`, `checkServiceErrors`
- `SCIMv2Client`
    - 確認每個 public method 如何包裝 `AbstractSCIMService`

## 5) 看設定與 provider 差異

- `SCIMConnectorConfiguration`
    - 把欄位與 `validate()` 看過一輪（了解可調整項）
- `SCIMProvider` + `SCIMv2Connector` 中與 provider 相關的 patch 建構邏輯
    - 理解不同 SCIM server 的特別處理

## 6) 最後用測試把整條線路串起來

- 打開 `SCIMv2ConnectorTests`
    - 先看 `setUpConf`：怎麼組 configuration
    - 再看 `crudUser`, `crudGroup`, `search`：對照前面程式碼把整個呼叫路徑在腦中走一次

呼叫路徑總結：

`Test → ConnectorFacade → SCIMv2Connector / AbstractSCIMConnector → SCIMv2Client / AbstractSCIMService → HTTP → SCIM server → 回傳 → DTO → ConnId ConnectorObject`