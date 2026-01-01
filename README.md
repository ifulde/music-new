__# 音乐网站后端 API 测试文档

## 项目信息

- **项目名称**: MusicAI
- **技术栈**: Spring Boot 3.2.0 + MyBatis + MySQL + JWT
- **基础URL**: `http://localhost:8080`
- **默认端口**: 8080

## 认证说明

大部分接口需要JWT认证，需要在请求头中添加：
```
Authorization: Bearer <token>
```

获取token的方式：调用登录接口获取。

---

## 1. 用户管理接口

### 1.1 用户注册

**接口**: `POST /api/register`

**请求头**:
```
Content-Type: application/json
```

**请求体**:
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "123456"
}
```

**响应示例** (201 Created):
```json
{
  "success": true,
  "message": "注册成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "avatarUrl": null,
    "bio": null,
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

---

### 1.2 用户登录

**接口**: `POST /api/login`

**请求头**:
```
Content-Type: application/json
```

**请求体**:
```json
{
  "username": "testuser",
  "password": "123456"
}
```

**响应示例** (200 OK):
```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcm5hbWUiOiJ0ZXN0dXNlciIsImlhdCI6MTcwNDA2NDgwMCwiZXhwIjoxNzA0MTUxMjAwfQ...",
    "user": {
      "id": 1,
      "username": "testuser",
      "email": "test@example.com",
      "avatarUrl": null,
      "bio": null,
      "createdAt": "2024-01-01T10:00:00"
    }
  }
}
```

**注意**: 请保存返回的`token`，后续接口需要使用。

---

### 1.3 获取当前用户信息

**接口**: `GET /api/user`

**请求头**:
```
Authorization: Bearer <your_token>
```

**响应示例** (200 OK):
```json
{
  "success": true,
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "avatarUrl": null,
    "bio": null,
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

---

## 2. 音乐管理接口

### 2.1 上传音乐

**接口**: `POST /api/songs`

**请求头**:
```
Authorization: Bearer <your_token>
Content-Type: multipart/form-data
```

**请求体** (form-data):
- `audio` (file, 必填): 音频文件，支持mp3、flac格式
- `cover` (file, 可选): 封面图片，支持jpg、jpeg、png格式
- `title` (text, 必填): 歌曲标题
- `artist` (text, 必填): 艺术家
- `album` (text, 可选): 专辑名

**Postman设置**:
1. 选择请求方法: POST
2. Body标签页选择: form-data
3. 添加字段:
   - `audio`: 类型选择File，选择音频文件
   - `cover`: 类型选择File，选择图片文件（可选）
   - `title`: 类型选择Text，输入"测试歌曲"
   - `artist`: 类型选择Text，输入"测试艺术家"
   - `album`: 类型选择Text，输入"测试专辑"（可选）

**响应示例** (201 Created):
```json
{
  "success": true,
  "message": "上传成功",
  "data": {
    "id": 1,
    "title": "测试歌曲",
    "artist": "测试艺术家",
    "album": "测试专辑",
    "duration": 0,
    "coverPath": "uuid-generated-name.jpg",
    "playCount": 0,
    "uploaderId": 1,
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

---

### 2.2 获取歌曲列表

**接口**: `GET /api/songs`

**查询参数**:
- `page` (可选): 页码，默认1
- `limit` (可选): 每页数量，默认20
- `search` (可选): 搜索关键词，在标题、艺术家、专辑中搜索

**请求示例**:
```
GET http://localhost:8080/api/songs?page=1&limit=20&search=测试
```

**响应示例** (200 OK):
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "title": "测试歌曲",
        "artist": "测试艺术家",
        "album": "测试专辑",
        "duration": 180,
        "coverPath": "cover.jpg",
        "playCount": 10,
        "uploaderId": 1,
        "createdAt": "2024-01-01T10:00:00"
      }
    ],
    "page": 1,
    "limit": 20,
    "total": 1,
    "totalPages": 1
  }
}
```

---

### 2.3 获取单个歌曲信息

**接口**: `GET /api/songs/{id}`

**请求示例**:
```
GET http://localhost:8080/api/songs/1
```

**响应示例** (200 OK):
```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "测试歌曲",
    "artist": "测试艺术家",
    "album": "测试专辑",
    "duration": 180,
    "coverPath": "cover.jpg",
    "playCount": 10,
    "uploaderId": 1,
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

