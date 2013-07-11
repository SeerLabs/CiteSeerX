#!/bin/bash
# dump data for stat (takes 1-2 minutes)
# usage: this

mysql -h brick3.ist.psu.edu -u csx-devel -pcsx-devel -D citeseerx --batch --skip-column-names -e "select url,ncites from papers,urls where urls.paperid=papers.id" > /tmp/url_ncites

mysql -h thelma.ist.psu.edu -u csx -pcsx -D citeseerx_crawl --batch --skip-column-names -e "select md5,url from main_crawl_document" > /tmp/urls
