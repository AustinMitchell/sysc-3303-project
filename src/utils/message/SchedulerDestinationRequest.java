package utils.message;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/** Object for sending to an elevator car to determine which elevator buttons it needs to press */
public class SchedulerDestinationRequest {
    public static final MessageType MESSAGE_TYPE = MessageType.SCHEDULER_DESTINATION_REQUEST;

    private int             _count;
    private byte            _carID;
    private ArrayList<Byte> _destinationFloors;

    public int messageCount()               { return _count; }
    /** Returns the target car ID */
    public int carID()                      { return _carID; }
    /** Returns the destination floor at an index */
    public int destinationFloor(int idx)    { return _destinationFloors.get(idx); }
    /** Returns the number of destination floors */
    public int destinationFloorCount()      { return _destinationFloors.size(); } 

    /** Add another destination floor to the request */
    public void addFloor(int newFloor)      { _destinationFloors.add((byte)newFloor); }

    /** Creates a new request object from the given car ID and destination floor */
    public SchedulerDestinationRequest(int carID) {
        _count              = Counter.next();
        _carID              = (byte)carID;
        _destinationFloors  = new ArrayList<>();
    }

    /** Creates a new request object from the byte array. First byte is the message type, second is the car ID, third
     * is the target destination floor */
    public SchedulerDestinationRequest(byte[] inputData) {
        MESSAGE_TYPE.verifyMessage(inputData);
        ByteBuffer buffer = ByteBuffer.wrap(inputData).position(1);

        _count              = buffer.getInt();
        _carID              = buffer.get();
        _destinationFloors  = new ArrayList<>();

        int numEntries      = buffer.get();
        for (int i=0; i<numEntries; i++) {
            addFloor(buffer.get());
        }
    }

    public byte[] destinationsAsArray() {
        byte[] listAsArray = new byte[_destinationFloors.size()];
        for(int i=0; i<listAsArray.length; i++) {
            listAsArray[i] = _destinationFloors.get(i);
        }
        return listAsArray;
    }

    /** Converts object into a byte array. First byte is the message type, second is the car ID, third is the target
     * destination floor */
    public byte[] toBytes() {
        return ByteBuffer.allocate(7+_destinationFloors.size())
                .put((byte)MESSAGE_TYPE.ordinal())
                .putInt(_count)
                .put(_carID)
                .put((byte)_destinationFloors.size())
                .put(destinationsAsArray())
                .array();
    }
}