---

### 2.4 更新歌曲信息

**接口**: `PUT /api/songs/{id}`

**请求头**:
```
Authorization: Bearer <your_token>
Content-Type: application/json
```

**请求体**:
```json
{
  "title": "更新后的标题",
  "artist": "更新后的艺术家",
  "album": "更新后的专辑"
}
```

**请求示例**:
```
PUT http://localhost:8080/api/songs/1
```

**响应示例** (200 OK):
```json
{
  "success": true,
  "message": "更新成功",
  "data": {
    "id": 1,
    "title": "更新后的标题",
    "artist": "更新后的艺术家",
    "album": "更新后的专辑",
    "duration": 180,
    "coverPath": "cover.jpg",
    "playCount": 10,
    "uploaderId": 1,
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

**注意**: 只有上传者可以更新歌曲信息。

---

### 2.5 删除歌曲

**接口**: `DELETE /api/songs/{id}`

**请求头**:
```
Authorization: Bearer <your_token>
```

**请求示例**:
```
DELETE http://localhost:8080/api/songs/1
```

**响应**: 204 No Content（无响应体）

**注意**: 只有上传者可以删除歌曲。

---

### 2.6 获取音频流

**接口**: `GET /api/songs/stream/{songId}`

**请求头**:
```
Authorization: Bearer <your_token>
Range: bytes=0-1024 (可选，用于断点续传)
```

**请求示例**:
```
GET http://localhost:8080/api/songs/stream/1
```

**响应**: 
- Content-Type: audio/mpeg 或 audio/flac
- Accept-Ranges: bytes
- 返回音频文件流

**Postman测试**:
- 在Postman中可以直接下载或播放音频文件
- 设置Range头可以实现断点续传

---

## 3. 播放列表管理接口

### 3.1 创建播放列表

**接口**: `POST /api/playlists`

**请求头**:
```
Authorization: Bearer <your_token>
Content-Type: application/json
```

**请求体**:
```json
{
  "name": "我的播放列表",
  "description": "这是一个测试播放列表"
}
```

**响应示例** (201 Created):
```json
{
  "success": true,
  "message": "创建成功",
  "data": {
    "id": 1,
    "name": "我的播放列表",
    "description": "这是一个测试播放列表",
    "userId": 1,
    "createdAt": "2024-01-01T10:00:00",
    "songs": []
  }
}
```

---

### 3.2 获取用户的播放列表

**接口**: `GET /api/playlists`

**请求头**:
```
Authorization: Bearer <your_token>
```

**响应示例** (200 OK):
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "我的播放列表",
      "description": "这是一个测试播放列表",
      "userId": 1,
      "createdAt": "2024-01-01T10:00:00",
      "songs": []
    }
  ]
}
```

---

### 3.3 获取单个播放列表详情

**接口**: `GET /api/playlists/{id}`

**请求头**:
```
Authorization: Bearer <your_token>
```

**请求示例**:
```
GET http://localhost:8080/api/playlists/1
```

