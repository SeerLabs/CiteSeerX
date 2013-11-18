/*
 * Copyright 2007 Penn State University
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.psu.citeseerx.utility.persistentqueue;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Extension of the BlockingQueue object with provisions for storing job
 * state in a DataSource backend.
 *
 * @author Isaac Councill
 * @version $Rev: 191 $ $Date: 2012-02-08 14:32:39 -0500 (Wed, 08 Feb 2012) $
 */
public class PersistentQueue<E extends PersistentJob>
implements BlockingQueue<E> {

    protected BlockingQueue<E> queue;
    
    public BlockingQueue<E> getQueue() {
        return queue;
    } //- getQueue
    
    
    protected DataSource dataSource;

    public DataSource getDataSource() {
        return dataSource;
    } //- getDataSource
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    } //- setDataSource
    
    
    protected PersistentQueuePopulator<E> populator;
    
    public PersistentQueuePopulator<E> getPopulator() {
        return populator;
    } //- getPopulator
    
    public void setPopulator(PersistentQueuePopulator<E> populator) {
        this.populator = populator;
    } //- setPopulator
    
    
    protected synchronized void repopulateJobs() throws SQLException {
        populator.populate(queue, dataSource);
    } //- repopulateJobs
    
    
    private long populateDelay = 5000;
    
    class Repopulator extends Thread {
        public Repopulator() {
            this.setDaemon(true);
        } //- Repopulator
        
        public void run() {
            while (true) {
                synchronized(this) {
                    if (queue.isEmpty()) {
                        try {
                            repopulateJobs();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    sleep(populateDelay);
                } catch (InterruptedException e) { /* ignore */ }
            }
        } //- run
    } //- class Repopulator
    

    /**
     * Starts job population from the persistent backend.  This
     * will load old jobs that got submitted when the queue was stopped
     * or that were not processed during the last execution cycle.
     */
    public void start() {
        Repopulator repopulator = new Repopulator();
        repopulator.start();
    } //- start
    
    
    protected boolean submitJob(E job) throws IllegalStateException {
        try {
            job.submit(dataSource);
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage());
        }
        return true;
        
    }  //- submitJob
    
    
    protected boolean checkoutJob(E job) throws IllegalStateException {
        try {
            job.checkout(dataSource);
            if (queue.isEmpty()) {
                repopulateJobs();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage());
        }
        return true;
        
    }  //- checkoutJob

    
    //=============================================================
    // Subclasses
    //=============================================================

    
    class PersistentArrayQueue extends PersistentQueue<E> {
        public PersistentArrayQueue(int capacity) {
            queue = new ArrayBlockingQueue<E>(capacity);
        }
        public PersistentArrayQueue(int capacity, boolean fair) {
            queue = new ArrayBlockingQueue<E>(capacity, fair);
        }
        public PersistentArrayQueue(int capacity, boolean fair,
                Collection<? extends E> c) {
            queue = new ArrayBlockingQueue<E>(capacity, fair, c);
        }
        
    }  //- class PersistentArrayQueue
    
    
    class PersistentLinkedQueue extends PersistentQueue<E> {
        public PersistentLinkedQueue() {
            queue = new LinkedBlockingQueue<E>();
        } //- PersistentLinkedQueue
        public PersistentLinkedQueue(Collection<? extends E> c) {
            queue = new LinkedBlockingQueue<E>(c);
        } //- PersistentLinkedQueue
        public PersistentLinkedQueue(int capacity) {
            queue = new LinkedBlockingQueue<E>(capacity);
        } //- PersistentLinkedQueue
        
    }  //- class PersistentLinkedQueue
    
    
    class PersistentPriorityQueue extends PersistentQueue<E> {
        public PersistentPriorityQueue() {
            queue = new PriorityBlockingQueue<E>();
        } //- PersistentPriorityQueue
        public PersistentPriorityQueue(Collection <? extends E> c) {
            queue = new PriorityBlockingQueue<E>(c);
        } //- PersistentPriorityQueue
        public PersistentPriorityQueue(int initialCapacity) {
            queue = new PriorityBlockingQueue<E>(initialCapacity);
        } //- PersistentPriorityQueue
        public PersistentPriorityQueue(int initialCapacity,
                Comparator<? super E> comparator) {
            queue = new PriorityBlockingQueue<E>(initialCapacity, comparator);
        } //- PersistentPriorityQueue
        
    }  //- class PersistentPriorityQueue
    
    
    class PersistentSynchronousQueue extends PersistentQueue<E> {
        public PersistentSynchronousQueue() {
            queue = new SynchronousQueue<E>();
        } //- PersistentSynchronousQueue
        public PersistentSynchronousQueue(boolean fair) {
            queue = new SynchronousQueue<E>(fair);
        } //- PersistentSynchronousQueue
        
    }  //- class PersistentSynchronousQueue
    

    //=============================================================
    // BlockingQueue interface
    //=============================================================

    
    /* (non-Javadoc)
     * @see java.util.concurrent.BlockingQueue#add(java.lang.Object)
     */
    public boolean add(E job) throws IllegalStateException {
        System.out.println("Adding job");
        return submitJob(job);
    } //- add
    
    
    /* (non-Javadoc)
     * @see java.util.concurrent.BlockingQueue#drainTo(java.util.Collection)
     */
    public int drainTo(Collection<? super E> c) throws IllegalStateException {
        int nElts = queue.drainTo(c);
        try {
            for (Iterator<? super E> it = c.iterator(); it.hasNext(); ) {
                PersistentJob job = (PersistentJob)it.next();
                job.checkout(dataSource);
            }
            if (queue.isEmpty()) {
                repopulateJobs();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage());
        }
        return nElts;
        
    }  //- drainTo
    
    
    /* (non-Javadoc)
     * @see java.util.concurrent.BlockingQueue#drainTo(java.util.Collection, int)
     */
    public int drainTo(Collection<? super E> c, int maxElements) {
        int nElts = queue.drainTo(c, maxElements);
        try {
            for (Iterator<? super E> it = c.iterator(); it.hasNext(); ) {
                PersistentJob job = (PersistentJob)it.next();
                job.checkout(dataSource);
            }
            if (queue.isEmpty()) {
                repopulateJobs();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage());
        }        
        return nElts;
        
    }  //- drainTo
    
    
    /* (non-Javadoc)
     * @see java.util.concurrent.BlockingQueue#offer(java.lang.Object)
     */
    public boolean offer(E job) {
        try {
            submitJob(job);
        } catch (IllegalStateException e) {
            return false;
        }
        return true;
        
    }  //- offer
    
    
    /* (non-Javadoc)
     * @see java.util.concurrent.BlockingQueue#offer(java.lang.Object, long, java.util.concurrent.TimeUnit)
     */
    public boolean offer(E job, long timeout, TimeUnit unit)
    throws InterruptedException {
        return offer(job);
    } //- offer
    
    
    /* (non-Javadoc)
     * @see java.util.concurrent.BlockingQueue#poll(long, java.util.concurrent.TimeUnit)
     */
    public E poll(long timeout, TimeUnit unit)
    throws InterruptedException, IllegalStateException {
        E job = queue.poll(timeout, unit);
        if (job != null) {
            checkoutJob(job);
        }
        return job;
        
    }  //- poll
    
    
    /* (non-Javadoc)
     * @see java.util.concurrent.BlockingQueue#put(java.lang.Object)
     */
    public void put(E job) throws InterruptedException, IllegalStateException {
        submitJob(job);
    } //- put
    
    
    /* (non-Javadoc)
     * @see java.util.concurrent.BlockingQueue#remainingCapacity()
     */
    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    } //- remainingCapacity
    
    
    /* (non-Javadoc)
     * @see java.util.concurrent.BlockingQueue#take()
     */
    public E take() throws InterruptedException, IllegalStateException {
        E job = queue.take();
        checkoutJob(job);
        return job;
        
    }  //- take
    
    
    //=============================================================
    // Queue interface
    //=============================================================

    
    /* (non-Javadoc)
     * @see java.util.Queue#element()
     */
    public E element() throws NoSuchElementException {
        return queue.element();
    } //- element
    
    /* (non-Javadoc)
     * @see java.util.Queue#peek()
     */
    public E peek() {
        return queue.peek();
    } //- peek
    
    /* (non-Javadoc)
     * @see java.util.Queue#poll()
     */
    public E poll() throws IllegalStateException {
        E job = queue.poll();
        if (job != null) {
            checkoutJob(job);
        }
        return job;
    } //- poll
    
    /* (non-Javadoc)
     * @see java.util.Queue#remove()
     */
    public E remove() throws NoSuchElementException, IllegalStateException {
        E job = queue.remove();
        checkoutJob(job);
        return job;
    } //- remove

    
    //=============================================================
    // Collection interface
    //=============================================================

    
    /* (non-Javadoc)
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    public boolean addAll(Collection<? extends E> c)
    throws IllegalStateException {
        for (Iterator<? extends E> it = c.iterator(); it.hasNext(); ) {
            add(it.next());
        }
        return false;
        
    }  //- addAll
    
    
    public void clear() {
        queue.clear();
    }
    
    /* (non-Javadoc)
     * @see java.util.Collection#contains(java.lang.Object)
     */
    public boolean contains(Object obj) {
        return queue.contains(obj);
    } //- contains
    
    /* (non-Javadoc)
     * @see java.util.Collection#containsAll(java.util.Collection)
     */
    public boolean containsAll(Collection<?> c) {
        return queue.containsAll(c);
    } //- containsAll
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        PersistentQueue oQueue = null;
        if (obj instanceof PersistentQueue) {
            oQueue = (PersistentQueue)obj;
        } else {
            return false;
        }
        if ((obj.getClass() == this.getClass()) &&
                (oQueue.getQueue().equals(queue))) {
            return true;
        } else {
            return false;
        }
        
    }  //- equals
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return queue.hashCode() + 23;
    } //- hashCode
    
    /* (non-Javadoc)
     * @see java.util.Collection#isEmpty()
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    } //- isEmpty
    
    /* (non-Javadoc)
     * @see java.util.Collection#iterator()
     */
    public Iterator<E> iterator() {
        return queue.iterator();
    } //- iterator
    
    /* (non-Javadoc)
     * @see java.util.Collection#remove(java.lang.Object)
     */
    public boolean remove(Object obj) {
        return queue.remove(obj);
    } //- remove
    
    /* (non-Javadoc)
     * @see java.util.Collection#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection<?> c) throws IllegalStateException {
        boolean changed = queue.removeAll(c);
        if (queue.isEmpty()) {
            try {
                repopulateJobs();
            } catch (SQLException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
        return changed;
        
    }  //- removeAll
    
    
    /* (non-Javadoc)
     * @see java.util.Collection#retainAll(java.util.Collection)
     */
    public boolean retainAll(Collection<?> c) {
        boolean changed = queue.retainAll(c);
        if (queue.isEmpty()) {
            try {
                repopulateJobs();
            } catch (SQLException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
        return changed;
        
    }  //- retainAll
    
    
    /* (non-Javadoc)
     * @see java.util.Collection#size()
     */
    public int size() {
        return queue.size();
    } //- size
    
    /* (non-Javadoc)
     * @see java.util.Collection#toArray()
     */
    public Object[] toArray() {
        return queue.toArray();
    }
    
    public <T> T[] toArray(T[] a) {
        return queue.toArray(a);
    } //- toArray
    
}  //- class PersistentQueue



