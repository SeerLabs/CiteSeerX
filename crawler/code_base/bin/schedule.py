#!/usr/local/bin/python2.6

import config
import datetime
import os
import sys
import math
import crawler.submit
import time 
from urlparse import urlsplit
import re

os.environ['DJANGO_SETTINGS_MODULE'] = config.django_settings_module

from django.db import connection
from citeseerx_crawl.main_crawl.models import Document, ParentUrl

def select_urls():
    schedule_time = datetime.datetime.now()
    
    # count doc # of each parent. cause will rank upon #docs (JWU)
    parent_ndocs = {}
    cursor = connection.cursor()
    print "query from database ..."
    print ""
    dbquery = "SELECT p.last_crawl_date,w.id,w.url FROM main_crawl_whitelist2 w JOIN main_crawl_parenturl p ON w.url = p.url ORDER BY w.id"
    #dbquery = "SELECT p.last_crawl_date,w.rank,w.url FROM main_crawl_whitelist1 w JOIN main_crawl_parenturl p ON w.url = p.url WHERE p.is_live = 1 ORDER BY rank"

    print dbquery
    cursor.execute(dbquery) 
    rows = cursor.fetchall()    
    print "query finished"
    print ""

    parents_schedule = []               
    time_format = "%Y-%m-%d %H:%M:%S"   
    print "retrieving urls ..."
    print "urls to be retrived: ",len(rows)
    for row in rows:                   
        flag_DomainBlockRule = 0 # if violating, this flag =1
        timestring = row[0]           
        t_delta = datetime.datetime.now() - timestring 
        rank = float(row[1])                          
        url = row[2]                                   
        # add a filter here
        # url cannot be in the acm.org and ieee.org
        scheme, host, path, query, fragment = urlsplit(url)
        for domain in ['acm.org','ieee.org','osa.org']:
            if host.endswith(domain):
                print 'url blocked:violating DomainBlockRule:'+url
   		flag_DomainBlockRule = 1
		break
	
	if not flag_DomainBlockRule:
            parents_schedule.append((t_delta.days,rank,url))

    print ""

    # get time delta
    schedule_file_name = datetime.datetime.now().strftime('%Y.%m.%d_%H')
    schedule_file_path = os.path.join(config.schdule_dir, schedule_file_name)
    if not os.path.exists(config.schdule_dir):
        os.makedirs(config.schdule_dir)
    
    f = open(schedule_file_path, 'w')
    
    urls = []
    print "writting scheduled urls into file ..."
    if config.schedule_from > len(parents_schedule):
	print 'schedule_from is greater than the length of seed list: make is smaller'
	return -1

    schedule_from = config.schedule_from
    ndocs_to_schedule = config.ndocs_to_schedule
    if config.ndocs_to_schedule == -1:
      schedule_from = 0
      ndocs_to_schedule = len(parents_schedule)

    schedule_to = schedule_from + ndocs_to_schedule
    print "urls to be written: ",ndocs_to_schedule
    print 'schedule from %(from)s to %(to)s in seed list' % {'from':schedule_from,'to':schedule_to}
    # if config.schedule_from+ndocs_to_schedule is greater than the length of 
    # parents_schedule, the index will be truncated to the length of this list
    # there is not any index overflow!
    for x in parents_schedule[schedule_from:schedule_to]: # schedule all documents
        urls.append(x[2])
        f.write(str(x[0]) + '\t' + str(x[1]) + '\t' + x[2] + '\n')
        
        # update last_crawl_date of scheduled parents
        p = ParentUrl.objects.get(url=x[2])
        p.last_crawl_date = schedule_time
        p.save() 
    
    f.close()                
    print "writing urls to file finished"    
    return urls
    
def submit(urls):
    # obtain current date to yyyymmdd format
    batch = int(datetime.datetime.now().strftime('%Y%m%d'))
    s = crawler.submit.Submitter(
        config.amq_host,
        61613,
        config.amq_queue
        )
    s.connect_mq()

    for u in urls:
        s.submit(u, batch)

    s.disconnect_mq()

if __name__ == '__main__':
    print 'scheduling...'
    urls = select_urls()
    print 'submitting...'
    print urls.__class__
    submit(urls)
    print 'done'