**响应示例** (200 OK):
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "我的播放列表",
    "description": "这是一个测试播放列表",
    "userId": 1,
    "createdAt": "2024-01-01T10:00:00",
    "songs": [
      {
        "id": 1,
        "title": "测试歌曲",
        "artist": "测试艺术家",
        "album": "测试专辑",
        "duration": 180,
        "coverPath": "cover.jpg",
        "playCount": 10,
        "uploaderId": 1,
        "createdAt": "2024-01-01T10:00:00"
      }
    ]
  }
}
```

---

### 3.4 更新播放列表信息

**接口**: `PUT /api/playlists/{id}`

**请求头**:
```
Authorization: Bearer <your_token>
Content-Type: application/json
```

**请求体**:
```json
{
  "name": "更新后的播放列表名",
  "description": "更新后的描述"
}
```

**请求示例**:
```
PUT http://localhost:8080/api/playlists/1
```

**响应示例** (200 OK):
```json
{
  "success": true,
  "message": "更新成功",
  "data": {
    "id": 1,
    "name": "更新后的播放列表名",
    "description": "更新后的描述",
    "userId": 1,
    "createdAt": "2024-01-01T10:00:00",
    "songs": []
  }
}
```

---

### 3.5 删除播放列表

**接口**: `DELETE /api/playlists/{id}`

**请求头**:
```
Authorization: Bearer <your_token>
```

**请求示例**:
```
DELETE http://localhost:8080/api/playlists/1
```

**响应**: 204 No Content（无响应体）

---

### 3.6 向播放列表添加歌曲

**接口**: `POST /api/playlists/{playlistId}/songs`

**请求头**:
```
Authorization: Bearer <your_token>
Content-Type: application/json
```

**请求体**:
```json
{
  "songId": 1
}
```

**请求示例**:
```
POST http://localhost:8080/api/playlists/1/songs
```

**响应示例** (200 OK):
```json
{
  "success": true,
  "message": "添加成功",
  "data": [
    {
      "id": 1,
      "title": "测试歌曲",
      "artist": "测试艺术家",
      "album": "测试专辑",
      "duration": 180,
      "coverPath": "cover.jpg",
      "playCount": 10,
      "uploaderId": 1,
      "createdAt": "2024-01-01T10:00:00"
    }
  ]
}
```

---

### 3.7 从播放列表移除歌曲

**接口**: `DELETE /api/playlists/{playlistId}/songs/{songId}`

**请求头**:
```
Authorization: Bearer <your_token>
```

**请求示例**:
```
DELETE http://localhost:8080/api/playlists/1/songs/1
```

**响应示例** (200 OK):
```json
{
  "success": true,
  "message": "移除成功",
  "data": []
}
```

---

## 4. 播放历史接口

### 4.1 记录播放历史

**接口**: `POST /api/history`

**请求头**:
```
Authorization: Bearer <your_token>
Content-Type: application/json
```

**请求体**:
```json
{
  "songId": 1,
  "playedAt": "2024-01-01T10:00:00"
}
```

**注意**: `playedAt`字段可选，如果不提供则使用服务器当前时间。

**响应示例** (201 Created):
```json
{
  "success": true,
  "message": "记录成功",
  "data": {
    "id": 1,
    "userId": 1,
    "songId": 1,
    "playedAt": "2024-01-01T10:00:00",
    "song": {
      "id": 1,
      "title": "测试歌曲",
      "artist": "测试艺术家",
      "album": "测试专辑",
      "duration": 180,
      "coverPath": "cover.jpg",
      "playCount": 11,
      "uploaderId": 1,
      "createdAt": "2024-01-01T10:00:00"
    }
  }
}
```

**注意**: 记录播放历史会自动增加歌曲的播放次数。

---

### 4.2 获取播放历史

**接口**: `GET /api/history`

**请求头**:
```
Authorization: Bearer <your_token>
```

**查询参数**:
- `page` (可选): 页码，默认1
- `limit` (可选): 每页数量，默认20

**请求示例**:
```
GET http://localhost:8080/api/history?page=1&limit=20
```

**响应示例** (200 OK):
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "userId": 1,
        "songId": 1,
        "playedAt": "2024-01-01T10:00:00",
        "song": {
          "id": 1,
          "title": "测试歌曲",
          "artist": "测试艺术家",
          "album": "测试专辑",
          "duration": 180,
          "coverPath": "cover.jpg",
          "playCount": 11,
          "uploaderId": 1,
          "createdAt": "2024-01-01T10:00:00"
        }
      }
    ],
    "page": 1,
    "limit": 20,
    "total": 1,
    "totalPages": 1
  }
}
```

---

## Postman 使用技巧

### 1. 环境变量设置

在Postman中创建环境变量，方便管理：

**变量名**: `base_url`
**初始值**: `http://localhost:8080`

**变量名**: `token`
**初始值**: (留空，登录后手动设置)

在请求URL中使用: `{{base_url}}/api/login`

### 2. 自动设置Token

创建登录请求后，可以在Tests标签页添加脚本自动保存token：

```javascript
if (pm.response.code === 200) {
    var jsonData = pm.response.json();
    if (jsonData.success && jsonData.data.token) {
        pm.environment.set("token", jsonData.data.token);
    }
}
```

然后在其他请求的Headers中使用：
```
Authorization: Bearer {{token}}
```

### 3. 文件上传测试

1. 选择POST请求方法
2. Body标签页选择 `form-data`
3. 对于文件字段，类型选择 `File`
4. 选择本地文件
5. 对于文本字段，类型选择 `Text`

