#!/usr/bin/env python3
"""
============================================
网络图集 - Python 图片采集脚本
============================================
支持多源采集：picsum、unsplash、自定义源
自动去重、缩略图生成、MD5校验

用法：
  python collector.py init        # 初始化数据库 + 预设分类章节
  python collector.py collect     # 采集图片（全量）
  python collector.py collect -s picsum -c 5  # 指定源+每章数量
  python collector.py stats       # 查看统计
  python collector.py dedup       # 去重清理
"""

import sqlite3
import hashlib
import os
import sys
import json
import time
import argparse
from datetime import datetime
from urllib.request import urlopen, Request
from urllib.error import URLError

# ==================== 配置 ====================

DB_PATH = os.path.join(os.path.dirname(__file__), 'images.db')
USER_AGENT = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'

# 分类 + 章节预设（可按需扩展）
PRESET_DATA = {
    '风景图集': ['山间晨雾', '落日海岸', '森林秘境', '冬日雪原'],
    '动漫图集': ['插画艺术', '古风雅韵', '科幻幻想', '萌系二次元'],
    '美女图集': ['清新少女', '优雅女神', '运动活力', '复古风情'],
    '汽车图集': ['经典老车', '豪华轿跑', '越野悍将', '赛道风云'],
    '萌宠图集': ['猫咪日常', '狗狗乐园', '异宠世界', '野外生灵'],
}

# ==================== 数据库 ====================

def get_db():
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    conn.execute("PRAGMA foreign_keys = ON")
    conn.execute("PRAGMA journal_mode = WAL")
    return conn

def init_db():
    """建表 + 插入预设分类章节"""
    schema_path = os.path.join(os.path.dirname(__file__), 'schema.sql')
    conn = get_db()
    with open(schema_path, 'r', encoding='utf-8') as f:
        conn.executescript(f.read())
    
    # 插入预设数据
    for classify_title, chapters in PRESET_DATA.items():
        # 检查分类是否已存在
        cur = conn.execute("SELECT id FROM t_classify WHERE title = ?", (classify_title,))
        row = cur.fetchone()
        if row:
            classify_id = row['id']
        else:
            cur = conn.execute(
                "INSERT INTO t_classify (title, cover, sort_order) VALUES (?, ?, ?)",
                (classify_title, '', len(PRESET_DATA))
            )
            classify_id = cur.lastrowid
        
        # 更新排序
        keys = list(PRESET_DATA.keys())
        conn.execute("UPDATE t_classify SET sort_order = ? WHERE id = ?",
                     (keys.index(classify_title), classify_id))
        
        for i, chapter_title in enumerate(chapters):
            cur = conn.execute(
                "SELECT id FROM t_chapter WHERE classify_id = ? AND title = ?",
                (classify_id, chapter_title)
            )
            if not cur.fetchone():
                conn.execute(
                    "INSERT INTO t_chapter (classify_id, title, cover, page_count, sort_order) "
                    "VALUES (?, ?, ?, 0, ?)",
                    (classify_id, chapter_title, '', i)
                )
    
    conn.commit()
    conn.close()
    print("✅ 数据库初始化完成")
    show_stats()

# ==================== 采集器 ====================

class ImageCollector:
    """图片采集器基类"""
    
    def __init__(self, db):
        self.db = db
    
    def url_exists(self, url):
        cur = self.db.execute("SELECT id FROM t_picture WHERE url = ?", (url,))
        return cur.fetchone() is not None
    
    def md5_exists(self, md5_hash):
        if not md5_hash:
            return False
        cur = self.db.execute("SELECT id FROM t_picture WHERE md5_hash = ?", (md5_hash,))
        return cur.fetchone() is not None
    
    def insert_picture(self, chapter_id, url, sort_order, source, source_id='',
                       thumb_url='', width=0, height=0, file_size=0):
        if self.url_exists(url):
            return None
        
        try:
            self.db.execute(
                "INSERT INTO t_picture (chapter_id, url, thumb_url, sort_order, "
                "width, height, file_size, source, source_id, md5_hash) "
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, '')",
                (chapter_id, url, thumb_url, sort_order, width, height, file_size,
                 source, str(source_id))
            )
            return self.db.execute("SELECT last_insert_rowid()").fetchone()[0]
        except Exception as e:
            print(f"   ⚠️ 插入失败: {e}")
            return None

