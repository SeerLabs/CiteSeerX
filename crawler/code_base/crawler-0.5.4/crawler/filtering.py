#!/usr/bin/env python2.4
from urlparse import urlsplit
import re
    
class Filter(object):
    def __init__(self, name, rule_config_string, master):
        self._master = master
        self.name = name
        self._rules = []
        self.receiver = None
        
        parts = rule_config_string.split(' ')
        for p in parts:
            segments = p.strip().split('::')
            rule_class = segments[0]
            rule = globals()[rule_class](segments[1:])
            
            # set special properties
            if rule_class == 'SeedHostRule':
                rule._seed_hosts = master.seed_hosts           
            
            self._rules.append(rule)

    def check(self, r):
        for rule in self._rules:
            if not rule.check(r):
                r.filtered_by = self.name + "." + rule.__class__.__name__                 
                return False 
                
        return True
    
    def put(self, r):
        """
        set receiver before calling put
        """
            
        if self.check(r):
            self.receiver.put(r)        


class SchemeRule(object):
    def __init__(self, params):
        pass
    
    def check(self, r):
        if r.scheme == 'http' or r.scheme == 'https':
            return True
        else:
            return False

# return True: URL has the same domain name as its parent
# return False: otherwise
class HostParentRule(object):
    def __init__(self,params):
        pass

    def check(self,r):
        """return true for seed urls"""
        if r.is_seed:
            return True

        """check parent URLs for non-seed  URLs"""
        """extract host from parent url"""
        o = urlsplit(r.parent_url)
        # split host name, remove first two parts
        # compare the remaining part
        # www.cs.cmu.edu. remove www.cs and keep only cmu.edu
        onetlocs = o.netloc.split('.')
        rhosts = r.host.split('.')
        onetlocdom = ".".join(onetlocs[len(onetlocs)-2:len(onetlocs)])
        rhostdom = ".".join(rhosts[len(rhosts)-2:len(rhosts)])
        #print onetlocdom,rhostdom
        if onetlocdom == rhostdom:
            return True
        else:
            return False

class DomainBlockRule(object):
    # params is a list of domains to block
    def __init__(self,params):
  	self.domains = params

    def check(self,r):
	"""check all URLs including seeds"""
	scheme, host, path, query, fragment = urlsplit(r.url)
   	for domain in self.domains:
	    if host.endswith(domain):
	 	return False

	"""return True after looping over all domains"""
	return True

class AllowPatternRule(object):
    def __init__(self, params):
        self._pattern = params[0]
        if self._pattern != '' and self._pattern != '*':
            self._reg_pattern = re.compile(self._pattern)
        else:
            self._reg_pattern = None
        
    def check(self, r):
        if self._pattern == '': # empty pattern matches nothing
            return False
        elif self._pattern == '*':    # pattern "*" matches everything
            return True
        else:
            return self._reg_pattern.match(r.url) != None
        
class DisallowPatternRule(object):
    def __init__(self, params):
        self._pattern = params[0]
        if self._pattern != '' and self._pattern != '*':
            self._reg_pattern = re.compile(self._pattern)
        else:
            self._reg_pattern = None
        
    def check(self, r):
        if self._pattern == '': # empty pattern matches nothing
            return True
        elif self._pattern == '*':    # pattern "*" matches everything
            return False
        else:
            return self._reg_pattern.match(r.url) == None

class BlackListRule(object):
    """
    Black list filtering with regular expression matching
    """
    def __init__(self, params):
        self.black_list = []
        f = open(params[0], 'r')
        for line in f:
            host_pattern = line[:-1].strip()
            if host_pattern != '':
                try:
                    host_reg = re.compile(host_pattern)
                except:
                    pass
                else:
                    self.black_list.append(host_reg)
        f.close()
                
    def check(self, r):
        for reg in self.black_list:
            if reg.match(r.host) != None:
                return False
        return True

class ExactBlackListRule(object):
    """
    Black list filtering with exact matching
    """
    def __init__(self, params):
        self.black_list = []
        f = open(params[0], 'r')
        for line in f:
            host = line[:-1].strip()

            if host != '':
                self.black_list.append(host)
        f.close()

    def check(self, r):
        if r.host in self.black_list:
            return False
        else:
            return True
        
class SeedHostRule(object):
    def __init__(self, params):
        if params[0].lower() == 'true':
            self._check_sub_string = True
        else:
            self._check_sub_string = False
        
        self._seed_hosts = None
    
    def check(self, r):
        if r.is_seed:
            return True
        
        if self._seed_hosts == None:
            return False
        
        if r.host not in self._seed_hosts:
            return False
        elif self._check_sub_string:
            substrings = self._seed_hosts[r.host]
            
            substring_match = False
            for s in substrings:
                if r.path.startswith(s):
                    substring_match = True
                    break
            
            return substring_match
        else:
            return True

class UrlDepthRule(object):
    def __init__(self, params):        
        self._min_depth = int(params[0])
        self._max_depth = int(params[1])
    
    def check(self, r):
        return r.depth >= self._min_depth and r.depth <= self._max_depth
        
class UrlLengthRule(object):
    def __init__(self, params):
        self._max_length = int(params[0])
    
    def check(self, r):
        return len(r.url) <= self._max_length

class AllowContentTypeRule(object):
    def __init__(self, params):
        self._content_types = params
    
    def check(self, r):
        if len(self._content_types) == 1 and self._content_types[0] == '*':
            return True
        else:
            ok = False
            for ct in self._content_types:
                if ct.find('__') == -1:
                    # no filename extention constain
                    if r.content_type == ct:
                        ok = True
                        break
                else:
                    # also consider filename extention
                    parts = ct.split('__')                    
                    if r.content_type == parts[0] and r.url.lower().endswith(parts[1].lower()):
                        ok = True
                        break
            return ok

class DisallowContentTypeRule(object):
    def __init__(self, params):
        self._allow_rule = AllowContentTypeRule(params)
    
    def check(self, r):
        return (not self._allow_rule.check(r))        

class ContentLengthRule(object):
    def __init__(self, params):
        self._max_length = int(params[0])
    
    def check(self, r):
        return r.content_length <= self._max_length

class HopRule(object):
    def __init__(self, params):
        self._max_hop = int(params[0])
    
    def check(self, r):
        return r.hop <= self._max_hop
