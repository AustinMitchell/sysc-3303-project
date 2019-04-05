package utils;

import java.util.ArrayList;
import java.util.List;

public class WorkerThread<R, I> {
    public interface Job<R, I> {
        R execute(List<I> inputData);
    }

    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private Object    _threadLock;
    private Job<R, I> _job;
    private List<I>   _inputData;
    private boolean   _jobIsFinished;
    private R         _result;

    /* ============================= */
    /* ========== GETTERS ========== */

    /** Return the status of the job */
    public boolean jobIsFinished() { return _jobIsFinished; }

    /** Returns null if the job is currently in progress, else returns the result of the job */
    public R result() { return _result; }

    /* ============================= */
    /* ========== SETTERS ========== */

    public void setInputData(List<I> inputData) { _inputData = inputData; }

    public void setJob(Job<R, I> job) { _job = job; }

    /* ================================== */
    /* ========== CONSTRUCTORS ========== */

    public WorkerThread(Job<R, I> job) { this(new ArrayList<I>(), job); }

    public WorkerThread(List<I> inputData, Job<R, I> job) {
        _inputData     = inputData;
        _job           = job;
        _result        = null;
        _jobIsFinished = true;
        _threadLock    = new Object();
    }

    /* ============================= */
    /* ========== METHODS ========== */

    public void run() {
        _result        = null;
        _jobIsFinished = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                _result = _job.execute(_inputData);
                synchronized (_threadLock) {
                    _threadLock.notifyAll();
                }
                _jobIsFinished = true;
            }
        }).start();
    }

    public void waitForJob() {
        while (!_jobIsFinished) {
            synchronized (_threadLock) {
                try {
                    _threadLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