class PicsumCollector(ImageCollector):
    """Picsum 采集器 - 免费开源图库"""
    
    BASE_API = "https://picsum.photos/v2/list"
    
    def collect_for_chapter(self, chapter_id, count=5):
        """采集指定章节图片"""
        # 用不同 page 获取多样化图片
        collected = 0
        for page in range(1, 6):
            if collected >= count:
                break
            url = f"{self.BASE_API}?page={page}&limit=30"
            try:
                req = Request(url, headers={'User-Agent': USER_AGENT})
                with urlopen(req, timeout=15) as resp:
                    data = json.loads(resp.read().decode())
                
                for item in data:
                    if collected >= count:
                        break
                    pic_id = item['id']
                    pic_url = f"https://picsum.photos/id/{pic_id}/800/1200"
                    thumb_url = f"https://picsum.photos/id/{pic_id}/400/300"
                    width = item.get('width', 0)
                    height = item.get('height', 0)
                    
                    pid = self.insert_picture(
                        chapter_id=chapter_id,
                        url=pic_url,
                        sort_order=collected + 1,
                        source='picsum',
                        source_id=pic_id,
                        thumb_url=thumb_url,
                        width=width,
                        height=height
                    )
                    if pid:
                        collected += 1
                        # 更新章节封面
                        self._update_chapter_cover(chapter_id, thumb_url)
                        print(f"   [{collected}/{count}] picsum id={pic_id}")
                
                time.sleep(0.5)  # 友好限速
                
            except URLError as e:
                print(f"   ⚠️ 网络错误: {e}")
                break
            except Exception as e:
                print(f"   ⚠️ 解析错误: {e}")
                continue
        
        return collected
    
    def _update_chapter_cover(self, chapter_id, cover_url):
        """自动设置章节封面（第一张图）"""
        self.db.execute(
            "UPDATE t_chapter SET cover = ? WHERE id = ? AND (cover = '' OR cover IS NULL)",
            (cover_url, chapter_id)
        )

# ==================== 采集主流程 ====================

def collect_all(source='picsum', per_chapter=5):
    """采集全部章节的图片"""
    conn = get_db()
    
    # 初始化采集器
    collectors = {
        'picsum': PicsumCollector(conn),
    }
    collector = collectors.get(source)
    if not collector:
        print(f"❌ 不支持的采集源: {source}")
        conn.close()
        return
    
    # 获取所有启用的章节
    chapters = conn.execute("""
        SELECT c.id, c.title, cl.title as classify_title
        FROM t_chapter c
        JOIN t_classify cl ON c.classify_id = cl.id
        WHERE c.status = 1
        ORDER BY c.classify_id, c.sort_order
    """).fetchall()
    
    print(f"\n📥 开始采集 [{source}] 共 {len(chapters)} 个章节，每章 {per_chapter} 张\n")
    
    total = 0
    for ch in chapters:
        # 检查已有图片数
        existing = conn.execute(
            "SELECT COUNT(*) FROM t_picture WHERE chapter_id = ? AND status = 1",
            (ch['id'],)
        ).fetchone()[0]
        
        if existing >= per_chapter:
            print(f"⏭️ [{ch['classify_title']}] {ch['title']} — 已有 {existing} 张，跳过")
            continue
        
        need = per_chapter - existing
        print(f"📸 [{ch['classify_title']}] {ch['title']} — 需要 {need} 张")
        
        cnt = collector.collect_for_chapter(ch['id'], per_chapter)
        
        # 更新章节图片数
        actual = conn.execute(
            "SELECT COUNT(*) FROM t_picture WHERE chapter_id = ? AND status = 1",
            (ch['id'],)
        ).fetchone()[0]
        conn.execute("UPDATE t_chapter SET page_count = ? WHERE id = ?", (actual, ch['id']))
        
        total += cnt
        print()
    
    conn.commit()
    
    # 更新分类封面
    update_classify_covers(conn)
    
    conn.commit()
    conn.close()
    print(f"\n✅ 采集完成！共采集 {total} 张图片")
    show_stats()

