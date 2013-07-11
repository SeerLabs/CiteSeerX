#!/usr/bin/env python

import config

import os
import sys
import datetime

os.environ['DJANGO_SETTINGS_MODULE'] = config.django_settings_module

from citeseerx_crawl.main_crawl.models import Document, HostStat, DomainStat, TldStat

    
def get_history(host_suffix):
    class history_item(object):
        pass
    
    today = datetime.datetime.now()
    
    year_history = []     
    this_year = today.year
    year_count = 5
    
    for i in range(year_count):
        the_year = this_year - year_count + i + 1
        start = str(the_year) + '-1-1' 
        to = str(the_year + 1) + '-1-1'
        if host_suffix:
            num = Document.objects.filter(host__endswith=host_suffix).filter(discover_date__gte=start).filter(discover_date__lt=to).count()
        else:
            num = Document.objects.filter(discover_date__gte=start).filter(discover_date__lt=to).count()
        x = history_item()
        x.year = the_year
        x.count = num
        year_history.append(x)
        
    month_history = [] 
    this_month = today.month
    month_count = 12
        
    month = today.month
    year = today.year
    
    for i in range(month_count):        
        start = datetime.date(year, month, 1)
        
        if month < 12:
            to = datetime.date(year, month+1, 1)
        else:
            to = datetime.date(year+1, 1, 1)
                             
        if host_suffix:                                         
            num = Document.objects.filter(host__endswith=host_suffix).filter(discover_date__gte=start).filter(discover_date__lt=to).count()
        else:
            num = Document.objects.filter(discover_date__gte=start).filter(discover_date__lt=to).count()
        x = history_item()
        x.month = start.strftime('%Y-%m')
        x.count = num
        month_history.append(x) 
        
        month = month - 1
        
        if month == 0:
            month = 12
            year = year - 1       
    
    month_history.reverse()
    
    if host_suffix:
        in_sys_count = Document.objects.filter(host__endswith=host_suffix).filter(state__exact=1).count()
        todo_count = Document.objects.filter(host__endswith=host_suffix).filter(state__exact=0).count()
    else:
        in_sys_count = Document.objects.filter(state__exact=1).count()
        todo_count = Document.objects.filter(state__exact=0).count()
        
    # construct history string
    history_str = str(in_sys_count) + ' ' + str(todo_count)
    
    for x in year_history:
        history_str += ' ' + str(x.year) + ':' + str(x.count)                     
    
    for x in month_history:
        history_str += ' ' + str(x.month) + ':' + str(x.count)
         
    return history_str     

def update_tld_history():
    tld_stats = TldStat.objects.all()
    
    for entry in tld_stats:
        history_str = get_history(entry.tld)
        entry.history = history_str
        try:
            entry.save()
        except:
            exctype, value = sys.exc_info()[:2]
            sys.stderr.write("UnhandledError: %s, %s\n" % (exctype, value))        
        else:
            print 'tld', entry.id, 'saved' 

def update_domain_history():
    domain_stats = DomainStat.objects.order_by('-ndocs')
    
    for entry in domain_stats:
        history_str = get_history(entry.domain)
        entry.history = history_str
        try:
            entry.save()
        except:
            exctype, value = sys.exc_info()[:2]
            sys.stderr.write("UnhandledError: %s, %s\n" % (exctype, value))        
        else:
            print 'domain', entry.id, 'saved'
            
def update_host_history():
    host_stats = HostStat.objects.all()
    
    for entry in host_stats:
        history_str = get_history(entry.host)
        entry.history = history_str
        try:
            entry.save()
        except:
            exctype, value = sys.exc_info()[:2]
            sys.stderr.write("UnhandledError: %s, %s\n" % (exctype, value))        
        else:
            print 'host', entry.id, 'saved'                         

if __name__ == '__main__':       
    print 'updating tld history...'
    update_tld_history()

    print 'updating domain history...'
    update_domain_history()

    print 'updating host history...'
    #update_host_history()
    
    print 'done'
