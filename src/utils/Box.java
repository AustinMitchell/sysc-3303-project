package utils;

public class Box<T> {
    
    private T _data;
    
    public Box() {
        this(null);
    }
    
    public Box(T data) {
        _data = data;
    }
    
    public synchronized T get() {
        T ret = _data;
        _data = null;
        this.notifyAll();
        return ret;
    }
    
    public synchronized T getWhenFull() {
        while(_data == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                return null;
            }
        }
        T ret = _data;
        _data = null;
        this.notifyAll();
        return ret;
    }
    
    public synchronized boolean put(T data) {
            if (_data != null) {
                return false;
            }
            _data = data;
            this.notifyAll();
            return true;
    }
    
    public synchronized boolean putWhenEmpty(T data) {
        while(_data != null) {
            try {
                wait();
            } catch (InterruptedException e) {
                return false;
            }
        }
        _data = data;
        this.notifyAll();
        return true;
    }
    
}
