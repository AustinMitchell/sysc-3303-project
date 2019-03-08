package utils.message;

import java.nio.ByteBuffer;

public class ElevatorError {

    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private int             _count;
    private SystemFault     _faultType;
    private int             _elevatorNumber;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */


    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */

    public static final MessageType MESSAGE_TYPE = MessageType.ELEVATOR_ERROR;

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

    public ElevatorError(String inputLine) {
        _count = Counter.next();

        String[] splitLine = inputLine.split(" ");

        if (splitLine.length != 4) {
            throw new RuntimeException("Invalid input: Did not have exactly 4 columns with single spaces between.");
        }

        this._faultType = SystemFault.fromOrdinal(Integer.parseInt(splitLine[2]));

        this._elevatorNumber = Integer.parseInt(splitLine[3]);
    }

    public ElevatorError(byte[] inputData) {
        MESSAGE_TYPE.verifyMessage(inputData);
        ByteBuffer buffer       = (ByteBuffer) ByteBuffer.wrap(inputData).position(1);

        this._count             = buffer.getInt();

        this._elevatorNumber    = buffer.get();
        this._faultType         = SystemFault.fromOrdinal(buffer.get());
    }

    public ElevatorError(SystemFault faultType, int elevatorNumber) {
        this._count             = Counter.next();

        this._faultType         = faultType;
        this._elevatorNumber    = elevatorNumber;
    }

    /* ============================= */
    /* ========== METHODS ========== */

    /**
     * Returns the entry as an array of bytes. Order:
     *      1 byte message type
     *      4 bytes count
     *      1 byte fault type
     *      1 byte elevator number
     */
    public byte[] toBytes() {
        return ByteBuffer.allocate(7)
                .put((byte)MESSAGE_TYPE.ordinal())
                .putInt(this._count)
                .put((byte)this._elevatorNumber)
                .put((byte)this._faultType.ordinal())
                .array();
    }

    /**
     * Returns string representation of the entry
     */
    @Override
    public String toString() {
        return String.format("Fault Type: %s, Elevator Number: %d", this._faultType.toString(), this._elevatorNumber);
    }

}
