#!/usr/bin/env python

from urlparse import urlsplit
import urllib
import os
import runconfig

try:
    # for python 2.6
    from urlparse import parse_qsl
except:
    # for python < 2.6
    from cgi import parse_qsl

def looks_like_dir(url):
    scheme, host, path, query, fragment = urlsplit(url)
    
    if query != '':
        # if it has query, it cannot be a dir
        is_dir = False
    elif len(path) == 0:    
        # if no path, it must be a dir (host root)
        is_dir = True
    elif path.endswith('/'):
        # if path ends with '/', it must be a dir
        is_dir = True
    else:
        segments = path.split('/')
        last_segment = segments[-1]
        if last_segment.find('.') != -1:
            # last segment contains ".", it MAYBE a file
            is_dir = False
        else:
            # last segment contains no ".", it MAYBE a dir
	    # This causes some problems. Further actions are needed to correct this. (Jian Wu, 2013-4-19)
            is_dir = True
    
    return is_dir

def get_canonical_url(link):
    """
    get_canonical_url() behaves weirdly under python 2.4:
    call get_canonical_url(u'http://www.abc.com') first, then all following calls will output unicode
    call get_canonical_url('http://www.abc.com') first, then all following calls will output string
    seems like some module of python 2.4 have a memory of its first call, should be a bug
    python 2.5, 2.6 has no such problem
    """ 
    
    scheme, host_port, path, query, fragment = urlsplit(link)
    
    """
    For example, if a url is "http://www.cwi.nl:80/%7Eguido/Python.html"
    running the command above gives:
    scheme: http
    host_port: www.cwi.nl:80
    path: /%7Eguido/Python.html
    query: ''
    fragment: ''
    """

    """
    url length cannot exceed a certain number of characters
    if len(link) > runconfig.urlmaxlen:
	return ''
    """

    if not scheme or not host_port:
        return ''
    
    host_port = host_port.lower()
    
    if path == '' and query != '':
        path = '/'
    
    if path.find('/../') != -1 or path.find('/./') != -1 or path.endswith('/..') or path.endswith('/.'):
        # do path normalization
        norm_path = os.path.normpath(path)
        
        if not norm_path.endswith('/'):
            # we may need to add '/'        
            if path.endswith('/') or path.endswith('/..') or path.endswith('/.'):
                norm_path += '/'
        
        path = norm_path
        
    # for path, unquote first then quote
    path = urllib.unquote(path)
    # this statement will encode all characters regardlessly, which is the 
    # original code by Shuyi
    #path = urllib.quote(path)
   
    # this statement will stop encoding all the quoted characters
    #path = urllib.quote(path,safe="%/:=&?~#+!$,;'@()*[]")

    # but right now, we only have problem with parenthesis
    path = urllib.quote(path,safe="%/:=&?~#+!$,;'@()*[] ")
    
    # for query, encode and sort
    if query:
        params = parse_qsl(query)
        params.sort()
        query = urllib.urlencode(params)
                
    url = scheme + '://' + host_port + path    
    if query:
        url += '?' + query    

    # Add "/" at the end if it is a folder
    if looks_like_dir(url) and (not url.endswith('/')):
        url += '/'
        
    return url

def host2domain(host):    
    parts = host.split('.')
    n = len(parts)
    
    domain = ''
    
    if n <= 2:
        domain = host
    else:
        tld = parts[n-1]
        t2 = parts[n-2]
        t3 = parts[n-3]
        
        l2domain = t2 + "." + tld
        l3domain = t3 + "." + l2domain
        
        if tld == "de":
            domain = l2domain
        elif tld == "at":            
            if t2 in {"gv":1, "ac":1, "co":1, "or":1, "priv":1}:
                domain = l3domain
            else:
                domain = l2domain            
        elif tld == "au":            
            if t2 in {"asn":1, "com":1, "net":1, "id":1, "org":1, "csiro":1, "oz":1, "info":1, "conf":1, "act":1, "nsw":1, "nt":1, "qld":1, "sa":1, "tas":1, "vic":1, "wa":1, "edu":1, "gov":1}:
                domain = l3domain
            else:
                domain = l2domain      
        elif tld == "be":            
            if t2 in {"ac":1}:
                domain = l3domain
            else:
                domain = l2domain                          
        elif tld == "ca":            
            if t2 in {"ab":1, "bc":1, "mb":1, "nb":1, "nf":1, "nl":1, "ns":1, "nt":1, "nu":1, "on":1, "pe":1, "qc":1, "sk":1, "yk":1}:
                domain = l3domain
            else:
                domain = l2domain                
        elif tld == "ch":            
            if t2 in {"com":1, "gov":1, "net":1, "org":1}:
                domain = l3domain
            else:
                domain = l2domain
        elif tld == "cn":            
            if t2 in {"com":1, "edu":1, "gov":1, "net":1, "org":1}:
                domain = l3domain
            else:
                domain = l2domain
        elif tld == "es":            
            if t2 in {"com":1, "edu":1, "gob":1, "nom":1, "org":1}:
                domain = l3domain
            else:
                domain = l2domain       
        elif tld == "fr":            
            if t2 in {"tm":1, "asso":1, "nom":1, "prd":1, "presse":1, "com":1, "gouv":1}:
                domain = l3domain
            else:
                domain = l2domain       
        elif tld == "hk":            
            if t2 in {"com":1, "edu":1, "gov":1, "net":1, "org":1, "idv":1}:
                domain = l3domain
            else:
                domain = l2domain                      
        elif tld == "il":            
            domain = l3domain           
        elif tld == "in":            
            if t2 in {"co":1, "firm":1, "net":1, "org":1, "gen":1, "ind":1, "nic":1, "ac":1, "edu":1, "res":1, "gov":1, "mil":1}:
                domain = l3domain
            else:
                domain = l2domain                                      
        elif tld == "jp":            
            if t2 in {"ac":1, "ad":1, "co":1, "ed":1, "go":1, "gr":1, "lg":1, "ne":1, "or":1}:
                domain = l3domain
            else:
                domain = l2domain       
        elif tld == "kr":            
            if t2 in {"co":1, "or":1, "ac":1}:
                domain = l3domain
            else:
                domain = l2domain     
        elif tld == "nz":            
            domain = l3domain               
        elif tld == "sg":            
            if t2 in {"com":1, "edu":1, "gov":1, "net":1, "org":1, "per":1, "idn":1}:
                domain = l3domain
            else:
                domain = l2domain                                   
        elif tld == "tw":            
            if t2 in {"edu":1, "gov":1, "mil":1, "com":1, "net":1, "org":1, "idv":1, "game":1, "ebiz":1, "club":1}:
                domain = l3domain
            else:
                domain = l2domain
        elif tld == "uk":            
            if t2 == "sch":
                if n >= 4:
                    domain = parts[n-4] + "." + l3domain
                else:
                    domain = l3domain
            else:
                domain = l3domain      
                           
        else:        
            domain = l2domain
            
        # temp code since not all TLDs are considered above
        if domain == l2domain and t2 in {"edu":1, "gov":1, "com":1, "org":1}:
            domain = l3domain
    
    return domain    
