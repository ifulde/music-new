-- 音乐网站数据库表结构
-- 请在使用前确认数据库 musicai 已创建
-- USE musicai;

-- 如果表已存在，可以使用以下命令删除（谨慎操作）
-- DROP TABLE IF EXISTS playlist_songs;
-- DROP TABLE IF EXISTS playlists;
-- DROP TABLE IF EXISTS play_history;
-- DROP TABLE IF EXISTS favorites;
-- DROP TABLE IF EXISTS songs;
-- DROP TABLE IF EXISTS users;

-- 1. users 表（如果不存在）
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(500),
    bio TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. songs 表（如果不存在）
CREATE TABLE IF NOT EXISTS songs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    artist VARCHAR(255) NOT NULL,
    album VARCHAR(255),
    duration INT DEFAULT 0 COMMENT '时长（秒）',
    file_path VARCHAR(500) NOT NULL COMMENT '音频文件路径',
    cover_path VARCHAR(500) COMMENT '封面图片路径',
    play_count INT DEFAULT 0 COMMENT '播放次数',
    uploader_id BIGINT NOT NULL COMMENT '上传者ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_uploader_id (uploader_id),
    INDEX idx_title (title),
    INDEX idx_artist (artist),
    FOREIGN KEY (uploader_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 如果 songs 表已存在但没有 uploader_id 字段，执行以下命令添加
-- ALTER TABLE songs ADD COLUMN IF NOT EXISTS uploader_id BIGINT NOT NULL AFTER play_count;
-- ALTER TABLE songs ADD INDEX IF NOT EXISTS idx_uploader_id (uploader_id);
-- ALTER TABLE songs ADD FOREIGN KEY IF NOT EXISTS (uploader_id) REFERENCES users(id) ON DELETE CASCADE;

-- 3. playlists 表（播放列表）
CREATE TABLE IF NOT EXISTS playlists (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    user_id BIGINT NOT NULL COMMENT '创建者ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. playlist_songs 表（播放列表和歌曲关联表）
CREATE TABLE IF NOT EXISTS playlist_songs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    playlist_id BIGINT NOT NULL,
    song_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_playlist_song (playlist_id, song_id),
    INDEX idx_playlist_id (playlist_id),
    INDEX idx_song_id (song_id),
    FOREIGN KEY (playlist_id) REFERENCES playlists(id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. play_history 表（播放历史）
CREATE TABLE IF NOT EXISTS play_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    song_id BIGINT NOT NULL,
    played_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_song_id (song_id),
    INDEX idx_played_at (played_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. favorites 表（收藏表，接口中未使用但表已存在）
CREATE TABLE IF NOT EXISTS favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    songs_id BIGINT NOT NULL COMMENT '歌曲ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_song (user_id, songs_id),
    INDEX idx_user_id (user_id),
    INDEX idx_songs_id (songs_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (songs_id) REFERENCES songs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 查看所有表
SHOW TABLES;

-- 查看表结构（示例）
-- DESCRIBE users;
-- DESCRIBE songs;
-- DESCRIBE playlists;
-- DESCRIBE playlist_songs;
-- DESCRIBE play_history;
-- DESCRIBE favorites;

