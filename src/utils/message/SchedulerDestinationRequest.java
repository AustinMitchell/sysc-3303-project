package utils.message;

/** Object for sending to an elevator car to determine which elevator button it needs to press */
public class SchedulerDestinationRequest {
    public static final MessageType MESSAGE_TYPE = MessageType.ELEVATOR_CONTINUE_RESPONSE;
    
    private byte _carID;
    private byte _destinationFloor;
    
    /** Returns the target car ID */
    public int carID()              { return _carID; }
    /** Returns the destination floor */
    public int destinationFloor()   { return _destinationFloor; }

    /** Creates a new request object from the given car ID and destination floor */
    public SchedulerDestinationRequest(int carID, int destinationFloor) {
        _carID              = (byte)carID;
        _destinationFloor   = (byte)destinationFloor;
    }
    
    /** Creates a new request object from the byte array. First byte is the message type, second is the car ID, third
     * is the target destination floor */
    public SchedulerDestinationRequest(byte[] inputData) {
        MESSAGE_TYPE.verifyMessage(inputData);
        _carID              = inputData[1];
        _destinationFloor   = inputData[2];
    }
    
    /** Converts object into a byte array. First byte is the message type, second is the car ID, third is the target
     * destination floor */
    public byte[] toBytes() {
        return new byte[] { (byte)MESSAGE_TYPE.ordinal(), _carID, _destinationFloor };
    }
}
