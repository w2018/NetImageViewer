# 图图 (NetImageViewer)

**本项目全程使用Operit AI开发生成。** 图图是一款基于Android平台的网络图片浏览应用，支持多级分类浏览、章节列表、全屏图片查看、浏览历史、收藏管理、缓存清理等功能，为用户提供流畅的在线图库浏览体验。

## ✨ 功能特性

- 🏠 **首页分类浏览** — 一级分类列表，快速定位感兴趣的内容
- 📑 **章节列表** — 分类下的章节浏览，支持下拉刷新
- 🖼️ **全屏图片查看** — 支持手势缩放、拖拽的全屏图片浏览模式，沉浸式体验
- 💾 **本地缓存** — 基于 Room 的离线数据缓存（分类/章节/图片三级缓存），提升加载速度
- 🌐 **网络请求** — Retrofit + OkHttp 构建的高效网络层
- ⚙️ **设置页面** — 自定义API Base URL，支持保存、重置和连接测试（OkHttp HEAD请求检测）
- 🗑️ **缓存管理** — 显示应用缓存大小，支持一键清理缓存
- 📚 **浏览历史** — 自动记录浏览过的章节，按时间倒序排列，支持清空
- ⭐ **我的收藏** — 收藏喜欢的章节，支持查看、跳转和清空
- 📝 **帮助页面** — 应用使用说明（数据库架构说明 + API接口说明）
- ℹ️ **关于页面** — 显示版本信息、作者、GitHub地址、技术栈、开源许可
- 🧭 **侧滑菜单** — 主界面Toolbar菜单入口，快速访问设置/缓存/历史收藏/帮助/关于

## 🛠️ 技术栈

| 类别 | 技术 |
|------|------|
| 语言 | Java 8 |
| 构建工具 | Gradle 8.9 (Kotlin DSL) + AGP 8.7.3 |
| 最低/目标 SDK | API 24 (Android 7.0) / API 34 (Android 14) |
| 网络请求 | Retrofit 2.9.0 + OkHttp 4.12.0 + Gson |
| 图片加载 | Glide 4.16.0 + PhotoView 2.3.0 |
| 本地数据库 | Room 2.6.1（缓存 + 浏览历史 + 收藏夹） |
| UI 框架 | Material Design 1.11.0 + AndroidX + ViewBinding |
| 架构组件 | ViewModel + LiveData + Lifecycle |
| 页面导航 | ViewPager2 + TabLayout（历史/收藏双Tab） |
| 自动构建 | GitHub Actions CI（`assembleRelease`） |

## 📦 项目结构

```
app/src/main/java/com/operit/netimageviewer/
├── data/
│   ├── model/                # 数据模型（API响应、图片项等）
│   ├── local/                # Room 本地数据源
│   │   ├── AppDatabase.java           # 数据库入口（三级缓存 + 浏览历史 + 收藏）
│   │   ├── ClassifyCacheEntity.java   # 分类缓存实体
│   │   ├── ChapterCacheEntity.java    # 章节缓存实体
│   │   ├── PictureCacheEntity.java    # 图片缓存实体
│   │   ├── BrowseHistoryEntity.java   # 浏览历史实体
│   │   ├── BrowseHistoryDao.java      # 浏览历史 DAO
│   │   ├── FavoriteEntity.java        # 收藏实体
│   │   ├── FavoriteDao.java           # 收藏 DAO
│   │   ├── ApiConfigManager.java      # API 地址配置管理器（SharedPreferences）
│   │   └── ...Dao.java               # 各缓存 DAO
│   ├── remote/                # Retrofit 远程数据源
│   │   └── RetrofitClient.java        # Retrofit 单例（支持动态更新 Base URL）
│   └── repository/           # 数据仓库层
├── ui/
│   ├── home/                 # 首页（一级分类列表，支持下拉刷新）
│   │   ├── HomeActivity.java
│   │   ├── HomeAdapter.java
│   │   └── HomeViewModel.java
│   ├── chapter/              # 章节列表页
│   │   ├── ChapterActivity.java
│   │   ├── ChapterAdapter.java
│   │   └── ChapterViewModel.java
│   ├── picture/              # 图片浏览页（ViewPager2 + PhotoView 沉浸式全屏）
│   │   ├── PictureActivity.java
│   │   ├── PicturePagerAdapter.java
│   │   ├── PictureViewModel.java
│   │   └── PictureFragment.java
│   ├── settings/             # 设置页面（自定义 API 地址）
│   │   └── SettingsActivity.java
│   ├── cache/                # 缓存管理页面（显示/清理缓存）
│   │   └── CacheManagerActivity.java
│   ├── help/                 # 帮助页面
│   │   └── HelpActivity.java
│   ├── about/                # 关于页面
│   │   └── AboutActivity.java
│   └── history/              # 历史与收藏页面（ViewPager2 + TabLayout）
│       ├── HistoryFavoriteActivity.java
│       ├── HistoryFavoritePagerAdapter.java
│       ├── HistoryFragment.java        # 浏览历史 Tab
│       ├── FavoriteFragment.java       # 我的收藏 Tab
│       └── HistoryFavoriteAdapter.java # 通用列表适配器
├── util/                     # 工具类
└── MyAppGlideModule.java     # Glide 自定义模块

res/
├── layout/                   # 13个布局文件
│   ├── activity_home.xml
│   ├── activity_chapter.xml
│   ├── activity_picture.xml
│   ├── activity_settings.xml          # 设置布局（URL输入+保存/重置/测试按钮）
│   ├── activity_help.xml              # 帮助布局
│   ├── activity_about.xml             # 关于布局
│   ├── activity_cache_manager.xml     # 缓存管理布局
│   ├── activity_history_favorite.xml  # 历史与收藏布局
│   ├── fragment_history_list.xml      # 历史/收藏列表 Fragment 布局
│   └── item_*.xml                     # 各列表项布局
├── menu/
│   ├── menu_main.xml          # 主界面菜单（设置/缓存/历史收藏/帮助/关于）
│   └── menu_picture.xml       # 图片页菜单（收藏按钮）
└── values/
    └── strings.xml            # 全部字符串资源
```

