#!/usr/bin/env python2.4

import os
import url_normalization
import logging

class ResourceWriter(object):
    def __init__(self, params):
        self.output_dir = params[0]
        
    def save(self, r, data):
        file_path = self.resource_to_path(r)
        dir_path = os.path.dirname(file_path)
        if os.path.exists(dir_path):
            if os.path.isdir(dir_path):
                pass # ok
            else:
                location = 'output.ResourceWriter.save'
                info = 'dir path is mistakenly used by a file'
                logging.warning("[bug] [%s] %s\n" % (location, info))                
        else:
            os.makedirs(dir_path)
        
        f = open(file_path, 'w')
        
        #print file_path
        f.write(data)
        f.close()
    
    def resource_to_path(self, r):
        """need to to customized
        """
        return ''

class MirrorWriter(ResourceWriter):
    def resource_to_path(self, r):
        p = get_mirror_path(r)
        return os.path.join(self.output_dir, p)

def get_mirror_path(r):
    # get port
    if r.port == None:
        port_str = ''
    else:
        port_str = ':' + r.port
    
    # get host path
    # www.cse.psu.edu:1234     edu\psu\cse\www\www.cse.psu.edu:1234
    # www.cse.psu.edu          edu\psu\cse\www\www.cse.psu.edu
    
    parts = r.host.split('.')
    host_str = ''
    for i in range(len(parts)-1, -1, -1):
        host_str = os.path.join(host_str, parts[i])
        
    host_str = os.path.join(host_str, r.host + port_str)    
        
    # get local path
    parts = os.path.split(r.path[1:])
    dir_path = parts[0].replace('.', '_DOT_')
    if r.query != '':
        file_name = parts[1] + "?" + r.query
    else:
        file_name = parts[1]
    
    local_path = os.path.join(dir_path, file_name)
        
    p = os.path.join(host_str, local_path)
    if url_normalization.looks_like_dir(p):
        p = os.path.join(p, 'INDEX.HTM')
    return p
