# 图图 (NetImageViewer)

**本项目全程使用 Operit AI 开发生成。** 图图是一款基于 Android 平台的网络图片浏览应用，支持三级分类浏览、章节列表、全屏图片查看、浏览历史、收藏管理、缓存清理等功能，为用户提供流畅的在线图库浏览体验。

**GitHub：** https://github.com/w2018/NetImageViewer

---

## ✨ 功能特性

- 🏠 **首页分类浏览** — 一级分类列表，支持下拉刷新（Snackbar 提示）
- 📑 **章节列表** — 分类下的二级章节浏览，支持下拉刷新（Snackbar 提示）
- 🖼️ **全屏图片查看** — ViewPager2 + PhotoView 全屏浏览，手势缩放/拖拽
- ⭐ **收藏功能** — 章节收藏，Toolbar 实时状态同步，收藏列表查看与跳转
- 📚 **浏览历史** — 自动记录，按时间倒序，支持清空
- 💾 **本地缓存** — Room 三级离线缓存（分类/章节/图片）
- ⚙️ **设置页面** — 自定义 API Base URL，保存/重置/连接测试
- 🗑️ **缓存管理** — 显示缓存大小，一键清理
- ℹ️ **关于页面** — 版本/作者/GitHub/技术栈文档/开源许可
- 📝 **帮助页面** — 数据结构/API 结构/网络层/数据库结构完整文档

## 🛠️ 技术栈

| 类别 | 技术 |
|------|------|
| 语言 | Java 8 |
| 构建工具 | Gradle 8.9 (Kotlin DSL) + AGP 8.7.3 |
| 最低/目标 SDK | API 24 / API 34 |
| 网络请求 | Retrofit 2.9.0 + OkHttp 4.12.0 + Gson |
| 图片加载 | Glide 4.16.0 + PhotoView 2.3.0 |
| 本地数据库 | Room 2.6.1（5张表） |
| UI 框架 | Material Design 1.11.0 + AndroidX + ViewBinding |
| 架构组件 | ViewModel + LiveData + Lifecycle |
| 页面导航 | ViewPager2 + TabLayout |
| 自动构建 | GitHub Actions CI |
| 代码混淆 | R8 / ProGuard（APK 2.1MB） |

## 📂 项目结构

```
app/src/main/java/com/operit/netimageviewer/
├── data/
│   ├── model/             # 数据模型
│   │   ├── ApiResponse.java
│   │   ├── ClassifyItem.java
│   │   ├── ChapterItem.java
│   │   └── PictureItem.java
│   ├── local/             # Room 本地数据源
│   │   ├── AppDatabase.java
│   │   ├── ClassifyCacheEntity/Dao.java
│   │   ├── ChapterCacheEntity/Dao.java
│   │   ├── PictureCacheEntity/Dao.java
│   │   ├── BrowseHistoryEntity/Dao.java
│   │   ├── FavoriteEntity/Dao.java
│   │   └── ApiConfigManager.java
│   ├── remote/            # Retrofit 远程数据源
│   │   ├── ApiService.java
│   │   └── RetrofitClient.java
│   └── repository/
├── ui/
│   ├── home/              # 首页（分类列表）
│   ├── chapter/           # 章节列表
│   ├── picture/           # 图片浏览
│   ├── settings/          # 设置
│   ├── cache/             # 缓存管理
│   ├── about/             # 关于（含技术文档）
│   ├── help/              # 帮助
│   └── history/           # 历史与收藏
└── MyAppGlideModule.java
```

## 🚀 构建运行

```bash
# 克隆仓库
git clone https://github.com/w2018/NetImageViewer.git
cd NetImageViewer

# Debug 构建
./gradlew assembleDebug

# Release 构建（R8 混淆 + 资源缩减）
./gradlew assembleRelease

# 安装
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 📖 帮助页文档

### 1. 数据结构

```java
public class ApiResponse<T> {
    int code;       // 状态码（200=成功）
    String msg;     // 响应消息
    T data;         // 泛型数据体
    boolean isSuccess();
}

public class ClassifyItem {
    int id; String title; String cover;
}

public class ChapterItem {
    int id; String title; String cover; int pageCount;
}

public class PictureItem {
    int id; String url; int order;
}
```

### 2. API 接口

| 端点 | 方法 | 请求格式 | 返回类型 |
|------|------|---------|---------|
| getClassifyList | GET | `api.php?action=classifyList` | `ApiResponse<List<ClassifyItem>>` |
| getChapterList | GET | `api.php?action=chapterList&id={id}` | `ApiResponse<List<ChapterItem>>` |
| getPictureList | GET | `api.php?action=pictureList&id={id}` | `ApiResponse<List<PictureItem>>` |

### 3. 网络层配置

- **OkHttp**: 连接超时15s / 读写超时30s / 10MB缓存 / Body级别日志
- **Retrofit**: 双检锁单例 / 动态baseURL / Gson 转换器

### 4. 数据库结构（Room 5张表）

| 表名 | 主键 | 说明 |
|------|------|------|
| classify_cache | id | 分类缓存 |
| chapter_cache | id | 章节缓存（含classifyId外键） |
| picture_cache | id | 图片缓存（含chapterId外键） |
| browse_history | id(自增) | 浏览历史（chapterId唯一索引） |
| favorites | id(自增) | 收藏夹 |

## 🌐 服务端 API

### 接口

| 接口 | 说明 |
|------|------|
| `api.php?action=classifyList` | 获取分类列表 |
| `api.php?action=chapterList&id=` | 获取章节列表 |
| `api.php?action=pictureList&id=` | 获取图片列表 |
| `api.php?action=stats` | 数据库统计 |

### 响应格式
```json
{"code": 200, "msg": "success", "data": [...]}
```

部署要求：PHP 7.1+，php-sqlite3 扩展。

## 🗄️ 服务端数据库 (SQLite3)

### 三级分类模型
```
t_classify ──1:N── t_chapter ──1:N── t_picture
```

### 预设数据

| 分类 | 章节 |
|------|------|
| 🏔️ 风景图集 | 山间晨雾 / 落日海岸 / 森林秘境 / 冬日雪原 |
| 🎨 动漫图集 | 插画艺术 / 古风雅韵 / 科幻幻想 / 萌系二次元 |
| 👩 美女图集 | 清新少女 / 优雅女神 / 运动活力 / 复古风情 |
| 🚗 汽车图集 | 经典老车 / 豪华轿跑 / 越野悍将 / 赛道风云 |
| 🐱 萌宠图集 | 猫咪日常 / 狗狗乐园 / 异宠世界 / 野外生灵 |

## 🐍 采集脚本

```bash
python collector.py init      # 初始化数据库
python collector.py collect   # 采集图片
python collector.py stats     # 查看统计
python collector.py dedup     # 去重
```

源：picsum (Lorem Picsum)，Python 3.6+ 标准库。

## 📋 版本历史

| 版本 | 说明 |
|------|------|
| v2.1.0 | 帮助页重写（完整技术文档）、Release构建（R8+缩减，APK 2.1MB） |
| v2.0.0 | 设置/缓存/历史收藏/双Tab导航 |
| v1.x | 基础功能迭代 |
| v1.0.0 | 初始版本 |

## 📄 许可证

仅供学习和个人使用。

---

*由 Operit AI 辅助开发* | https://github.com/w2018/NetImageViewer
