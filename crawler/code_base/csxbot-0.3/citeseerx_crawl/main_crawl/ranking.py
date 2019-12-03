import os
import math
from xml.dom.minidom import getDOMImplementation

from django.http import HttpResponse, Http404
from django.shortcuts import render_to_response
from django.db.models import Sum
from django.template import Context, loader
from django.db import connection

from citeseerx_crawl.main_crawl.models import HostStat, DomainStat, TldStat

import config

# only get the first 10000 rankings for domains (full list contains 300,000+)
def ndocs_rank(request, group_by):
    if group_by == 'domain':        
        total_docs = DomainStat.objects.all().aggregate(total_docs=Sum('ndocs'))['total_docs']
        items = DomainStat.objects.order_by('-ndocs')[:10000]
    elif group_by == 'tld':
        total_docs = TldStat.objects.all().aggregate(total_docs=Sum('ndocs'))['total_docs']
        items = TldStat.objects.order_by('-ndocs')
    else:
        raise Http404
    
    impl = getDOMImplementation()    
    xml_doc = impl.createDocument(None, "%s_ndocs_rank" % group_by, None)
    root = xml_doc.documentElement
       
    i = 0 
    accu_num = 0  
    for x in items:
        i += 1        
        accu_num += x.ndocs # accumulated document number
        
        x.order = i           # small number - higher order
        x.citespdoc = float(x.ncites) / float(x.ndocs) # number of citations per document 
        x.accu_num = accu_num # accumulated document number
        x.accu_per = float(accu_num) / float(total_docs)# accumulated percentage
        
        if group_by == 'domain':
            x.name = x.domain
        elif group_by == 'tld':
            x.name = x.tld
        
        node = xml_doc.createElement(group_by)
        node.setAttribute('name', x.name)
        node.setAttribute('rank', str(i))
        node.setAttribute('ndocs', str(x.ndocs))
        node.setAttribute('ncites', str(x.ncites))
        node.setAttribute('citespdoc', str(x.citespdoc))#jwu
        node.setAttribute('accu_num', str(x.accu_num))
        node.setAttribute('accu_per', str(x.accu_per))
        
        root.appendChild(node)
        
    
    t = loader.get_template('ndocs_rank.htm')
    c = Context({
        'items': items,
        'group_by': group_by
    })
    html = t.render(c) 
    
    output_file = os.path.join(config.stat_dir, group_by + '_ndocs_rank.htm')
    f = open(output_file, 'w')
    f.write(html)
    f.close()
    
    output_file = os.path.join(config.stat_dir, group_by + '_ndocs_rank.xml')
    f = open(output_file, 'w')
    f.write(xml_doc.toxml())        
    f.close()
    
    return HttpResponse(html)

# only get the first 10000 rankings for domains (full list contains 300,000+)    
def ncites_rank(request, group_by):  
    if group_by == 'domain':        
        total_cites = DomainStat.objects.all().aggregate(total_cites=Sum('ncites'))['total_cites']
        items = DomainStat.objects.filter(ncites__gt=0).order_by('-ncites')[:10000]
    elif group_by == 'tld':
        total_cites = TldStat.objects.all().aggregate(total_cites=Sum('ncites'))['total_cites']
        items = TldStat.objects.filter(ncites__gt=0).order_by('-ncites')
    else:
        raise Http404
        
    impl = getDOMImplementation()    
    xml_doc = impl.createDocument(None, "%s_ncites_rank" % group_by, None)
    root = xml_doc.documentElement              
    
    i = 0 
    accu_num = 0    
    for x in items:
        i += 1        
        accu_num += x.ncites
        
        x.order = i
        x.accu_num = accu_num
        x.accu_per = float(accu_num) / float(total_cites)  
        
        if group_by == 'domain':
            x.name = x.domain
        elif group_by == 'tld':
            x.name = x.tld

        node = xml_doc.createElement(group_by)
        node.setAttribute('name', x.name)
        node.setAttribute('rank', str(i))
        node.setAttribute('ndocs', str(x.ndocs))
        node.setAttribute('ncites', str(x.ncites))
        node.setAttribute('accu_num', str(x.accu_num))
        node.setAttribute('accu_per', str(x.accu_per))
        
        root.appendChild(node)                                  
    
    t = loader.get_template('ncites_rank.htm')
    c = Context({
        'items': items,
        'group_by': group_by
    })
    html = t.render(c) 
    
    output_file = os.path.join(config.stat_dir, group_by + '_ncites_rank.htm')
    f = open(output_file, 'w')
    f.write(html)    
    f.close()
    
    output_file = os.path.join(config.stat_dir, group_by + '_ncites_rank.xml')
    f = open(output_file, 'w')
    f.write(xml_doc.toxml())        
    f.close()    
    
    return HttpResponse(html)     
    
