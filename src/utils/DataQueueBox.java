package utils;

import java.util.LinkedList;
import java.util.Queue;

/** Thread-safe object for storing a data queue. */
public class DataQueueBox<T> {
    
    protected Queue<T> _dataQueue;
    
    /** Creates a new empty data queue */
    public DataQueueBox() {
        _dataQueue = new LinkedList<>();
    }
    
    public synchronized boolean isEmpty() {
        return _dataQueue.isEmpty();
    }
    
    /** Gets the first item in the queue. If there is nothing in the queue, returns null. */
    public synchronized T get() {
        T ret = _dataQueue.poll();
        this.notifyAll();
        return ret;
    }
    
    /** Waits for something to go into the queue, then gets it. */
    public synchronized T getWhenNotEmpty() {
        while(_dataQueue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                return null;
            }
        }
        return get();
    }
    
    /** Puts an item into the queue. */
    public synchronized boolean put(T data) {
            _dataQueue.add(data);
            this.notifyAll();
            return true;
    }
    
    /** Waits for the queue to be empty, then puts an item into it. */
    public synchronized boolean putWhenEmpty(T data) {
        while(!_dataQueue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                return false;
            }
        }
        return put(data);
    }
    
}
