package utils.message;

/** Object for responding to an ElevatorContinueRequest. */
public class ElevatorContinueResponse {
    public static final MessageType MESSAGE_TYPE = MessageType.ELEVATOR_CONTINUE_RESPONSE;
    
    private byte _carID;
    private boolean _response;
    
    /** Returns the car ID property */
    public int      carID()     { return _carID; }
    
    /** Returns the continue response */
    public boolean  response()  { return _response; }
    
    /** Creates a new request object from the given car ID */
    public ElevatorContinueResponse(int carID, boolean response) {
        _carID      = (byte)carID;
        _response   = response;
    }
    
    /** Creates a request object from the given byte array. First byte will be the message type, second byte is the car ID, third
     * byte is the response */
    public ElevatorContinueResponse(byte[] inputData) {
        MESSAGE_TYPE.verifyMessage(inputData);
        _carID      = inputData[1];
        _response   = (inputData[2] == 1);
    }
    
    /** Converts this object into a byte array. First byte is the message ID, second byte is the car ID, third byte is the response */
    public byte[] toBytes() {
        return new byte[] { (byte)MESSAGE_TYPE.ordinal(), _carID, (byte)(_response ? 1 : 0) };
    }
}
