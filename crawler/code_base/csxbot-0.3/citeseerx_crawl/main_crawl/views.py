import time
import datetime

from django.http import HttpResponse, Http404
from django.shortcuts import render_to_response

from citeseerx_crawl.main_crawl.models import ParentUrl, Document, Submission, HostStat, DomainStat, TldStat
   
def overall_history(request):
    class history_item(object):
        pass
    
    today = datetime.datetime.now()
    
    #---------------------------------------------------------------------------
    year_history = []     
    this_year = today.year
    year_count = 5
    
    for i in range(year_count):
        the_year = this_year - year_count + i + 1
        start = str(the_year) + '-1-1' 
        to = str(the_year + 1) + '-1-1'
        num = Document.objects.filter(discover_date__gte=start).filter(discover_date__lt=to).count()
        x = history_item()
        x.year = the_year
        x.count = num
        year_history.append(x)
    
    #---------------------------------------------------------------------------
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
    
    #---------------------------------------------------------------------------
    day_history = []     
    day_count = 30
        
    month = today.month
    year = today.year
    day = today.day
    
    this_day = datetime.date(year, month, day)
    
    one_day = datetime.timedelta(1)
    
    for i in range(day_count, -1, -1):        
        start = this_day - one_day*i 
        to = start + one_day
                                     
        num = Document.objects.filter(discover_date__gte=start).filter(discover_date__lt=to).count()
        x = history_item()
        x.day = start.strftime('%Y-%m-%d')
        x.count = num
        day_history.append(x)
                    
    #---------------------------------------------------------------------------    
    in_sys_count = Document.objects.filter(state__exact=1).count()
    todo_count = Document.objects.filter(state__exact=0).count()
    fail_count = Document.objects.filter(state__exact=-1).count()
    
    data = {
        'host_suffix': '',
        'year_history': year_history,
        'month_history': month_history,
        'day_history': day_history,
        'in_sys_count': in_sys_count,
        'todo_count': todo_count,
        'fail_count': fail_count
        }        
    
    return render_to_response('overall_history.htm', data)  

def history(request, group_by, name):
    class history_item(object):
        pass

    history_str = ''
    if group_by == 'domain':
        try:
            entry = DomainStat.objects.get(domain=name)
            history_str = entry.history
        except DomainStat.DoesNotExist:
            raise Http404    
    elif group_by == 'tld':
        try:
            entry = TldStat.objects.get(tld=name)
            history_str = entry.history
        except TldStat.DoesNotExist:
            raise Http404 
    else:
        raise Http404
        
    if history_str:    
        parts = history_str.split(' ')
        in_sys_count = parts[0]
        todo_count = parts[1]
        
        year_history = []
        for p in parts[2:7]:
            x = history_item()
            x.year = p.split(':')[0]
            x.count = p.split(':')[1]
            year_history.append(x)
    
        month_history = []
        for p in parts[7:]:
            x = history_item()
            x.month = p.split(':')[0]
            x.count = p.split(':')[1]
            month_history.append(x)      
        
        data = {
            'host_suffix': name,
            'year_history': year_history,
            'month_history': month_history,
            'in_sys_count': in_sys_count,
            'todo_count': todo_count
            }        
        
        return render_to_response('history.htm', data)
    else:
        raise Http404       