## 🚀 构建运行

```bash
# 克隆仓库
git clone https://github.com/w2018/NetImageViewer.git
cd NetImageViewer

# 调试构建
./gradlew assembleDebug

# 安装到设备
adb install app/build/outputs/apk/debug/app-debug.apk

# Release 构建（自动签名需配置 keystore）
./gradlew assembleRelease
```

## 📋 版本历史

| 版本 | 说明 |
|------|------|
| v2.1.0 | 新增设置/帮助/关于/缓存管理/历史收藏页面，ViewPager2+TabLayout双Tab，补全 strings.xml 修复 CI 构建 |
| v2.0.0 | 新增首页下拉刷新、Glide 占位图配置、章节预加载功能、网络错误 Snackbar 重试 |
| v1.1.1 | 修复 GitHub Actions CI 构建的 AAPT2 路径问题 |
| v1.1.0 | 取消首页和章节页标题栏，优化全屏浏览体验 |
| v1.0.0 | 初始版本：分类浏览、章节列表、图片查看 |

## 🌐 API 接口 (api.php)

服务端接口文件 `api.php` 为 App 提供 RESTful JSON 数据，部署在 PHP 7.1+ 环境，需启用 `php-sqlite3` 扩展。

### 接口列表

| 接口 | 方法 | 参数 | 说明 |
|------|------|------|------|
| `/api.php?action=classifyList` | GET | — | 获取一级分类列表 |
| `/api.php?action=chapterList` | GET | `id` (分类ID) | 获取分类下的二级章节列表 |
| `/api.php?action=pictureList` | GET | `id` (章节ID) | 获取章节下的三级图片列表 |
| `/api.php?action=stats` | GET | — | 获取数据库统计信息 |

> 💡 App 设置页面支持自定义 API Base URL，可将服务端部署到任意地址后，在 App 内配置即可使用。

### 响应格式

**成功响应：**
```json
{
    "code": 200,
    "msg": "success",
    "data": [...]
}
```

**错误响应：**
```json
{
    "code": 400,
    "msg": "错误描述",
    "data": null
}
```

### 架构说明

- **主数据源**：SQLite3（`server/images.db`），由采集脚本定期更新
- **回退机制**：数据库不可用时，`classifyList` 接口自动回退到内置硬编码数据，确保 App 基础可用
- **只读连接**：接口以 `SQLITE3_OPEN_READONLY` 模式打开数据库，避免并发写入冲突
- **跨域支持**：设置 `Access-Control-Allow-Origin: *` 允许跨域访问

### 部署要求

```bash
# 环境要求
PHP ≥ 7.1
php-sqlite3 扩展

# 目录结构
/var/www/html/
├── api.php              # 接口入口文件
└── server/
    └── images.db        # SQLite3 数据库文件
```

---

## 🗄️ 数据库结构 (SQLite3)

App 采用三级分类模型，使用 SQLite3 存储，数据库文件为 `server/images.db`。

### 数据模型 ER 图

```
t_classify (一级分类)
    │ 1:N
    ▼
t_chapter (二级章节)
    │ 1:N
    ▼
t_picture (三级图片)
```

### 表结构详述

#### t_classify — 分类表

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | INTEGER PK | 自增主键 |
| `title` | TEXT NOT NULL | 分类名称（如：风景图集、动漫图集） |
| `cover` | TEXT NOT NULL | 封面图 URL |
| `sort_order` | INTEGER | 排序权重（值越小越靠前） |
| `status` | INTEGER | 状态：1=启用 0=禁用 |
| `created_at` | TEXT | 创建时间（本地时间） |
| `updated_at` | TEXT | 更新时间（本地时间） |

