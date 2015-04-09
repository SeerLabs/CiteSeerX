import datetime

import gviz_api

from django.http import HttpResponse, Http404
from captcha.fields import CaptchaField
from django.shortcuts import render_to_response
from django import forms 

from citeseerx_crawl.main_crawl.models import ParentUrl, Document, Submission

import crawler.submit
import crawler.url_normalization
import logging
import config
class CaptchaForm(forms.Form):
    url = forms.CharField(widget=forms.TextInput(attrs={'size': 80, 'class': 'text required url'}))
    email = forms.CharField(widget=forms.TextInput(attrs={'size': 80, 'class': 'text required email'}))
    captcha = CaptchaField()

class CaptchaForm_pub(forms.Form):
    url = forms.CharField(widget=forms.TextInput(attrs={'size': 80, 'class': 'text required url'}))
    email = forms.CharField(widget=forms.TextInput(attrs={'size': 80, 'class': 'text required email'}))
    submitter_name = forms.CharField(widget=forms.TextInput(attrs={'size': 80, 'class': 'text required submitter name'}))
    captcha = CaptchaField()

def handle_submission(request):
    if request.method == 'GET':
        form = CaptchaForm()
        data = {'form': form}
        return render_to_response('submit.htm', data)
    elif request.method == 'POST':
        form = CaptchaForm(request.POST)
        if form.is_valid():
            url = form.cleaned_data['url']
            email = form.cleaned_data['email']

            msg = 'Thank you for your submission.'

            norm_url = crawler.url_normalization.get_canonical_url(url)

            if norm_url =='':
                data = {
                   'err': 'Invalid URL.',
                   'form': form
                }
                return render_to_response('submit.htm', data)

            if len(norm_url) > 255:
                data = {
                   'err': 'The length of the URL canno exceed 255.',
                   'form': form
                }
                return render_to_response('submit.htm', data)

            # check if already exist
            p = None
            old_sub = None
            new_sub = None

            try:
                p = ParentUrl.objects.get(url=norm_url)
            except ParentUrl.DoesNotExist:
                try:
                    old_sub = Submission.objects.get(url=norm_url)

                except Submission.DoesNotExist:
                    # only new urls will be saved to submission database
                    new_sub = Submission(url=norm_url, email=email)
                    new_sub.save()

            # submitted url will be recrawled (even it's old)
            batch = int(datetime.datetime.now().strftime('%Y%m%d'))

            #s = crawler.submit.Submitter(config.amq_host, 61613, config.amq_queue)
            #s.connect_mq()
            #s.submit(url, batch)
            #s.disconnect_mq()

            data = {
                'parent': p,
                'old_sub': old_sub,
                'new_sub': new_sub,
                'msg': msg,
                'form': form
            }

            return render_to_response('submit.htm', data)
        else:
            data = {'form': form}
            return render_to_response('submit.htm', data)

# render publisher submission page
def handle_submission_pub(request):
    logging.basicConfig(level=logging.DEBUG,\
                        format="%(asctime)s %(name)s %(levelname)s %(message)s")
    logfh = logging.FileHandler("/data/tmp/handle_submission_pub.log",mode="a")
    logging.getLogger("").addHandler(logfh)
    logger = logging.getLogger("handle_submission_pub")
    if request.method == 'GET':
        logger.debug("reqeust method is GET")
        form = CaptchaForm_pub()
        data = {'form': form}
        return render_to_response('submit_pub.htm', data)
    elif request.method == 'POST':
        logger.debug("reqeust method is POST")
        form = CaptchaForm_pub(request.POST)
        if form.is_valid():
            url = form.cleaned_data['url']
            email = form.cleaned_data['email']
            submitter_name = form.cleaned_data["submitter_name"]

            msg = 'Thank you for your submission.'

            norm_url = crawler.url_normalization.get_canonical_url(url)

            if norm_url =='':
                data = {
                   'err': 'Invalid URL.',
                   'form': form
                }
                return render_to_response('submit_pub.htm', data)

            if len(norm_url) > 255:
                data = {
                   'err': 'The length of the URL canno exceed 255.',
                   'form': form
                }
                return render_to_response('submit_pub.htm', data)

            # check if already exist
            p = None
            old_sub = None
            new_sub = None

            try:
                p = ParentUrl.objects.get(url=norm_url)
                logger.debug("URL found in submission_pub table")
            except ParentUrl.DoesNotExist:
                try:
                    old_sub = Submission.objects.get(url=norm_url)
                    
                except Submission.DoesNotExist:
                    logger.debug("URL inserted into submission_pub table")
                    # only new urls will be saved to submission_pub database
                    new_sub = Submission(url=norm_url, email=email, submitter_name=submitter_name)
                    new_sub.save()

            # submitted url will be recrawled (even it's old)
            batch = int(datetime.datetime.now().strftime('%Y%m%d'))

            data = {
                'parent': p,
                'old_sub': old_sub,
                'new_sub': new_sub,
                'msg': msg,
                'form': form
            }

            return render_to_response('submit_pub.htm', data)
        else:
            data = {'form': form}
            return render_to_response('submit_pub.htm', data)
        
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
    
