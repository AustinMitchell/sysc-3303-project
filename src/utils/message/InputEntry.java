package utils.message;

public abstract class InputEntry implements Comparable<InputEntry> {

    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */


    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */

    protected int           _count;
    protected TimeStamp     _timestamp;

    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */



    /* ============================= */
    /* ========== SETTERS ========== */



    /* ============================= */
    /* ========== GETTERS ========== */

    /** Returns the count from entry */
    public int          messageCount()  { return _count; }

    /** Returns timestamp from entry */
    public TimeStamp    timestamp()     { return _timestamp; }

    /* ================================== */
    /* ========== CONSTRUCTORS ========== */



    /* ============================= */
    /* ========== METHODS ========== */

    /** Returns the entry as a byte array */
    public abstract byte[] toBytes();

    /** Returns the entry as a string */
    public abstract String toString();

    /**
     * Calculates the difference between self and another entry and returns the
     * result in milliseconds
     *
     * @param entry
     * @return difference in milliseconds
     */
    public int differenceInMilliseconds(InputEntry entry) {
        return this._timestamp.toMilliseconds() - entry.timestamp().toMilliseconds();
    }


    /** Compares the timestamps of this entry and another entry. Earlier timestamps are considered "less than" later timestamps. */
    @Override
    public int compareTo(InputEntry other) {
        return this._timestamp.compareTo(other._timestamp);
    }
}
