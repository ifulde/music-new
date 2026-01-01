# 故障排查指南

## 如何查看详细错误信息

当遇到"服务器内部错误"时，请按以下步骤排查：

### 1. 查看控制台日志

服务器内部错误的详细信息会在控制台（Console/Terminal）中打印出来。请查看：
- IDE的运行控制台
- 或者启动应用时的Terminal/命令行窗口

错误信息会以堆栈跟踪（Stack Trace）的形式显示，包含：
- 异常类型
- 错误消息
- 出错的代码位置

### 2. 查看详细错误响应

我已经更新了异常处理器，现在API响应中会包含详细的错误信息，格式如下：

```json
{
  "success": false,
  "message": "服务器内部错误: 具体错误信息",
  "data": null
}
```

## 常见问题及解决方法

### 问题1: 数据库连接失败

**错误信息**: 
- `Communications link failure`
- `Access denied for user`
- `Unknown database 'musicai'`

**解决方法**:
1. 检查`application.properties`中的数据库配置
2. 确认MySQL服务已启动
3. 确认数据库`musicai`已创建
4. 确认用户名和密码正确
5. 确认端口号正确（默认3306）

**检查命令**:
```sql
-- 连接到MySQL后执行
SHOW DATABASES;  -- 查看是否存在musicai数据库
USE musicai;     -- 切换到musicai数据库
SHOW TABLES;     -- 查看表是否存在
```

---

### 问题2: 数据表不存在

**错误信息**: 
- `Table 'musicai.playlists' doesn't exist`
- `Table 'musicai.playlist_songs' doesn't exist`
- `Table 'musicai.songs' doesn't exist`

**解决方法**: 创建缺失的数据表

根据代码要求，需要以下表：

#### playlists 表
```sql
CREATE TABLE playlists (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    INDEX idx_user_id (user_id)
);
```

#### playlist_songs 表
```sql
CREATE TABLE playlist_songs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    playlist_id BIGINT NOT NULL,
    song_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    UNIQUE KEY uk_playlist_song (playlist_id, song_id),
    INDEX idx_playlist_id (playlist_id),
    INDEX idx_song_id (song_id)
);
```

---

### 问题3: 字段不存在

**错误信息**: 
- `Unknown column 'uploader_id' in 'field list'`
- `Unknown column 'xxx' in 'field list'`

**解决方法**: 在对应的表中添加缺失的字段

#### songs 表需要 uploader_id 字段
```sql
ALTER TABLE songs ADD COLUMN uploader_id BIGINT NOT NULL AFTER play_count;
```

---

### 问题4: MyBatis Mapper找不到

**错误信息**: 
- `Invalid bound statement (not found)`
- `Could not find resource mapper/XXXMapper.xml`

**解决方法**:
1. 确认`src/main/resources/mapper/`目录下存在对应的XML文件
2. 确认`application.properties`中配置了正确的路径：
   ```properties
   mybatis.mapper-locations=classpath:mapper/*.xml
   ```
3. 确认XML文件中的`namespace`与Mapper接口的包名一致

---

### 问题5: JWT Token相关错误

**错误信息**: 
- `SignatureException`
- `ExpiredJwtException`
- `MalformedJwtException`

**解决方法**:
1. 确认JWT secret配置正确（`application.properties`中的`jwt.secret`）
2. 确认token格式正确：`Bearer <token>`
3. 确认token未过期（默认24小时）
4. 重新登录获取新的token

---

### 问题6: 文件上传失败

**错误信息**: 
- `FileNotFoundException`
- `Access Denied`
- `Maximum upload size exceeded`

**解决方法**:
1. 确认文件上传目录有写入权限
2. 确认文件大小未超过限制（默认100MB）
3. 确认文件格式支持（音频：mp3/flac，图片：jpg/jpeg/png）
4. 检查`application.properties`中的文件路径配置

---

### 问题7: 密码加密错误

**错误信息**: 
- `BCryptPasswordEncoder`相关错误
- `NoSuchMethodError`

**解决方法**:
1. 确认`spring-security-crypto`依赖已正确添加
2. 刷新Maven依赖：右键项目 -> Maven -> Reload Project
3. 确认Spring Security版本兼容

---

## 调试步骤

1. **查看控制台日志**
   - 找到完整的错误堆栈
   - 查看最后一个`Caused by`语句

2. **检查数据库**
   - 确认数据库已创建
   - 确认所有必需的表已创建
   - 确认表结构正确（字段名、类型）

3. **检查配置文件**
   - `application.properties`中的配置是否正确
   - 数据库连接信息
   - MyBatis配置
   - JWT配置

4. **检查依赖**
   - Maven依赖是否全部下载成功
   - 是否有版本冲突

5. **逐步测试**
   - 先测试最简单的接口（如注册、登录）
   - 确认数据库连接正常
   - 再测试复杂接口

## 获取帮助

如果问题仍然存在，请提供以下信息：

1. **完整的错误堆栈**（从控制台复制）
2. **API请求信息**：
   - 请求URL
   - 请求方法
   - 请求头
   - 请求体
3. **数据库信息**：
   - 表结构（`DESCRIBE table_name;`）
   - 是否有测试数据
4. **配置文件**：
   - `application.properties`（隐藏敏感信息）

---

## 快速检查清单

- [ ] MySQL服务已启动
- [ ] 数据库`musicai`已创建
- [ ] 所有必需的表已创建（users, songs, playlists, playlist_songs, play_history, favorites）
- [ ] `songs`表有`uploader_id`字段
- [ ] `application.properties`配置正确
- [ ] Maven依赖已下载
- [ ] 文件上传目录有写入权限
- [ ] 服务器端口8080未被占用

