#!/usr/bin/env python

import os
import cPickle
import logging
import Queue
import shelve
import time
from threading import Lock, Thread


class MemoryDue(object):
    def __init__(self, params):        
        self._db = {}        
        self._check_lock = Lock()
        self.receiver = None        
            
    def put(self, r):
        """
        set receiver before calling put
        """
        
        is_new = False
        
        self._check_lock.acquire()
        try:
            if r.md5 in self._db:
                # also compare batch
                old_batch = int(self._db[r.md5])
                new_batch = r.batch
            
                if new_batch > old_batch:
                    is_new = True
                else:
                    is_new = False                    
            else:
                is_new = True
            
            if is_new:
                self._db[r.md5] = r.batch                 
        finally:
            self._check_lock.release()

        # for performance reason, the following code is outside above critical section          
        if is_new:
            self.receiver.put(r)

class DiskDue(MemoryDue):           
    def __init__(self, params):
        db_file = params[0]
        
        self._check_lock = Lock()

        dir = os.path.dirname(db_file)
        if not os.path.exists(dir):
            os.makedirs(dir)        
        
        self._db = shelve.open(db_file, 'c')
        self.receiver = None
                 
        
class MercatorDue(object):
    def __init__(self, params):
        # disk file
        self.due_dir = params[0]
        
        if not os.path.exists(self.due_dir):
            os.makedirs(self.due_dir)
        
        self.file_version = 0
               
        # buffer        
        self._buffer = {}
        self._buffer_size_limit = int(params[1])
                   
        self._check_lock = Lock()
        self._merge_lock = Lock()
        
        self.receiver = None
        
        # state
        self.no_merge_yet = True    # protected by _check_lock
        
        # statistics
        self.input_count = 0        # protected by _check_lock
        self.buffer_hit_count = 0   # protected by _check_lock
        self.disk_hit_count = 0     # protected by _merge_lock
        self.passed_count = 0       # protected by _check_lock (no_merge_yet=True) and by _merge_lock (no_merge_yet=False)
        
        self.keep_file = False
    
    def __str__(self):
        #buffer_pct = float(len(self._buffer)) / float(self._buffer_size_limit) * 100.0
        if self._merge_lock.locked():
            status_str = '!'
        else:
            status_str = ''
        
        return 'MercatorDue%s -- Merges:%d Buffer:%d/%d\n\tInput:%d BufferHit:%d DiskHit:%d Passed:%d' % (
            status_str,
            self.file_version,
            len(self._buffer),
            self._buffer_size_limit,
            self.input_count,
            self.buffer_hit_count,
            self.disk_hit_count,
            self.passed_count
            )
        
    def put(self, r):
        logging.debug('> %s.%d' % (r.md5, r.batch))
        
        self._check_lock.acquire()
        try:
            self.input_count += 1
            
            r.sent = False
            
            # first check LRU cache
            
            # then check buffer
            if r.md5 in self._buffer and self._buffer[r.md5].batch >= r.batch:
                # it's a dup
                self.buffer_hit_count += 1
                return
            else:
                # immediately forward when no_merge_yet = True
                # it will not be forwarded again during merging
                if self.no_merge_yet:                    
                    self.forward(r)                
                
                if r.md5 in self._buffer:
                    # same md5, but later one has larger batch # and will replace the previous one
                    # if it was not forwarded already, it should be considered as a buffer hit
                    if not self._buffer[r.md5].sent:
                        self.buffer_hit_count += 1
                
                # add new resource or replace the same one with smaller batch           
                self._buffer[r.md5] = r
                
                # when buffer is full, merge buffer to disk file and forward unique URLs
                if len(self._buffer) >= self._buffer_size_limit:
                    # block when the previous merging is not done yet
                    while self._merge_lock.locked():
                        logging.debug('...')
                        time.sleep(1)
                    
                    self.packing_for_merge()
                                              
        finally:
            self._check_lock.release()
    
    def forward(self, r):
        """
        forward passed resource
        
        should be protected by a Lock since it increment passed_count
        """
        if not r.sent:        
            self.receiver.put(r)
            self.passed_count += 1
            r.sent = True # make sure it will not be forwarded more than once
    
    def notify_idle(self):
        """
        called when the system is idling
        """
        
        self._check_lock.acquire()
        try:
            if len(self._buffer) > 0:
                # skip when previous merging is not done yet
                if self._merge_lock.locked():
                    return
                
                self.packing_for_merge()                                              
        finally:
            self._check_lock.release()        
    
    def packing_for_merge(self):
        """
        make sure this method is protected by self._check_lock
        """                
        
        self.no_merge_yet = False
        
        # sort buffer by md5
        buffer_keys = self._buffer.keys()
        buffer_keys.sort()
                            
        # make a sorted copy of the buffer
        buffer_to_merge = Queue.Queue(0)
        for k in buffer_keys:
            buffer_to_merge.put(self._buffer[k], block=False)                                                                 
        
        # use another thread to do the merging                    
        t = Thread(target=self.merge_check, args=[buffer_to_merge])
        t.start()
        
        # create an new empty buffer
        self._buffer = {}                            
    
    def merge_check(self, buffer_to_merge):                                       
        self._merge_lock.acquire()
        
        logging.debug('*'*40)
        
        try:            
            from_file_path = os.path.join(self.due_dir, 'data.%d' % self.file_version)
            
            self.file_version += 1
            to_file_path = os.path.join(self.due_dir, 'data.%d' % self.file_version)
                        
            if not os.path.exists(from_file_path):
                # create an empty file
                from_file = open(from_file_path, 'w')
                from_file.close()
                        
            from_file = open(from_file_path, 'r')
            to_file = open(to_file_path, 'w')
            
            # buffer used to load from disk file
            disk_input_buffer = Queue.Queue(self._buffer_size_limit)
            # buffer used to dump to disk file
            disk_output_buffer = Queue.Queue(self._buffer_size_limit)
            
            def get_left(from_file, disk_input_buffer):
                # if input buffer is empty, try to fill it
                if disk_input_buffer.empty():
                    try:
                        pairs = cPickle.load(from_file)
                    except EOFError:
                        return None, None
                    except:
                        logging.exception('Fail to load in merge_check:')
                        return None, None
                    else:    
                        for (md5, batch) in pairs:
                            disk_input_buffer.put((md5, batch), block=False)
                            
                if not disk_input_buffer.empty():
                    md5, batch = disk_input_buffer.get(block=False)
                    return md5, batch                    
                else:
                    logging.error('[BUG] [due.py merge_check()] disk_input_buffer should not be empty here')
                    return None, None 
            
            def get_right(buffer_to_merge):
                try:
                    r = buffer_to_merge.get(block=False)
                except Queue.Empty:    
                    r = None
                
                return r
            
            def dump_record(md5, batch, to_file, disk_output_buffer):
                if disk_output_buffer.full():                    
                    clear_output_buffer(to_file, disk_output_buffer)
                
                # add to buffer
                disk_output_buffer.put((md5, batch), block=False)
            
            def clear_output_buffer(to_file, disk_output_buffer):
                out_pairs = []
                while not disk_output_buffer.empty():
                    out_pairs.append(disk_output_buffer.get(block=False))
                    
                cPickle.dump(out_pairs, to_file)                
                
                
            md51, batch1 = get_left(from_file, disk_input_buffer)
            r2 = get_right(buffer_to_merge)
                    
            while md51 is not None and r2 is not None:
                if md51 == r2.md5:
                    if r2.batch > batch1:
                        # it's new
                        dump_record(r2.md5, r2.batch, to_file, disk_output_buffer)
                        
                        self.forward(r2)                        
                    else:
                        # it's dup                                                
                        dump_record(md51, batch1, to_file, disk_output_buffer)
                        
                        self.disk_hit_count += 1
                
                    md51, batch1 = get_left(from_file, disk_input_buffer)
                    r2 = get_right(buffer_to_merge)
                elif md51 < r2.md5:
                    # output left
                    dump_record(md51, batch1, to_file, disk_output_buffer)
                    
                    md51, batch1 = get_left(from_file, disk_input_buffer)
                else:
                    # output and forward r2
                    dump_record(r2.md5, r2.batch, to_file, disk_output_buffer)                        
                    
                    self.forward(r2)
                    
                    r2 = get_right(buffer_to_merge)
            
            # output the rest
            while md51 is not None:
                dump_record(md51, batch1, to_file, disk_output_buffer)
                md51, batch1 = get_left(from_file, disk_input_buffer)
                
            while r2 is not None:
                dump_record(r2.md5, r2.batch, to_file, disk_output_buffer)
                
                self.forward(r2)
                
                r2 = get_right(buffer_to_merge)
            
            # clear output buffer
            clear_output_buffer(to_file, disk_output_buffer)                                
        
            from_file.close()            
            to_file.close()
            
            # remove from_file            
            if not self.keep_file:
                os.remove(from_file_path) 
        
            logging.debug('-'*40)
            
        finally:
            self._merge_lock.release()
        
        