import os
from xml.dom.minidom import getDOMImplementation

from django.http import HttpResponse, Http404
from django.shortcuts import render_to_response

from citeseerx_crawl.main_crawl.models import Document
import config

def get_docs_xml(request):
    docs = Document.objects.filter(state__exact=0).order_by('-id')[:config.ingest_limit]

    impl = getDOMImplementation()    
    xDoc = impl.createDocument(None, "response", None)
    root = xDoc.documentElement # why no argument here but Line 19 has?
    root.setAttribute("location", config.ingest_rep_dir)
    
    def add_node(tag, value):
        node = xDoc.createElement(tag)        
        node.appendChild(xDoc.createTextNode(value))
        root.appendChild(node)
        return node
        
    if 'key' in request.GET:
        key = request.GET['key']
        if key != config.api_key:
            error = 'invalid api key'
            add_node('error', error)
            return HttpResponse(xDoc.toxml(), mimetype='text/xml')            
    else:
        error = 'missing api key'
        add_node('error', error)
        return HttpResponse(xDoc.toxml(), mimetype='text/xml')             
        
    for d in docs:
        p = id2path(d.id)         
                
        doc_node = xDoc.createElement("doc")
        doc_node.setAttribute("id", str(d.id))
        doc_node.appendChild(xDoc.createTextNode(p))
        root.appendChild(doc_node)
            
    return HttpResponse(xDoc.toxml(), mimetype='text/xml')

def set_docs_xml(request):
    impl = getDOMImplementation()    
    xDoc = impl.createDocument(None, "response", None)
    root = xDoc.documentElement
    
    def add_node(tag, value):
        node = xDoc.createElement(tag)        
        node.appendChild(xDoc.createTextNode(value))
        root.appendChild(node)
        return node        
    
    if 'key' in request.GET:
        key = request.GET['key']
        if key != config.api_key:
            error = 'invalid api key'
            add_node('error', error)
            return HttpResponse(xDoc.toxml(), mimetype='text/xml')            
    else:
        error = 'missing api key'
        add_node('error', error)
        return HttpResponse(xDoc.toxml(), mimetype='text/xml')
            
    if 'ids' in request.GET and 'state' in request.GET:
        ids = request.GET['ids']
        state = request.GET['state']
        if state not in ['0', '1', '-1']:
            error = 'invalid state (must be 0, 1, or -1)'
            add_node('error', error)
            return HttpResponse(xDoc.toxml(), mimetype='text/xml')
    else:
        error = 'wrong parameters'
        add_node('error', error)
        return HttpResponse(xDoc.toxml(), mimetype='text/xml')
    
    id_list = ids.split(',')
    
    req_count = len(id_list)
    suc_count = 0
    fail_count = 0    

    for id in id_list:        
        try:
            doc_id = int(id)
            d = Document.objects.get(id=doc_id)
            d.state = state
            d.save()
        except ValueError:
            fail_count += 1
        except Document.DoesNotExist:
            fail_count += 1                    
        else:
            suc_count += 1
            
    add_node('SubmittedDocNumber', str(req_count))
    add_node('SuccessNumber', str(suc_count))
    add_node('FailNumber', str(fail_count))
            
    return HttpResponse(xDoc.toxml(), mimetype='text/xml')  
    
def id2path(id):
    p1 = id / 1000000
    p2 = (id % 1000000) / 1000
    p3 = id % 1000
    s1 = str(p1).zfill(3) 
    s2 = str(p2).zfill(3)
    s3 = str(p3).zfill(3)
    p = "%s/%s/%s/%s.%s.%s.pdf" % (s1, s2, s3, s1, s2, s3)
    return p
    
