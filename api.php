<?php
/**
 * 网络图集 API v2.0
 * 部署到 http://115.190.224.6/api.php
 * 
 * 三级接口（与 App 签名一致）：
 *   GET /api.php?action=classifyList             一级分类列表
 *   GET /api.php?action=chapterList&id=1         二级章节列表
 *   GET /api.php?action=pictureList&id=101       三级图片列表
 * 
 * 架构：SQLite3（images.db）为主数据源，采集脚本定期更新。
 * 要求：PHP ≥ 7.1 + php-sqlite3 扩展
 */

header('Content-Type: application/json; charset=utf-8');
header('Access-Control-Allow-Origin: *');

$action = isset($_GET['action']) ? $_GET['action'] : '';
$id     = isset($_GET['id'])     ? intval($_GET['id']) : 0;

// ==================== 数据库连接 ====================

define('DB_PATH', __DIR__ . '/server/images.db');

function openDB() {
    static $db = null;
    if ($db === null) {
        if (!extension_loaded('sqlite3')) {
            return null;
        }
        if (!file_exists(DB_PATH)) {
            return null;
        }
        try {
            $db = new SQLite3(DB_PATH, SQLITE3_OPEN_READONLY);
            $db->busyTimeout(3000);
        } catch (Exception $e) {
            return null;
        }
    }
    return $db;
}

// ==================== 业务查询 ====================

function getClassifyList() {
    $db = openDB();
    if ($db) {
        $results = [];
        $stmt = $db->prepare(
            "SELECT id, title, cover FROM t_classify WHERE status = 1 ORDER BY sort_order"
        );
        $res = $stmt->execute();
        while ($row = $res->fetchArray(SQLITE3_ASSOC)) {
            $results[] = [
                'id'    => (int)$row['id'],
                'title' => $row['title'],
                'cover' => $row['cover']
            ];
        }
        if (!empty($results)) return $results;
    }
    // fallback 硬编码数据
    return [
        ['id' => 1, 'title' => '风景图集', 'cover' => 'https://picsum.photos/id/10/400/300'],
        ['id' => 2, 'title' => '动漫图集', 'cover' => 'https://picsum.photos/id/20/400/300'],
        ['id' => 3, 'title' => '美女图集', 'cover' => 'https://picsum.photos/id/30/400/300'],
        ['id' => 4, 'title' => '汽车图集', 'cover' => 'https://picsum.photos/id/50/400/300'],
        ['id' => 5, 'title' => '萌宠图集', 'cover' => 'https://picsum.photos/id/40/400/300'],
    ];
}

function getChapterList($classifyId) {
    $db = openDB();
    if ($db) {
        $results = [];
        $stmt = $db->prepare(
            "SELECT id, title, cover, page_count FROM t_chapter 
             WHERE classify_id = :cid AND status = 1 
             ORDER BY sort_order"
        );
        $stmt->bindValue(':cid', $classifyId, SQLITE3_INTEGER);
        $res = $stmt->execute();
        while ($row = $res->fetchArray(SQLITE3_ASSOC)) {
            $results[] = [
                'id'        => (int)$row['id'],
                'title'     => $row['title'],
                'cover'     => $row['cover'],
                'pageCount' => (int)$row['page_count']
            ];
        }
        if (!empty($results)) return $results;
    }
    return null;
}

function getPictureList($chapterId) {
    $db = openDB();
    if ($db) {
        $results = [];
        $stmt = $db->prepare(
            "SELECT id, url, sort_order FROM t_picture 
             WHERE chapter_id = :cid AND status = 1 
             ORDER BY sort_order"
        );
        $stmt->bindValue(':cid', $chapterId, SQLITE3_INTEGER);
        $res = $stmt->execute();
        while ($row = $res->fetchArray(SQLITE3_ASSOC)) {
            $results[] = [
                'id'    => (int)$row['id'],
                'url'   => $row['url'],
                'order' => (int)$row['sort_order']
            ];
        }
        if (!empty($results)) return $results;
    }
    return null;
}

// ==================== 路由处理 ====================

switch ($action) {
    case 'classifyList':
        jsonSuccess(getClassifyList());
        break;

    case 'chapterList':
        if ($id <= 0) {
            jsonError('缺少 id 参数');
        }
        $data = getChapterList($id);
        if ($data !== null) {
            jsonSuccess($data);
        } else {
            jsonError('分类不存在或无章节数据');
        }
        break;

    case 'pictureList':
        if ($id <= 0) {
            jsonError('缺少 id 参数');
        }
        $data = getPictureList($id);
        if ($data !== null) {
            jsonSuccess($data);
        } else {
            jsonError('章节不存在或无图片数据');
        }
        break;

    case 'stats':
        // 统计接口（可选）
        $db = openDB();
        if ($db) {
            $classify = $db->querySingle("SELECT COUNT(*) FROM t_classify WHERE status = 1");
            $chapter  = $db->querySingle("SELECT COUNT(*) FROM t_chapter WHERE status = 1");
            $picture  = $db->querySingle("SELECT COUNT(*) FROM t_picture WHERE status = 1");
            jsonSuccess([
                'classify_count' => (int)$classify,
                'chapter_count'  => (int)$chapter,
                'picture_count'  => (int)$picture
            ]);
        } else {
            jsonError('数据库未就绪');
        }
        break;

    default:
        jsonError('无效的 action。可用: classifyList, chapterList, pictureList, stats');
}

// ==================== 工具函数 ====================

function jsonSuccess($data) {
    echo json_encode([
        'code' => 200,
        'msg'  => 'success',
        'data' => $data
    ], JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);
    exit;
}

function jsonError($msg) {
    echo json_encode([
        'code' => 400,
        'msg'  => $msg,
        'data' => null
    ], JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);
    exit;
}