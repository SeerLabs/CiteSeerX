#!/usr/bin/env python

import config

import os
import sys
from hashlib import md5
import re
import urlparse

from crawler.url_normalization import get_canonical_url, host2domain

os.environ['DJANGO_SETTINGS_MODULE'] = config.django_settings_module

from citeseerx_crawl.main_crawl.models import HostStat, DomainStat, TldStat

hosts = {}
domains = {}
tlds = {}

def stat(urls_file_path, ncites):
    f = open(urls_file_path, 'r')
    for line in f:
        parts = line[:-1].split('\t')
        code = parts[0]
        url = parts[1]        
        
        scheme, host, path, query, fragment = urlparse.urlsplit(url)
        
        if host.find(':') != -1:
            parts = host.split(':')
            host = parts[0]
        
        tld = host.split('.')[-1]
        domain = host2domain(host)
        
        if re.match(r'^\d+$', tld) != None:
            # tld is a number, which is part of a IP address
            continue
        
        if tld == '': # caused by invalid host name
            continue
            
        cite_num = 0
        if code in ncites:
            cite_num = ncites[code]
        
        if host in hosts:                        
            hosts[host][0] += 1
            hosts[host][1] += cite_num
        else:            
            hosts[host] = [1, cite_num]
        
        if tld in tlds:
            tlds[tld][0] += 1
            tlds[tld][1] += cite_num
        else:
            tlds[tld] = [1, cite_num]
        
        if domain in domains:
            domains[domain][0] += 1
            domains[domain][1] += cite_num
        else:
            domains[domain] = [1, cite_num]             
             
                       
    f.close()                
    
    print len(hosts), len(tlds), len(domains)
    
def save_hosts():
    for k, v in hosts.iteritems():        
        try:
            entry = HostStat.objects.get(host=k)
            entry.ndocs = v[0]
            entry.ncites = v[1]
        except HostStat.DoesNotExist:
            entry = HostStat(
                host=k,
                ndocs=v[0],
                ncites=v[1])
        
        try:
            entry.save()
        except:
            exctype, value = sys.exc_info()[:2]
            sys.stderr.write("UnhandledError: %s, %s\n" % (exctype, value))        
        else:
            print 'host', entry.id, 'saved'    

def save_domains():
    for k, v in domains.iteritems():        
        try:
            entry = DomainStat.objects.get(domain=k)
            entry.ndocs = v[0]
            entry.ncites = v[1]
        except DomainStat.DoesNotExist:
            entry = DomainStat(
                domain=k,
                ndocs=v[0],
                ncites=v[1])
        
        try:
            entry.save()
        except:
            exctype, value = sys.exc_info()[:2]
            sys.stderr.write("UnhandledError: %s, %s\n" % (exctype, value))        
        else:
            print 'domain', entry.id, 'saved'

def save_tlds():
    for k, v in tlds.iteritems():
        try:
            entry = TldStat.objects.get(tld=k)
            entry.ndocs = v[0]
            entry.ncites = v[1]
        except TldStat.DoesNotExist:
            entry = TldStat(
                tld=k,
                ndocs=v[0],
                ncites=v[1])
        
        try:
            entry.save()
        except:
            exctype, value = sys.exc_info()[:2]
            sys.stderr.write("UnhandledError: %s, %s\n" % (exctype, value))        
        else:
            print 'tld', entry.id, 'saved'

def load_ncites_data(ncites_file_path):
    ncites = {}
    f = open(ncites_file_path, 'r')
    for line in f:
        parts = line[:-1].split('\t')
        url = get_canonical_url(parts[0])
        code = md5(url).hexdigest()
        cite_num = int(parts[1])
        if code not in ncites:
            ncites[code] = cite_num
        else:
            ncites[code] += cite_num
            
    f.close()

    return ncites


if __name__ == '__main__':    
    print 'loading ncites file (takes about 1 min)...'
    ncites = load_ncites_data(config.ncites_file_path)
    
    print 'processing urls (takes about 1 min)...'    
    stat(config.urls_file_path, ncites)
    
    print 'saving stats to db (takes about 5 min)...'
    save_hosts()
    save_domains()
    save_tlds()
    
    print 'done'
