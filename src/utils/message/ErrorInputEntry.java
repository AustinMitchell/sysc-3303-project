package utils.message;

import java.nio.ByteBuffer;

public class ErrorInputEntry extends InputEntry {

    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private SystemFault     _faultType;
    private int             _elevatorNumber;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */


    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */

    public static final MessageType MESSAGE_TYPE = MessageType.ERROR_INPUT_ENTRY;

    /* ============================= */
    /* ========== SETTERS ========== */



    /* ============================= */
    /* ========== GETTERS ========== */

    /** Return the elevator number of the entry */
    public int          elevatorNumber()    { return this._elevatorNumber; }

    /** Returns the fault type of the entry */
    public SystemFault  faultType()         { return this._faultType; }

    /* ================================== */
    /* ========== CONSTRUCTORS ========== */

    public ErrorInputEntry(String inputLine) {
        _count = Counter.next();

        String[] splitLine = inputLine.split(" ");

        if (splitLine.length != 4) {
            throw new RuntimeException("Invalid input: Did not have exactly 4 columns with single spaces between.");
        }

        this._timestamp = new TimeStamp(splitLine[1]);

        this._faultType = SystemFault.fromOrdinal(Integer.parseInt(splitLine[2]));

        this._elevatorNumber = Integer.parseInt(splitLine[3]);
    }

    public ErrorInputEntry(byte[] inputData) {
        MESSAGE_TYPE.verifyMessage(inputData);
        ByteBuffer buffer       = (ByteBuffer) ByteBuffer.wrap(inputData).position(1);

        this._count             = buffer.getInt();

        byte[] timeStamp        = new byte[5];
        buffer.get(timeStamp);
        this._timestamp         = new TimeStamp(timeStamp);

        this._faultType         = SystemFault.fromOrdinal(buffer.get());
        this._elevatorNumber    = buffer.get();
    }

    /* ============================= */
    /* ========== METHODS ========== */

    /**
     * Returns the entry as an array of bytes. Order:
     *      1 byte message type
     *      4 bytes count
     *      5 bytes timestamp
     *      1 byte fault type
     *      1 byte elevator number
     */
    @Override
    public byte[] toBytes() {
        return ByteBuffer.allocate(12)
                .put((byte)MESSAGE_TYPE.ordinal())
                .putInt(this._count)
                .put(this._timestamp.toBytes())
                .put((byte)this._faultType.ordinal())
                .put((byte)this._elevatorNumber)
                .array();
    }

    /**
     * Returns string representation of the entry
     */
    @Override
    public String toString() {
        return String.format("Timestamp: %s, Fault Type: %s, Elevator Number: %d", this._timestamp.toString(),
                                                                                   this._faultType.toString(),
                                                                                   this._elevatorNumber);
    }

}
