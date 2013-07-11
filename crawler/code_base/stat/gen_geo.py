#!/usr/bin/env python
# python 2.5+ required
# pygeoip required

import os
import sys
import datetime
from subprocess import Popen, call, PIPE

import pygeoip

import config
os.environ['DJANGO_SETTINGS_MODULE'] = config.django_settings_module
from citeseerx_crawl.main_crawl.models import HostStat, DomainStat


def host2ip(host):
    args = ['host', host]    
    proc = Popen(args, stdout=PIPE, stderr=PIPE)
    stdout, stderr = proc.communicate()
    lines = stdout.split('\n')
    
    ip = ''
    for line in lines:
        pos = line.find('has address')        
        if pos != -1:
            pre_length = pos + 12
            ip = line[pre_length:]
    
    return ip
   
def gen_geo():
    gic = pygeoip.GeoIP(config.geoip_data_file)
    
    #domain_stats = DomainStat.objects.all()
    domain_stats = DomainStat.objects.filter(ip__exact='')
    
    for entry in domain_stats:
        try:
            ip = host2ip(entry.domain)
            
            if ip == '':
                host_stats = HostStat.objects.filter(host__endswith=entry.domain)
                if len(host_stats) > 0:
                    ip = host2ip(host_stats[0].host)                
            
            if ip:
                result = None
                try:
                    result = gic.record_by_addr(ip)
                except pygeoip.GeoIPError:
                    sys.stderr.write("bad ip for %s: %s\n" % (entry.domain, ip))
                    
                if result:
                    entry.ip = ip
                    entry.latitude = float(result['latitude'])
                    entry.longitude = float(result['longitude'])
                    entry.country = result['country_code']
                
                    entry.save()
                    print entry.domain, '->', ip
                else:
                    sys.stderr.write("no record for %s: %s\n" % (entry.domain, ip))
            else:
                sys.stderr.write("no ip for %s\n" % entry.domain)                
        except:     
            exctype, value = sys.exc_info()[:2]
            sys.stderr.write("UnhandledError: %s, %s\n" % (exctype, value))    

if __name__ == '__main__':    
    gen_geo()
    
    print 'done' 