def cpd_rank(request, group_by):    
    items = []
     
    if group_by == 'domain':        
        items.extend(DomainStat.objects.filter(ncites__gt=0).filter(ndocs__gte=100).order_by('-ncites'))
    elif group_by == 'tld':
        items.extend(TldStat.objects.filter(ncites__gt=0).filter(ndocs__gte=100).order_by('-ncites'))
    else:
        raise Http404
                
    for x in items:                    
        x.cpd = float(x.ncites)/float(x.ndocs)        
    
    items.sort(cmp=lambda x,y: cmp(y.cpd, x.cpd))
    
    impl = getDOMImplementation()    
    xml_doc = impl.createDocument(None, "%s_cpd_rank" % group_by, None)
    root = xml_doc.documentElement 
    
    i = 0
    for x in items:
        i += 1
        x.order = i      

        if group_by == 'domain':
            x.name = x.domain
        elif group_by == 'tld':
            x.name = x.tld

        node = xml_doc.createElement(group_by)
        node.setAttribute('name', x.name)
        node.setAttribute('rank', str(i))
        node.setAttribute('ndocs', str(x.ndocs))
        node.setAttribute('ncites', str(x.ncites))
        node.setAttribute('cpd', str(x.cpd))
        
        root.appendChild(node)              

    t = loader.get_template('cpd_rank.htm')
    c = Context({
        'items': items,
        'group_by': group_by
    })
    html = t.render(c) 
    
    output_file = os.path.join(config.stat_dir, group_by + '_cpd_rank.htm')
    f = open(output_file, 'w')
    f.write(html)    
    f.close()
    
    output_file = os.path.join(config.stat_dir, group_by + '_cpd_rank.xml')
    f = open(output_file, 'w')
    f.write(xml_doc.toxml())        
    f.close()       
    
    return HttpResponse(html)
    
def country_ndocs_rank(request):
    country_list = []
    
    parent_ndocs = {}
    cursor = connection.cursor()
    cursor.execute("select sum(ndocs) as ndocs_total, country from main_crawl_domainstat where country != '' group by country order by ndocs_total desc")
    rows = cursor.fetchall()
    
    for row in rows:
        ndocs = int(row[0])
        country = row[1]
        log_ndocs = math.log(ndocs) 
        country_list.append({'ndocs':ndocs, 'country':country, 'log_ndocs':log_ndocs})            
    
    country_count = len(country_list)
    
    domain_list = []
    detail_country = ''
    domain_count = 0
    total_domain_count = 0    
    
    if 'cc' in request.GET:
        detail_country = request.GET['cc']
        total_domain_count = DomainStat.objects.filter(country__exact=detail_country).count()
        
        if total_domain_count > 0:
            
            if total_domain_count > 300: 
                #domain_list = DomainStat.objects.filter(country__exact=detail_country).filter(ndocs__gt=100).order_by('-ndocs')
                domain_list = DomainStat.objects.filter(country__exact=detail_country).order_by('-ndocs')[:300]
            else:
                domain_list = DomainStat.objects.filter(country__exact=detail_country)                
                
            domain_count = len(domain_list)
        else:
            detail_country = '' # invalid country        
    
    data = {
        'country_list': country_list,
        'country_count': country_count,
        'detail_country': detail_country,
        'domain_list': domain_list,
        'domain_count': domain_count,
        'total_domain_count': total_domain_count
    }        
        
    return render_to_response('country_ndocs_rank.htm', data)    