#### t_chapter — 章节表

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | INTEGER PK | 自增主键 |
| `classify_id` | INTEGER FK | 所属分类 ID → `t_classify.id` |
| `title` | TEXT NOT NULL | 章节名称 |
| `cover` | TEXT NOT NULL | 封面图 URL |
| `page_count` | INTEGER | 图片数量（由采集脚本自动更新） |
| `sort_order` | INTEGER | 排序权重 |
| `status` | INTEGER | 状态：1=启用 0=禁用 |
| `created_at` | TEXT | 创建时间 |
| `updated_at` | TEXT | 更新时间 |

#### t_picture — 图片表

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | INTEGER PK | 自增主键 |
| `chapter_id` | INTEGER FK | 所属章节 ID → `t_chapter.id` |
| `url` | TEXT NOT NULL | 图片原始 URL |
| `thumb_url` | TEXT | 缩略图 URL（可选） |
| `sort_order` | INTEGER | 图片展示顺序 |
| `width` | INTEGER | 图片宽度（px） |
| `height` | INTEGER | 图片高度（px） |
| `file_size` | INTEGER | 文件大小（字节） |
| `source` | TEXT | 采集来源标识（如 `picsum`） |
| `source_id` | TEXT | 原始平台 ID |
| `md5_hash` | TEXT | MD5 哈希值（用于去重） |
| `status` | INTEGER | 状态：1=启用 0=禁用 |
| `created_at` | TEXT | 创建时间 |

### 索引

- `idx_chapter_classify` — 加速按分类查询章节
- `idx_picture_chapter` — 加速按章节查询图片
- `idx_picture_md5` — 加速 MD5 去重
- `idx_picture_source` — 加速按来源查询

### App 端 Room 数据库

App 内置 Room 数据库（`net_image_viewer_db`），包含以下表：

| 表名 | 用途 |
|------|------|
| `classify_cache` | 分类数据本地缓存 |
| `chapter_cache` | 章节数据本地缓存 |
| `picture_cache` | 图片数据本地缓存 |
| `browse_history` | 浏览历史记录（按章节去重） |
| `favorites` | 收藏夹 |

### 初始化

```bash
sqlite3 server/images.db < server/schema.sql
```

---

## 🐍 Python 采集程序 (collector.py)

`server/collector.py` 是数据采集脚本，负责从在线图源获取图片元数据并写入 SQLite3 数据库。

### 快速开始

```bash
# 1. 初始化数据库 + 预设分类章节
python collector.py init

# 2. 采集图片（全部章节，每章5张，默认 picsum 源）
python collector.py collect

# 3. 自定义采集（指定源和每章数量）
python collector.py collect -s picsum -c 10
```

### 命令一览

| 命令 | 说明 |
|------|------|
| `python collector.py init` | 建表并插入 5 个分类、每分类 4 个章节的预设数据 |
| `python collector.py collect` | 全量采集，自动跳过已达目标数量的章节 |
| `python collector.py collect -s picsum -c 10` | 使用 picsum 源，每章采集 10 张 |
| `python collector.py stats` | 查看分类/章节/图片数量统计 |
| `python collector.py detail` | 查看全部分类章节详情 |
| `python collector.py dedup` | 按 MD5 和 URL 去重，删除重复图片记录 |

### 采集源

当前支持的图片来源：

| 源标识 | 平台 | 说明 |
|--------|------|------|
| `picsum` | [Lorem Picsum](https://picsum.photos/) | 免费开源图库，按 ID 获取稳定图片 |

> 💡 扩展新源：参照 `PicsumCollector` 基类方式，实现 `ImageCollector` 子类并注册到 `collectors` 字典即可。

### 预设数据

脚本内置 5 大分类 × 4 章节的结构：

| 分类 | 章节 |
|------|------|
| 风景图集 | 山间晨雾 / 落日海岸 / 森林秘境 / 冬日雪原 |
| 动漫图集 | 插画艺术 / 古风雅韵 / 科幻幻想 / 萌系二次元 |
| 美女图集 | 清新少女 / 优雅女神 / 运动活力 / 复古风情 |
| 汽车图集 | 经典老车 / 豪华轿跑 / 越野悍将 / 赛道风云 |
| 萌宠图集 | 猫咪日常 / 狗狗乐园 / 异宠世界 / 野外生灵 |

### 运行要求

- Python ≥ 3.6（标准库即可，无第三方依赖）
- `server/schema.sql` 与 `collector.py` 位于同一目录
- 需要网络连接访问在线图源
- 脚本内置限速（`time.sleep(0.5)`），友好请求

---

## 📄 许可证

本项目仅供学习和个人使用。