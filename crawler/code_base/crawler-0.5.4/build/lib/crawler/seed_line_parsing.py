#!/usr/bin/env python2.4

import resource
from exception import BadResourceError

class DefaultSeedLineParser(object):
    """default parser for seed line"""
    def __init__(self, params):
        """
        we need this empty __init__ so that the instance can be created in a
        dynamic and uniform way 
        """        
        pass
            
    def parse(self, line, batch=0):
        """Parse a line to get a resource.
        
        Make sure there is no ending '\n' in the input line
        
        Args:
            line: input string without ending '\n'
        
        Returns:
            a resource to crawl (return None if fail)
        """
        
        if line.endswith('\n'):
            return None
            
        parts = line.split('\t')
        if len(parts) == 1:
            url = parts[0]
            code = None
        elif len(parts) == 2:
            # 12345   http://www.cse.psu.edu
            url = parts[1];
            code = parts[0];
        else:
            return None
               
        try: 
            r = resource.Resource(code, None, url, True, 0, batch)
        except BadResourceError:
            r = None
             
        return r

class UrlParentPairSeedLineParser(object):
    """seed line parser for <url, parent-url> paired line"""
    def __init__(self, params):
        """
        we need this empty __init__ so that the instance can be created in a
        dynamic and uniform way 
        """            
        pass    
    
    def parse(self, line, batch=0):
        """Parse a line to get a resource.
        
        Make sure there is no ending '\n' in the input line
        
        Args:
            line: input string without ending '\n'
        
        Returns:
            a resource to crawl (return None if fail)
        """
        
        if line.endswith('\n'):
            return None
            
        parts = line.split('\t')
        if len(parts) == 1:
            url = parts[0]
            parent_url = None
        elif len(parts) == 2:
            # http://www.cse.psu.edu/~shzheng/sigkdd-2007.pdf	http://www.cse.psu.edu/~shzheng/            
            url = parts[0];
            if parts[1] == '':
                parent_url = None
            else:
                parent_url = parts[1]
        else:
            return None
        
        try:        
            r = resource.Resource(None, parent_url, url, True, 0, batch)
        except BadResourceError:
            r = None
        
        return r
        