### 4. 测试流程建议

1. **第一步**: 注册新用户 (`POST /api/register`)
2. **第二步**: 登录获取token (`POST /api/login`)
3. **第三步**: 上传歌曲 (`POST /api/songs`)
4. **第四步**: 获取歌曲列表 (`GET /api/songs`)
5. **第五步**: 创建播放列表 (`POST /api/playlists`)
6. **第六步**: 添加歌曲到播放列表 (`POST /api/playlists/{id}/songs`)
7. **第七步**: 记录播放历史 (`POST /api/history`)
8. **第八步**: 获取播放历史 (`GET /api/history`)
9. **第九步**: 获取推荐歌曲 (`POST /api/recommend`)

---

## 5. 推荐接口

### 5.1 获取推荐歌曲

**接口**: `POST /api/recommend`

**请求头**:
```
Content-Type: application/json
```

**请求体**:
```json
{
    "userHistorySeq": [6092057, 2093200, 2169359, 4914989, 6804216, 5916928, 141783, 2737135, 188179, 2737135]
}
```

**请求体字段说明**:
- `userHistorySeq` (Array, 必填): 用户历史播放的歌曲ID列表

**请求示例**:
```
POST http://localhost:8080/api/recommend
```

**响应示例** (200 OK):
```json
{
    "code": 200,
    "message": "获取推荐成功",
    "data": [805980, 8938377, 127989, 8798307, 8171569]
}
```

**响应字段说明**:
- `code` (Integer): 响应状态码，200表示成功
- `message` (String): 响应消息
- `data` (Array<Long>): 推荐的歌曲ID列表

**Postman测试步骤**:
1. 创建POST请求到：`http://localhost:8080/api/recommend`
2. 添加Header：`Content-Type: application/json`
3. 在Body中选择raw，格式选择JSON
4. 输入请求体数据
5. 点击Send发送请求

**请求体示例**:

**示例1：基本推荐请求**
```json
{
    "userHistorySeq": [6092057, 2093200, 2169359, 4914989, 6804216, 5916928, 141783, 2737135, 188179, 2737135]
}
```

**示例2：单个歌曲ID**
```json
{
    "userHistorySeq": [6092057]
}
```

**示例3：大量歌曲ID**
```json
{
    "userHistorySeq": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25]
}
```

**示例4：自定义参数**
```json
{
    "userId": "user123",
    "userHistorySeq": [6092057, 2093200, 2169359],
    "topK": 10
}
```

**工作流程**:
1. 前端发送POST请求到 `/api/recommend`，携带JSON数据和 `Content-Type: application/json` 请求头
2. 后端接收请求并将请求体原样转发到推荐服务器 `http://10.45.17.84:80/api/v1/recommend`
3. 推荐服务器返回推荐结果
4. 后端解析响应，提取 `data.topKItem` 字段（推荐的歌曲ID列表）
5. 将推荐的歌曲ID列表返回给前端

**注意**:
- 该接口不需要JWT认证
- 请求体会原样转发到推荐服务器，可以包含任意JSON数据
- 推荐服务器的响应格式为：
```json
{
    "code": 200,
    "data": {
        "recommendType": "personalized",
        "topKCount": 5,
        "topKItem": [歌曲ID列表],
        "topKScore": [评分列表],
        "userId": "unknown_user"
    },
    "msg": "success",
    "requestId": "req_407342776"
}
```
- 后端只提取并返回 `data.topKItem` 字段给前端

---

## Postman 使用技巧常见错误码

- **200 OK**: 请求成功
- **201 Created**: 创建成功
- **204 No Content**: 删除成功，无响应体
- **400 Bad Request**: 请求参数错误
- **401 Unauthorized**: 未提供或无效的令牌
- **403 Forbidden**: 没有权限（如非上传者尝试修改歌曲）
- **404 Not Found**: 资源不存在
- **500 Internal Server Error**: 服务器内部错误

---

## 注意事项

1. 所有需要认证的接口都必须在请求头中携带有效的JWT token
2. 文件上传大小限制为100MB
3. 音频文件格式仅支持mp3和flac
4. 封面图片格式支持jpg、jpeg、png
5. 只有上传者可以更新和删除自己上传的歌曲
6. 只有播放列表的所有者可以管理播放列表
7. 记录播放历史会自动增加歌曲的播放次数

