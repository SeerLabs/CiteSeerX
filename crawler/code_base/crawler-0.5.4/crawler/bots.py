#!/usr/bin/env python

import os
import httplib
import socket
import urllib2
import shelve
from threading import Lock
import datetime # (jwu 06/27/2011)
import time     # (jwu 06/27/2011)

class RobotsTxt(object):
    """
    Represent rules of a robots.txt file
    """
    
    def __init__(self):
        self.agents = {} # disallowed content
        self.agentcdt = {} # crawl delay time (jwu 06/27/2011)
    	self.last_crawl_time = 0 # jwu 06/27/2011
    
    def show(self):
        """
        print rules for each agent
        """
        for k in self.agents.keys():
            print "%s: %s" % (k, self.agents[k])

    def load_from_string(self, data):
        lines = data.split('\n')
                
        current_agent = None

        for line in lines:
            comment_pos = line.find('#')
            if comment_pos != -1:
                line_no_comment = line[:comment_pos]
            else:
                line_no_comment = line
                
            lower_line = line_no_comment.lower()
            if lower_line.startswith("user-agent"):
                current_agent  = lower_line[11:].strip()
                disallows = []
                self.agents[current_agent] = disallows                
              	self.agentcdt[current_agent] = 0 # (jwu 06/27/2011)
            elif lower_line.startswith("disallow"):
                if current_agent is not None:
                    value = line_no_comment[9:].strip()
                    if value != "":
                        self.agents[current_agent].append(value)
	# -begin (jwu 06/27/2011)
            elif lower_line.startswith("crawl-delay"):
            	if current_agent is not None:
		    valuet = line_no_comment[12:].strip() # crawl delay time, string
                    if valuet != "":
                    	valuet = float(valuet) # convert to float
			self.agentcdt[current_agent] = valuet
 	# -end (jwu 06/27/2011)
            else:
                pass

    def offline_load(self, file_path):
        """
        load from a given file
        """
        
        txt_file = open(file_path, 'r')
        data = txt_file.read()
        txt_file.close()
        
        self.load_from_string(data)        

    def check_path(self, user_agent, path):
        """
        check against an agent and a path
        """
        
        agent_lower = user_agent.lower()
        result = True
        
        if user_agent in self.agents:
            for dis in self.agents[agent_lower]:
                if path.startswith(dis):
                    result = False
                    break                
        elif '*' in self.agents:
            for dis in self.agents['*']:
                if path.startswith(dis):
                    result = False
                    break
        else:
            result = True

        return result
# -begin (jwu 06/27/2011)	
    def check_cdt(self,user_agent,last_crawl_time):
	"""
	check against crawl delay time
 	"""
    
	agent_lower = user_agent.lower()
	result = True

	# if user agent is specified in robots.txt
	if user_agent in self.agentcdt:
	    current_time = datetime.datetime.now()
            t_delta = current_time - last_crawl_time
	    if t_delta >= self.agentcdt[agent_lower]:
		result = True
	    else: 
		result = False
	# if all rule applied to all agents
    	elif '*' in self.agentcdt:
	    current_time = datetime.datetime.now()
            t_delta = current_time - last_crawl_time
	    if t_delta >= self.agentcdt[agent_lower]:
		result = True
	    else: 
		result = False
	else: 
	    result = True

	return result
# -end (jwu 06/27/2011)
# -begin (jwu 06/28/2011)
    def update_time(self,new_time):
	"""
	update the last crawl time
	"""
	self.last_crawl_time = new_time
# -end (jwu 06/28/2011)

	    
class RobotsChecker(object):
    """
    Check URLs against robots.txt
    """
    def __init__(self, user_agent, bots_dir, master):
        self.user_agent = user_agent
        self._master = master
        self.db_lock = Lock()        
        
        if not os.path.exists(bots_dir):
            os.makedirs(bots_dir) 

        db_file = os.path.join(bots_dir, 'bots.db')
        self.bots = shelve.open(db_file)
        self.receiver = None
        
    def put(self, r):
        """
        set receiver before calling put
        """
            
        ok = self._check(r.host, r.path)
        if ok:
            self.receiver.put(r)
        else:
            r.filtered_by = 'RobotsChecker'
            self._master.on_resource_filtered(r, True)

    def _check(self, host, path):
        bot = None
        found = False
        host_key = str(host)        
        
        self.db_lock.acquire()
        try:
            found = (host_key in self.bots)
        finally:
            self.db_lock.release()         
            
        if not found:
            bot = self.load_bot_for_host(host)
            
            self.db_lock.acquire()
            try:
                self.bots[host_key] = bot                                      
            finally:
                self.db_lock.release()
        else:            
            self.db_lock.acquire()
            try:            
                bot = self.bots[host_key]
            finally:
                self.db_lock.release()        
            
        result = bot.check_path(self.user_agent, path)
        return result
         
    def load_bot_for_host(self, host):
        bot = RobotsTxt() # a empty bot object is created even if the txt_file does not exist
                
        bots_url = "http://%s/robots.txt" % host
        data = ''
        r_bots_url = resource.Resource(None,host, bots_url, False, 1, 0, '')
            
        # try to fetch
        try:
            f = urllib2.urlopen(bots_url)
            data = f.read()
            f.close()            
	    # parent url is set to be the host
	    # hop is set to be 1 because it comes from a host
	    # batch number is set to be 0
	    # anchor text is an empty string
	    self._master.on_resource_fetched(r_bots_url) # jwu,2012-10-08, write loading robots.txt to log
        except httplib.HTTPException:
            pass
        except urllib2.URLError:
            pass
        except socket.timeout:
            pass
        except socket.error:
            pass                
        except IOError:
            pass
        except ValueError:
            pass            
        
        bot.load_from_string(data)
        
        return bot
