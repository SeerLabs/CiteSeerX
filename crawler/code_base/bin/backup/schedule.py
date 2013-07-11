#!/usr/bin/env python

import config

import datetime
import os
import sys
import math

import crawler.submit

os.environ['DJANGO_SETTINGS_MODULE'] = config.django_settings_module

from django.db import connection
from citeseerx_crawl.main_crawl.models import Document, ParentUrl

def select_urls():
    schedule_time = datetime.datetime.now()
        
    # count doc # of each parent
    parent_ndocs = {}
    cursor = connection.cursor()
    cursor.execute("select count(*) as ndocs,parent_id from main_crawl_document where parent_id is not null group by parent_id")
    rows = cursor.fetchall()
    
    for row in rows:
        ndocs = int(row[0])
        pid = row[1]
        parent_ndocs[pid] = ndocs

    # get time delta    
    parents_schedule = []
    
    parent_urls = ParentUrl.objects.filter(is_live__exact=True)
    for p in parent_urls:
        t_delta = datetime.datetime.now() - p.last_crawl_date
        if p.id in parent_ndocs:
            ndocs = parent_ndocs[p.id]
        else:
            ndocs = 0
        
        if ndocs < 2:
            ndocs = 2 # make sure log(ndocs) > 0 
        
        parents_schedule.append((t_delta.days, math.log(ndocs), p.url))                
    
    # sort
    parents_schedule.sort(cmp=compare_func)
    
    schedule_file_name = datetime.datetime.now().strftime('%Y.%m.%d_%H')
    schedule_file_path = os.path.join(config.schdule_dir, schedule_file_name)
    if not os.path.exists(config.schdule_dir):
        os.makedirs(config.schdule_dir)
    
    f = open(schedule_file_path, 'w')
    
    urls = []
    for x in parents_schedule[:config.ndocs_to_schedule]:
        urls.append(x[2])
        f.write(str(x[0]) + '\t' + str(x[1]) + '\t' + x[2] + '\n')
        
        # update last_crawl_date of scheduled parents
        p = ParentUrl.objects.get(url=x[2])
        p.last_crawl_date = schedule_time
        p.save() 
    
    f.close()                
        
    return urls
    
def compare_func(x, y):
    if y[0]*y[1] > x[0]*x[1]:
        return 1
    elif y[0]*y[1] < x[0]*x[1]:
        return -1
    else:
        return 0    

def submit(urls):
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
    submit(urls)
    print 'done'
