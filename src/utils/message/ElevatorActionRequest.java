package utils.message;

/** Object for requesting a specific elevator car's next move from the scheduler. */
public class ElevatorActionRequest {
    public static final MessageType MESSAGE_TYPE = MessageType.ELEVATOR_ACTION_REQUEST;
    
    private byte _carID;
    
    /** Returns the car ID property */
    public int carID() { return _carID; }
    
    /** Creates a new request object from the given car ID */
    public ElevatorActionRequest(int carID) {
        _carID = (byte)carID;
    }
    
    /** Creates a request object from the given byte array. First byte will be the message type, second byte is the car ID */
    public ElevatorActionRequest(byte[] inputData) {
        MESSAGE_TYPE.verifyMessage(inputData);
        _carID = inputData[1];
    }
    
    /** Converts this object into a byte array. First byte is the message ID, second byte is the car ID */
    public byte[] toBytes() {
        return new byte[] { (byte)MESSAGE_TYPE.ordinal(), _carID };
    }
}
