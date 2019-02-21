package utils.message;

import java.nio.ByteBuffer;

/** Object for responding to an ElevatorContinueRequest. */
public class ElevatorContinueResponse {
    public static final MessageType MESSAGE_TYPE = MessageType.ELEVATOR_CONTINUE_RESPONSE;
    
    private int     _count;
    private byte    _carID;
    private byte    _floor;
    
    public int      messageCount()  { return _count; }
    
    /** Returns the car ID property */
    public int      carID()         { return _carID; }
    
    /** Returns the continue response. -1 means continue, otherwise it will be the floor its stopping at */
    public byte     response()      { return _floor; }
    
    /** 
     * Creates a new request object from the given car ID and floor. If floor is -1, it means that the elevator should
     * not continue.
     * @param carID     ID of the car to send the message to
     * @param floor     Floor to stop at, used to control the elevator lamps*/
    public ElevatorContinueResponse(int carID, int floor) {
        _count = Counter.next();
        _carID = (byte)carID;
        _floor = (byte)floor;
    }
    
    /**
     * Creates a request object from the given byte array. First byte will be the message type, second byte is the car ID, third
     * byte is the floor to stop at, -1 mean do not stop.
     * */
    public ElevatorContinueResponse(byte[] inputData) {
        MESSAGE_TYPE.verifyMessage(inputData);
        ByteBuffer buffer = ByteBuffer.wrap(inputData).position(1);
        
        _count = buffer.getInt();
        _carID = buffer.get();
        _floor = buffer.get();
    }
    
    /** Converts this object into a byte array. First byte is the message ID, second byte is the car ID, third byte is the response */
    public byte[] toBytes() {
        return ByteBuffer.allocate(7)
                .put((byte)MESSAGE_TYPE.ordinal())
                .putInt(_count)
                .put(_carID)
                .put(_floor)
                .array();
    }
}
