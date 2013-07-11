import datetime

import gviz_api

from django.http import HttpResponse, Http404
from django.shortcuts import render_to_response

from citeseerx_crawl.main_crawl.models import ParentUrl, Document, Submission

import crawler.submit
import crawler.url_normalization

import config

def handle_submission(request):
    if request.method == 'GET':
        data = {}
        return render_to_response('submit.htm', data)
    elif request.method == 'POST':
        url = request.POST['url']
        email = request.POST['email']
        
        msg = 'Thank you for your submission.'
        
        # url normalization
        norm_url = crawler.url_normalization.get_canonical_url(url)
        
        if norm_url == '':
            data = {                    
                'err': 'Invalid URL.'
            }
            return render_to_response('submit.htm', data)                                
        
        if len(norm_url) > 255:
            data = {                    
                'err': 'The lengt of the URL cannot exceed 255.'
            }
            return render_to_response('submit.htm', data)
        
        # check if already exist
        p = None
        old_sub = None
        new_sub = None
        
        try:
            p = ParentUrl.objects.get(url=norm_url)            
            batch = 0 # added by jwu 03/20/2012
        except ParentUrl.DoesNotExist:
            try: 
                old_sub = Submission.objects.get(url=norm_url)                            
                batch = old_sub.id # added by jwu 03/20/2012
            except Submission.DoesNotExist:
                # only new urls will be saved to submission database
                new_sub = Submission(url=norm_url, email=email)
                new_sub.save()                        
                batch = new_sub.id # added by jwu 03/20/2012
        
        # submitted url will be recrawled (even it's old)
        #batch = int(datetime.datetime.now().strftime('%Y%m%d')) # commented out by jwu 03/20/2012
        
        s = crawler.submit.Submitter(config.amq_host, 61613, config.amq_queue)
        s.connect_mq()
        s.submit(url, batch)
        s.disconnect_mq()
        
        data = {                    
            'parent': p,
            'old_sub': old_sub,
            'new_sub': new_sub, 
            'msg': msg
        }        
        
        return render_to_response('submit.htm', data)
        
def tracking_parent(request, pid):
    try:
        p = ParentUrl.objects.get(id=pid)
    except ParentUrl.DoesNotExist:
        raise Http404
    
    docs = Document.objects.filter(parent__exact=p).order_by('-update_date')
    doc_count = len(docs)
        
    data = {     
        'url': p.url,  
        'doc_count': doc_count,
        'docs': docs,
        }
    
    return render_to_response('tracking.htm', data)

def tracking_sub(request, sid):
    try:
        sub = Submission.objects.get(id=sid)
    except Submission.DoesNotExist:
        raise Http404
        
    try:
        p = ParentUrl.objects.get(url=sub.url)
    except ParentUrl.DoesNotExist:
        data = {        
            'url': sub.url,
            'doc_count': 0,
            'docs': None,
            }
    else:
        docs = Document.objects.filter(parent__exact=p).order_by('-update_date')
        doc_count = len(docs)
        
        data = {        
            'url': sub.url,
            'doc_count': doc_count,
            'docs': docs,
            }
    
    return render_to_response('tracking.htm', data)  
    
def sub_stat(request):
    data = {}
    return render_to_response('sub_stat.htm', data)    

def sub_stat_data(request):
    day_history = []     
    day_count = 30
        
    today = datetime.date.today()                
    one_day = datetime.timedelta(1)
        
    for i in range(day_count, -1, -1):        
        start = today - one_day*i 
        to = start + one_day
                                         
        num = Submission.objects.filter(add_time__gte=start).filter(add_time__lt=to).count()
        day_history.append([start.strftime('%Y.%m.%d'), num])
    
    description = [
        ('Date', 'string'),
        ('Number', 'number')
        ]    
            
    data_table = gviz_api.DataTable(description)
    data_table.LoadData(day_history)
                
    out = data_table.ToJSonResponse()    
    return HttpResponse(out, mimetype='text/plain')
    
