package utils.message;

public class ElevatorButtonPushEvent {
    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private byte _carID;
    private byte _floorNumber;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */

    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */

    public static final MessageType MESSAGE_TYPE = MessageType.ELEVATOR_BUTTON_PUSH_EVENT;

    /* ============================= */
    /* ========== SETTERS ========== */

    /* ============================= */
    /* ========== GETTERS ========== */

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
        this._carID         = inputData[1];
        this._floorNumber   = inputData[2];
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
        return new byte[] { (byte)MESSAGE_TYPE.ordinal(), this._carID, this._floorNumber };
    }
}
