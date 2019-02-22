package utils.message;

import java.nio.ByteBuffer;

/** Object for requesting a specific elevator car's next move from the scheduler. */
public class ElevatorActionRequest {
    public static final MessageType MESSAGE_TYPE = MessageType.ELEVATOR_ACTION_REQUEST;

    private int     _count;
    private byte    _carID;

    public int messageCount()   { return _count; }

    /** Returns the car ID property */
    public int carID()          { return _carID; }

    /** Creates a new request object from the given car ID */
    public ElevatorActionRequest(int carID) {
        _count = Counter.next();
        _carID = (byte)carID;
    }

    /** Creates a request object from the given byte array. First byte will be the message type, second byte is the car ID */
    public ElevatorActionRequest(byte[] inputData) {
        MESSAGE_TYPE.verifyMessage(inputData);
        ByteBuffer buffer = (ByteBuffer) ByteBuffer.wrap(inputData).position(1);

        _count = buffer.getInt();
        _carID = buffer.get();
    }

    /** Converts this object into a byte array. First byte is the message ID, second byte is the car ID */
    public byte[] toBytes() {
        return ByteBuffer.allocate(6)
                .put((byte)MESSAGE_TYPE.ordinal())
                .putInt(_count)
                .put(_carID)
                .array();
    }
}
