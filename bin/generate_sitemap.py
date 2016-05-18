#!/usr/bin/python
# Script to generate sitemaps
# Douglas Jordan
# Requires mysql-python

import MySQLdb
import argparse
import logging
import os
import sys

logging.basicConfig(level=logging.INFO)
parser = argparse.ArgumentParser()
parser.add_argument("sitemapdir")
args = parser.parse_args()
try:
    sitemapdir = args.sitemapdir
except:
    logging.error("sitemap dir not set. run python generate_sitemap.py -h")
    sys.exit(0)
   
MAX_PER_FILE = 49999
db = MySQLdb.connect(host="csxdb03", user="csx-prod", passwd="csx-prod", db="citeseerx")
cur = db.cursor()
i = 0
file = 1
header = '<?xml version="1.0" encoding="UTF-8"?>\n<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">\n'

cur.execute("SELECT id FROM papers WHERE public = 1")
for row in cur.fetchall():
    if i == 0:
        try:
            f = open(os.path.join(sitemapdir,"sitemap%d.xml" % file), 'w+')
        except IOError:
            os.makedirs()
        f.write(header)
    f.write('<url>\n\t<loc>http://citeseerx.ist.psu.edu/viewdoc/download?doi=%s&rep=rep1&type=pdf</loc>\n</url>\n' % row[0])
    i = i + 1
    if i == MAX_PER_FILE:
        file = file + 1
        i = 0
        f.write('</urlset>')
        f.close()
        logging.info("sitemap generated: {}".format(f.name))
if not f.closed: 
    f.write('</urlset>')
    f.close()
    logging.info("sitemap generated: {}".format(f.name))

f = open(os.path.join(sitemapdir,'sitemap_index.xml'), 'w+')
f.write('<?xml version="1.0" encoding="UTF-8"?>\n<sitemapindex xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">\n')
for i in range(1, file+1):
    f.write('<sitemap>\n\t<loc>http://citeseerx.ist.psu.edu/sitemap%d.xml</loc>\n</sitemap>\n' % i)

f.write('</sitemapindex>');
f.close()
logging.info("sitemap index file: {}".format(f.name))
