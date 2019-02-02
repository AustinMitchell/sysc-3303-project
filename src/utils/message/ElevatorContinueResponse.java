package utils.message;

/** Object for responding to an ElevatorContinueRequest. */
public class ElevatorContinueResponse {
    public static final MessageType MESSAGE_TYPE = MessageType.ELEVATOR_CONTINUE_RESPONSE;
    
    private byte _carID;
    private byte _floor;
    
    /** Returns the car ID property */
    public int      carID()     { return _carID; }
    
    /** Returns the continue response. -1 means continue, otherwise it will be the floor its stopping at */
    public byte     response()  { return _floor; }
    
    /** 
     * Creates a new request object from the given car ID and floor. If floor is -1, it means that the elevator should
     * not continue.
     * @param carID     ID of the car to send the message to
     * @param floor     Floor to stop at, used to control the elevator lamps*/
    public ElevatorContinueResponse(int carID, byte floor) {
        _carID = (byte)carID;
        _floor = floor;
    }
    
    /**
     * Creates a request object from the given byte array. First byte will be the message type, second byte is the car ID, third
     * byte is the floor to stop at, -1 mean do not stop.
     * */
    public ElevatorContinueResponse(byte[] inputData) {
        MESSAGE_TYPE.verifyMessage(inputData);
        _carID = inputData[1];
        _floor = inputData[2];
    }
    
    /** Converts this object into a byte array. First byte is the message ID, second byte is the car ID, third byte is the response */
    public byte[] toBytes() {
        return new byte[] { (byte)MESSAGE_TYPE.ordinal(), _carID, _floor };
    }
}
