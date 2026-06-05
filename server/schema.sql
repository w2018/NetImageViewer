-- ============================================
-- 网络图集 SQLite3 数据库结构
-- ============================================

-- 一级：分类表
CREATE TABLE IF NOT EXISTS t_classify (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    title       TEXT    NOT NULL,              -- 分类名称（风景图集、动漫图集…）
    cover       TEXT    NOT NULL,              -- 封面图URL
    sort_order  INTEGER DEFAULT 0,            -- 排序
    status      INTEGER DEFAULT 1,            -- 1=启用 0=禁用
    created_at  TEXT    DEFAULT (datetime('now','localtime')),
    updated_at  TEXT    DEFAULT (datetime('now','localtime'))
);

-- 二级：章节表
CREATE TABLE IF NOT EXISTS t_chapter (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    classify_id INTEGER NOT NULL,             -- 所属分类ID → t_classify.id
    title       TEXT    NOT NULL,              -- 章节名称
    cover       TEXT    NOT NULL,              -- 封面图URL
    page_count  INTEGER DEFAULT 0,            -- 图片数量
    sort_order  INTEGER DEFAULT 0,            -- 排序
    status      INTEGER DEFAULT 1,
    created_at  TEXT    DEFAULT (datetime('now','localtime')),
    updated_at  TEXT    DEFAULT (datetime('now','localtime')),
    FOREIGN KEY (classify_id) REFERENCES t_classify(id)
);

-- 三级：图片表
CREATE TABLE IF NOT EXISTS t_picture (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    chapter_id  INTEGER NOT NULL,             -- 所属章节ID → t_chapter.id
    url         TEXT    NOT NULL,              -- 图片URL
    thumb_url   TEXT,                          -- 缩略图URL（可选）
    sort_order  INTEGER DEFAULT 0,            -- 图片顺序
    width       INTEGER,                       -- 图片宽度
    height      INTEGER,                       -- 图片高度
    file_size   INTEGER,                       -- 文件大小(byte)
    source      TEXT,                          -- 采集来源（unsplash, picsum...）
    source_id   TEXT,                          -- 原始ID
    md5_hash    TEXT,                          -- 图片去重用
    status      INTEGER DEFAULT 1,
    created_at  TEXT    DEFAULT (datetime('now','localtime')),
    FOREIGN KEY (chapter_id) REFERENCES t_chapter(id)
);

-- 索引优化
CREATE INDEX IF NOT EXISTS idx_chapter_classify ON t_chapter(classify_id);
CREATE INDEX IF NOT EXISTS idx_picture_chapter  ON t_picture(chapter_id);
CREATE INDEX IF NOT EXISTS idx_picture_md5      ON t_picture(md5_hash);
CREATE INDEX IF NOT EXISTS idx_picture_source   ON t_picture(source, source_id);

-- 外键约束
PRAGMA foreign_keys = ON;
