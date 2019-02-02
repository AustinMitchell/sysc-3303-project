package utils.message;

/** Object for requesting whether an elevator should continue or stop at the floor. */
public class ElevatorContinueRequest {
    public static final MessageType MESSAGE_TYPE = MessageType.ELEVATOR_CONTINUE_REQUEST;
    
    private byte _carID;
    
    /** Returns the car ID property */
    public int carID() { return _carID; }
    
    /** Creates a new request object from the given car ID */
    public ElevatorContinueRequest(int carID) {
        _carID = (byte)carID;
    }
    
    /** Creates a request object from the given byte array. First byte will be the message type, second byte is the car ID */
    public ElevatorContinueRequest(byte[] inputData) {
        MESSAGE_TYPE.verifyMessage(inputData);
        _carID = inputData[1];
    }
    
    /** Converts this object into a byte array. First byte is the message ID, second byte is the car ID */
    public byte[] toBytes() {
        return new byte[] { (byte)MESSAGE_TYPE.ordinal(), _carID };
    }
}
