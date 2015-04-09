#!/usr/bin/python
# Script to generate sitemaps
# Douglas Jordan
# Requires mysql-python

import MySQLdb

MAX_PER_FILE = 49999
db = MySQLdb.connect(host="csxdb03", user="csx-devel", passwd="csx-devel", db="citeseerx")
cur = db.cursor()
i = 0
file = 1
header = '<?xml version="1.0" encoding="UTF-8"?>\n<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">\n'

cur.execute("SELECT id FROM papers WHERE public = 1")
for row in cur.fetchall():
    if i == 0:
        f = open('sitemap/sitemap%d.xml' % file, 'w+')
        f.write(header)
    f.write('<url>\n\t<loc>http://citeseerx.ist.psu.edu/viewdoc/summary?doi=%s</loc>\n</url>\n' % row[0])
    i = i + 1
    if i == MAX_PER_FILE:
        file = file + 1
        i = 0
        f.write('</urlset>')
        f.close()

f = open('sitemap/sitemap_index.xml', 'w+')
f.write('<?xml version="1.0" encoding="UTF-8"?>\n<sitemapindex xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">\n')

for i in range(0, file+1):
    f.write('<sitemap>\n\t<loc>http://citeseerx.ist.psu.edu/sitemap%d.xml</loc>\n</sitemap>\n' % i)

f.write('</sitemapindex>');
f.close()
