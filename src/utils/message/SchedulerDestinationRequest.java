package utils.message;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/** Object for sending to an elevator car to determine which elevator buttons it needs to press */
public class SchedulerDestinationRequest {
    public static final MessageType MESSAGE_TYPE = MessageType.SCHEDULER_DESTINATION_REQUEST;
    
    private byte _carID;
    private ArrayList<Byte> _destinationFloors;
    
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
        _carID              = (byte)carID;
        _destinationFloors  = new ArrayList<>();
    }
    
    /** Creates a new request object from the byte array. First byte is the message type, second is the car ID, third
     * is the target destination floor */
    public SchedulerDestinationRequest(byte[] inputData) {
        MESSAGE_TYPE.verifyMessage(inputData);
        _carID              = inputData[1];
        _destinationFloors  = new ArrayList<>();
        for (int i=0; i<inputData[2]; i++) {
            addFloor(inputData[3+i]);
        }
    }
    
    private byte[] destinationsAsArray() {
        byte[] listAsArray = new byte[_destinationFloors.size()];
        for(int i=0; i<listAsArray.length; i++) {
            listAsArray[i] = _destinationFloors.get(i);
        }
        return listAsArray;
    }
    
    /** Converts object into a byte array. First byte is the message type, second is the car ID, third is the target
     * destination floor */
    public byte[] toBytes() {
        int bufferSize = 3+_destinationFloors.size();
        return ByteBuffer.allocate(bufferSize).put((byte)MESSAGE_TYPE.ordinal()).put(_carID).put((byte)_destinationFloors.size()).put(destinationsAsArray()).array();
    }
}
