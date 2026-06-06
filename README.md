# 图图 (NetImageViewer)

**本项目全程使用 Operit AI 开发生成。** 图图是一款基于 Android 平台的网络图片浏览应用，支持三级分类浏览、章节列表、全屏图片查看、浏览历史、收藏管理、缓存清理等功能，为用户提供流畅的在线图库浏览体验。

**GitHub：** https://github.com/operit/NetImageViewer

**部署地址：** http://127.0.0.1/api.php

---

## ✨ 功能特性

- 🏠 **首页分类浏览** — 一级分类列表，支持下拉刷新（Snackbar 提示）
- 📑 **章节列表** — 分类下的二级章节浏览，支持下拉刷新（Snackbar 提示）
- 🖼️ **全屏图片查看** — ViewPager2 + PhotoView 全屏浏览，手势缩放/拖拽，预加载下一章
- ⭐ **收藏功能** — 章节收藏，Toolbar 实时状态同步，收藏列表查看与跳转
- 📚 **浏览历史** — 自动记录，按时间倒序，支持清空与侧滑删除
- 💾 **本地缓存** — Room 三级离线缓存（分类/章节/图片），策略：先读缓存再请求网络
- ⚙️ **设置页面** — 自定义 API Base URL，保存/重置/连接测试
- 🗑️ **缓存管理** — 显示缓存大小，一键清理 HTTP / Room 缓存
- ℹ️ **关于页面** — 版本/作者/GitHub/数据结构/API 结构/网络层/数据库完整文档
- 🔄 **自动构建** — GitHub Actions CI，推送 Tag 自动编译 Release + R8 混淆缩减

## 🛠️ 技术栈

| 类别 | 技术 |
|------|------|
| 语言 | Java 8 |
| 构建工具 | Gradle 8.9 (Kotlin DSL) + AGP 8.7.3 |
| 最低/目标 SDK | API 24 (Android 7.0) / API 34 (Android 14) |
| 网络请求 | Retrofit 2.9.0 + OkHttp 4.12.0 + Gson |
| 图片加载 | Glide 4.16.0 + PhotoView 2.3.0 |
| 本地数据库 | Room 2.6.1（5 张表，version 2，fallbackToDestructiveMigration） |
| UI 框架 | Material Design 1.11.0 + AndroidX + ViewBinding |
| 架构组件 | ViewModel + LiveData + Lifecycle |
| 页面导航 | ViewPager2 + TabLayout + Fragment |
| 并发 | ExecutorService (FixedThreadPool 4) |
| 自动构建 | GitHub Actions CI (JDK 17, tagged trigger) |
| 代码混淆 | R8 / ProGuard（isMinifyEnabled + shrinkResources，APK ~2.6MB） |
| 服务端 | PHP 7.1+ + SQLite3 + Python 采集脚本 |

## 📂 完整项目结构