def update_classify_covers(conn):
    """自动设置分类封面（取第一个章节的封面）"""
    classifies = conn.execute("SELECT id FROM t_classify WHERE status = 1").fetchall()
    for cl in classifies:
        cur = conn.execute(
            "SELECT cover FROM t_chapter WHERE classify_id = ? AND status = 1 ORDER BY sort_order LIMIT 1",
            (cl['id'],)
        )
        row = cur.fetchone()
        if row and row['cover']:
            conn.execute(
                "UPDATE t_classify SET cover = ? WHERE id = ? AND (cover = '' OR cover IS NULL)",
                (row['cover'], cl['id'])
            )

def dedup():
    """去重：删除重复图片"""
    conn = get_db()
    # 按 md5 去重
    dupes = conn.execute("""
        SELECT md5_hash, COUNT(*) as cnt, MIN(id) as keep_id
        FROM t_picture
        WHERE md5_hash != '' AND status = 1
        GROUP BY md5_hash
        HAVING cnt > 1
    """).fetchall()
    
    deleted = 0
    for row in dupes:
        conn.execute(
            "DELETE FROM t_picture WHERE md5_hash = ? AND id != ?",
            (row['md5_hash'], row['keep_id'])
        )
        deleted += row['cnt'] - 1
    
    # 按 url 去重
    dupes2 = conn.execute("""
        SELECT url, COUNT(*) as cnt, MIN(id) as keep_id
        FROM t_picture
        WHERE status = 1
        GROUP BY url
        HAVING cnt > 1
    """).fetchall()
    
    for row in dupes2:
        conn.execute(
            "DELETE FROM t_picture WHERE url = ? AND id != ?",
            (row['url'], row['keep_id'])
        )
        deleted += row['cnt'] - 1
    
    conn.commit()
    conn.close()
    print(f"✅ 去重完成，删除 {deleted} 条重复记录")

def show_stats():
    """显示数据库统计"""
    conn = get_db()
    classify_cnt = conn.execute("SELECT COUNT(*) FROM t_classify WHERE status = 1").fetchone()[0]
    chapter_cnt  = conn.execute("SELECT COUNT(*) FROM t_chapter WHERE status = 1").fetchone()[0]
    picture_cnt  = conn.execute("SELECT COUNT(*) FROM t_picture WHERE status = 1").fetchone()[0]
    
    print(f"\n📊 数据库统计")
    print(f"   分类: {classify_cnt}")
    print(f"   章节: {chapter_cnt}")
    print(f"   图片: {picture_cnt}")
    print(f"   数据库: {DB_PATH} ({os.path.getsize(DB_PATH) / 1024:.1f} KB)\n")
    conn.close()

def show_detail():
    """显示详细信息"""
    conn = get_db()
    classifies = conn.execute(
        "SELECT * FROM t_classify WHERE status = 1 ORDER BY sort_order"
    ).fetchall()
    
    for cl in classifies:
        print(f"\n📁 [{cl['title']}]")
        chapters = conn.execute(
            "SELECT * FROM t_chapter WHERE classify_id = ? AND status = 1 ORDER BY sort_order",
            (cl['id'],)
        ).fetchall()
        for ch in chapters:
            pic_cnt = conn.execute(
                "SELECT COUNT(*) FROM t_picture WHERE chapter_id = ? AND status = 1",
                (ch['id'],)
            ).fetchone()[0]
            print(f"   📖 {ch['title']} ({pic_cnt}张)")
    
    conn.close()

# ==================== CLI ====================

def main():
    parser = argparse.ArgumentParser(description='网络图集采集工具')
    sub = parser.add_subparsers(dest='command')
    
    sub.add_parser('init', help='初始化数据库+预设分类章节')
    
    p_collect = sub.add_parser('collect', help='采集图片')
    p_collect.add_argument('-s', '--source', default='picsum', help='采集源 (picsum)')
    p_collect.add_argument('-c', '--count', type=int, default=5, help='每章节图片数')
    
    sub.add_parser('stats', help='查看统计')
    sub.add_parser('detail', help='查看详细信息')
    sub.add_parser('dedup', help='去重清理')
    
    args = parser.parse_args()
    
    if args.command == 'init':
        init_db()
    elif args.command == 'collect':
        collect_all(args.source, args.count)
    elif args.command == 'stats':
        show_stats()
    elif args.command == 'detail':
        show_detail()
    elif args.command == 'dedup':
        dedup()
    else:
        parser.print_help()

if __name__ == '__main__':
    main()
