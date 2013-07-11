#!/usr/bin/env

import re

#link_pattern_str = r'href\s*=\s*[\'"](?P<u>[^\'"<>]+)[\'"]'
link_pattern_str = r'<a\s+[^<>]*href\s*=\s*[\'"](?P<u>[^\'"<>]+)[\'"][^<>]*>(?P<ah>.*)</a>'
link_pattern = re.compile(link_pattern_str, re.I|re.U)

tag_pattern_str = r'<[^<>]*>?'
tag_pattern = re.compile(tag_pattern_str, re.I|re.U)

def get_links(html):
    links = []    
    for m in link_pattern.finditer(html):
        link = m.group('u')
        anchor_html = m.group('ah')    
        
        if link and link !='#':      
            link = link.replace('\n', '')      
            link = link.replace('\r', '')
            link = link.replace('\t', '')            
        else:
            continue         
           
        if anchor_html:
            anchor_text = tag_pattern.sub('', anchor_html)
            
            anchor_text = anchor_text.replace('\n', '')      
            anchor_text = anchor_text.replace('\r', '')
            anchor_text = anchor_text.replace('\t', '') 
        else:
            anchor_text = ''
        
        links.append((link, anchor_text))
   
    return links

charset_pattern_str = r'<meta\s*http-equiv\s*=\s*[\'"]?content-type[\'"]?\s*content\s*=\s*[\'"]?[^\'"<>;]+;\s*charset=(?P<c>[a-zA-Z0-9-]+)[\'"]?\s*/?>'

charset_pattern = re.compile(charset_pattern_str, re.I|re.U)

def get_charset(html):
    charset = None
    
    m = charset_pattern.search(html)
    if m is not None:
        charset = m.group('c')
    
    return charset
    