```
NetImageViewer/
├── .github/
│   └── workflows/
│       └── release.yml                     # GitHub Actions 自动构建（tag 触发）
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/operit/netimageviewer/
│   │   │   │   ├── MyAppGlideModule.java       # Glide 全局配置
│   │   │   │   ├── data/
│   │   │   │   │   ├── model/                  # 数据模型（POJO）
│   │   │   │   │   │   ├── ApiResponse.java        # API 通用响应包装 {code, msg, data}
│   │   │   │   │   │   ├── ClassifyItem.java       # 分类模型
│   │   │   │   │   │   ├── ChapterItem.java        # 章节模型
│   │   │   │   │   │   └── PictureItem.java        # 图片模型
│   │   │   │   │   ├── local/                  # Room 本地数据源
│   │   │   │   │   │   ├── AppDatabase.java        # 数据库入口（单例，5 表）
│   │   │   │   │   │   ├── ApiConfigManager.java   # 动态 API Base URL 管理
│   │   │   │   │   │   ├── ClassifyCacheEntity.java
│   │   │   │   │   │   ├── ClassifyCacheDao.java
│   │   │   │   │   │   ├── ChapterCacheEntity.java
│   │   │   │   │   │   ├── ChapterCacheDao.java
│   │   │   │   │   │   ├── PictureCacheEntity.java
│   │   │   │   │   │   ├── PictureCacheDao.java
│   │   │   │   │   │   ├── BrowseHistoryEntity.java
│   │   │   │   │   │   ├── BrowseHistoryDao.java
│   │   │   │   │   │   ├── FavoriteEntity.java
│   │   │   │   │   │   └── FavoriteDao.java
│   │   │   │   │   ├── remote/                 # Retrofit 远程数据源
│   │   │   │   │   │   ├── ApiService.java         # API 接口定义（3 端点）
│   │   │   │   │   │   └── RetrofitClient.java     # 双检锁单例，动态 baseURL
│   │   │   │   │   └── repository/
│   │   │   │   │       └── ImageRepository.java    # 统一仓库层：缓存策略 + 错误处理
│   │   │   │   └── ui/
│   │   │   │       ├── home/                   # 首页（分类列表）
│   │   │   │       │   ├── HomeActivity.java
│   │   │   │       │   ├── HomeViewModel.java
│   │   │   │       │   └── ClassifyAdapter.java
│   │   │   │       ├── chapter/                # 章节列表
│   │   │   │       │   ├── ChapterActivity.java
│   │   │   │       │   ├── ChapterViewModel.java
│   │   │   │       │   └── ChapterAdapter.java
│   │   │   │       ├── picture/                # 全屏图片浏览
│   │   │   │       │   ├── PictureActivity.java
│   │   │   │       │   ├── PictureViewModel.java
│   │   │   │       │   └── PicturePagerAdapter.java
│   │   │   │       ├── history/                # 历史 & 收藏（双 Tab）
│   │   │   │       │   ├── HistoryFavoriteActivity.java
│   │   │   │       │   ├── HistoryFavoritePagerAdapter.java
│   │   │   │       │   ├── HistoryFavoriteAdapter.java
│   │   │   │       │   ├── HistoryFragment.java
│   │   │   │       │   ├── FavoriteFragment.java
│   │   │   │       │   └── SwipeToDeleteCallback.java
│   │   │   │       ├── settings/               # 设置
│   │   │   │       │   └── SettingsActivity.java
│   │   │   │       ├── cache/                  # 缓存管理
│   │   │   │       │   └── CacheManagerActivity.java
│   │   │   │       ├── about/                  # 关于（含完整技术文档）
│   │   │   │       │   └── AboutActivity.java
│   │   │   │       └── help/                   # 帮助页面
│   │   │   │           └── HelpActivity.java
│   │   │   └── res/
│   │   │       ├── drawable/                   # SVG/XML 图标（9 个）
│   │   │       ├── layout/                     # XML 布局（16 个）
│   │   │       ├── menu/                       # 菜单（3 个）
│   │   │       ├── mipmap-hdpi/
│   │   │       ├── mipmap-mdpi/
│   │   │       ├── mipmap-xhdpi/
│   │   │       ├── mipmap-xxhdpi/
│   │   │       ├── mipmap-xxxhdpi/
│   │   │       └── values/                     # strings / colors / themes
│   ├── proguard-rules.pro                      # ProGuard/R8 混淆规则
│   └── build.gradle.kts                        # App 模块构建配置
├── server/
│   ├── schema.sql                              # SQLite3 数据库建表 DDL
│   └── collector.py                            # Python 多源图片采集脚本
├── api.php                                     # PHP API 入口（路由到 server/images.db）
├── build.gradle.kts                            # 项目级构建配置
├── settings.gradle.kts                         # 模块设置
├── gradle.properties                           # Gradle 属性
├── gradlew                                     # Gradle Wrapper（Linux）
├── gradlew.bat                                 # Gradle Wrapper（Windows）
├── gradle/wrapper/
│   ├── gradle-wrapper.jar
│   └── gradle-wrapper.properties
├── .gitignore
├── .github/                                    # GitHub 相关
├── BUILD_ENV_LOCK.yml                          # 构建环境锁定配置
├── DEV_RULES.md                                # 开发规范
├── icon_origin.png                             # 原始图标资源
├── local.properties                            # 本地 SDK 路径配置
└── README.md
```
```

## 🚀 构建运行

```bash
# 克隆仓库
git clone https://github.com/operit/NetImageViewer.git
cd NetImageViewer

# Debug 构建
./gradlew assembleDebug

# Release 构建（R8 混淆 + 资源缩减）
./gradlew assembleRelease

# 直接安装
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 🌐 服务端部署

### API 接口

| 端点 | 方法 | 参数 | 说明 |
|------|------|------|------|
| `api.php?action=classifyList` | GET | 无 | 获取一级分类列表 |
| `api.php?action=chapterList&id={classifyId}` | GET | classifyId | 获取指定分类下的章节列表 |
| `api.php?action=pictureList&id={chapterId}` | GET | chapterId | 获取指定章节的图片列表 |
| `api.php?action=stats` | GET | 无 | 数据库统计信息 |

### 响应格式

```json
{
  "code": 200,
  "msg": "success",
  "data": [...] 
}
```

错误响应：`code` 为 400，`msg` 为错误描述。

### 部署要求

- PHP 7.1+（需启用 `php-sqlite3` 扩展）
- API 入口文件 `api.php` 部署到 Web 服务器根目录
- 数据库文件 `server/images.db` 与 `api.php` 相对路径关联

### 服务端文件结构

```
部署目录/
├── api.php                   # API 入口（路由分发）
└── server/
    ├── images.db             # SQLite3 数据库（由 collector.py 生成）
    ├── schema.sql            # 数据库建表 DDL
    └── collector.py          # Python 采集脚本
```

## 🗄️ 服务端数据库结构 (SQLite3)

### 三级分类关系模型

```
t_classify ──1:N── t_chapter ──1:N── t_picture
```

