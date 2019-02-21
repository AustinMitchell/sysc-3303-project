package utils.message;

import java.nio.ByteBuffer;

public class ElevatorButtonPushEvent {
    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private int     _count;
    private byte    _carID;
    private byte    _floorNumber;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */

    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */

    public static final MessageType MESSAGE_TYPE = MessageType.ELEVATOR_BUTTON_PUSH_EVENT;

    /* ============================= */
    /* ========== SETTERS ========== */

    /* ============================= */
    /* ========== GETTERS ========== */

    public int messageCount() { return _count; }
    
    /**
     * Retrieves the carID of the elevator that sent the message
     *
     * @return carID
     */
    public int carID() { return this._carID; }

    /**
     * Retrieves the requested floor number
     *
     * @return
     */
    public int floorNumber() { return this._floorNumber; }

    /* ================================== */
    /* ========== CONSTRUCTORS ========== */

    /**
     * Creates a new event object from the given car ID and the requested floor number
     *
     * @param carID
     * @param floorNumber
     */
    public ElevatorButtonPushEvent(int carID, int floorNumber) {
        this._count = Counter.next();
        this._carID = (byte)carID;
        this._floorNumber = (byte)floorNumber;
    }

    /**
     * Creates an event object from the given byte array:
     *      First byte:     message type
     *      Second byte:    car ID
     *      Third byte:     floor number
     *
     * @param inputData
     */
    public ElevatorButtonPushEvent(byte[] inputData) {
        MESSAGE_TYPE.verifyMessage(inputData);
        ByteBuffer buffer = ByteBuffer.wrap(inputData).position(1);

        this._count         = buffer.getInt();
        this._carID         = buffer.get();
        this._floorNumber   = buffer.get();
    }

    /* ============================= */
    /* ========== METHODS ========== */

    /**
     * Converts the data of the object into a byte array:
     *      First byte:     message type
     *      Second byte:    car ID
     *      Third byte:     floor number
     *
     * @return byte array representation of object
     */
    public byte[] toBytes() {
        return ByteBuffer.allocate(7)
                .put((byte)MESSAGE_TYPE.ordinal())
                .putInt(_count)
                .put(_carID)
                .put(_floorNumber)
                .array();
    }
}
