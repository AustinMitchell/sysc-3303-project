package utils.message;

/** Simple counter for tracking message counts, count is static and thus tracked per process. */
public class Counter {
    private static int COUNT = 0;
    
    public static synchronized int next() {
        return COUNT++;
    }
    
    private Counter() {}
}