### t_classify（分类表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INTEGER PK | 分类 ID（自增） |
| title | TEXT | 分类名称 |
| cover | TEXT | 封面图 URL |
| sort_order | INTEGER | 排序序号 |
| status | INTEGER | 1=启用, 0=禁用 |
| created_at | TEXT | 创建时间 |
| updated_at | TEXT | 更新时间 |

### t_chapter（章节表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INTEGER PK | 章节 ID（自增） |
| classify_id | INTEGER FK | 所属分类 ID → t_classify.id |
| title | TEXT | 章节名称 |
| cover | TEXT | 封面图 URL |
| page_count | INTEGER | 图片总数 |
| sort_order | INTEGER | 排序序号 |
| status | INTEGER | 1=启用, 0=禁用 |
| created_at | TEXT | 创建时间 |
| updated_at | TEXT | 更新时间 |

### t_picture（图片表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INTEGER PK | 图片 ID（自增） |
| chapter_id | INTEGER FK | 所属章节 ID → t_chapter.id |
| url | TEXT | 图片 URL |
| thumb_url | TEXT | 缩略图 URL |
| sort_order | INTEGER | 图片顺序 |
| width | INTEGER | 图片宽度（px） |
| height | INTEGER | 图片高度（px） |
| file_size | INTEGER | 文件大小（bytes） |
| source | TEXT | 采集来源（picsum 等） |
| source_id | TEXT | 原始采集源 ID |
| md5_hash | TEXT | 图片 MD5（去重用） |
| status | INTEGER | 1=启用, 0=禁用 |
| created_at | TEXT | 创建时间 |

### 索引

| 索引名 | 表 | 字段 |
|--------|-----|------|
| idx_chapter_classify | t_chapter | classify_id |
| idx_picture_chapter | t_picture | chapter_id |
| idx_picture_md5 | t_picture | md5_hash |
| idx_picture_source | t_picture | source, source_id |

### 预设数据

| 分类 | 章节 |
|------|------|
| 🏔️ 风景图集 | 山间晨雾 / 落日海岸 / 森林秘境 / 冬日雪原 |
| 🎨 动漫图集 | 插画艺术 / 古风雅韵 / 科幻幻想 / 萌系二次元 |
| 👩 美女图集 | 清新少女 / 优雅女神 / 运动活力 / 复古风情 |
| 🚗 汽车图集 | 经典老车 / 豪华轿跑 / 越野悍将 / 赛道风云 |
| 🐱 萌宠图集 | 猫咪日常 / 狗狗乐园 / 异宠世界 / 野外生灵 |

## 🐍 图片采集脚本

`server/collector.py` 支持多源采集、自动去重、MD5 校验、封面自动更新。

### 命令

```bash
python collector.py init                          # 建表 + 插入预设分类/章节
python collector.py collect                        # 全量采集（每章 5 张，默认源 picsum）
python collector.py collect -s picsum -c 10        # 指定源 + 每章图片数
python collector.py stats                          # 查看数据库统计
python collector.py detail                         # 查看详细分类/章节数据
python collector.py dedup                          # 去重清理（按 md5 + url）
```

### 采集源

- **picsum** — Lorem Picsum 免费开源图库，自动设置缩略图和章节封面

采集策略：按分类章节轮询，友好限速 0.5s/请求，自动跳过已有足量图片的章节，采集完成后自动更新分类封面。

## 📖 本地缓存策略（Room）

App 端采用 **先读缓存，再请求网络** 的双层策略：

1. 启动页面时先从 Room 数据库读取上次缓存的数据并展示
2. 同时发起网络请求获取最新数据
3. 网络返回成功 → 更新 LiveData（UI 自动刷新） + 写入 Room 缓存
4. 网络失败 → 弹出 Snackbar 提示，保留缓存展示
5. 设置中修改 API Base URL 后自动清除所有 Room 缓存 + HTTP 缓存

### App 端数据库（Room, 5 张表）

| 表名 | 对应 Entity | 缓存内容 | 清空时机 |
|------|-----------|---------|---------|
| classify_cache | ClassifyCacheEntity | 分类列表 | API URL 变更 / 手动清理 |
| chapter_cache | ChapterCacheEntity | 分类下的章节列表 | API URL 变更 / 手动清理 |
| picture_cache | PictureCacheEntity | 章节内的图片列表 | API URL 变更 / 手动清理 |
| browse_history | BrowseHistoryEntity | 用户浏览记录（自增 ID + chapterId 唯一索引） | 手动清空 / 单条侧滑删除 |
| favorites | FavoriteEntity | 用户收藏（自增 ID） | 单条取消 / 全部取消 |

## 📋 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| **v2.2.0** | 2026-06 | 修复收藏功能页面空白 Bug|
| v2.1.0 | — | 设置/缓存/历史收藏/双 Tab 导航 |
| v1.0.0 | — | 初始版本 |

## 📄 许可证

仅供学习和个人使用。

---

*由 Operit AI 辅助开发* | https://github.com/operit/NetImageViewer
