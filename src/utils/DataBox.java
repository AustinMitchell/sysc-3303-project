package utils;

/** Thread-safe object for storing an object. */
public class DataBox<T> extends DataQueueBox<T> {

    /** Puts an item into the queue. */
    @Override
    public synchronized boolean put(T data) {
            if (!_dataQueue.isEmpty()) {
                return false;
            }
            _dataQueue.add(data);
            this.notifyAll();
            return true;
    }
    
    /** Waits for the queue to be empty, then puts an item into it. */
    @Override
    public synchronized boolean putWhenEmpty(T data) {
        while(!_dataQueue.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                return false;
            }
        }
        this.notifyAll();
        return put(data);
    }
}
