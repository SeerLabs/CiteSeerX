from django.shortcuts import render_to_response

from citeseerx_crawl.main_crawl.models import Document

def handle_query(request):
    if 'url_prefix' in request.REQUEST:        
        url_prefix = request.REQUEST['url_prefix']
    else:
        url_prefix = ''
        
    if url_prefix:      
        show_results = True       
        
        if len(url_prefix) > 10: 
            doc_count = Document.objects.filter(url__startswith=url_prefix).count()
            docs = Document.objects.filter(url__startswith=url_prefix).order_by('-update_date')[:100]
            msg = ''
        else:
            doc_count = 0
            docs = []
            msg = 'Url prefix is too short'              
    else:
        show_results = False
        doc_count = -1
        docs = None
        msg = ''

        
    data = {
        'show_results': show_results,
        'doc_count': doc_count,
        'docs': docs,
        'url_prefix': url_prefix,
        'msg': msg
        }
    
    return render_to_response('query.htm', data)
    