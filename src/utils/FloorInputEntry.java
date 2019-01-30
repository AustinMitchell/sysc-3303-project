package utils;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class FloorInputEntry implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private TimeStamp 	_timestamp;
    private int 		_floor;
    private Direction 	_direction;
    private int 		_car;
    
    /** Returns timestamp from entry */
    public TimeStamp 	timestamp() { return _timestamp; }
    /** Returns floor selection from entry */
    public int 			floor()		{ return _floor; }
    /** Returns selected direction from entry */
    public Direction	direction() { return _direction; }
    /** Returns car selection from entry */
    public int 			car()		{ return _car; }
    
    /** Creates an entry from a line in the input file */
    public FloorInputEntry(String inputLine) {
        String[] splitLine = inputLine.split(" ");
        
        if (splitLine.length != 4) {
            throw new RuntimeException("Invalid input: Did not have exactly 4 columns with single spaces between.");
        }
        
        _timestamp = new TimeStamp(splitLine[0]);
        
        _floor = Integer.parseInt(splitLine[1]);
        
        switch(splitLine[2].toUpperCase()) {
        case "UP":
            _direction = Direction.UP;
            break;
        case "DOWN":
            _direction = Direction.DOWN;
            break;
        default:
            throw new RuntimeException("Invalid input in column 3: Did not match 'Up' or 'Down', case insensitive");
        }
        
        _car = Integer.parseInt(splitLine[3]);
    }
    
    /** Creates a new FloorInputEntry from a byte array. Only considers the first 9 bytes */
    public FloorInputEntry(byte[] inputData) {
        _timestamp  = new TimeStamp(inputData);
        _floor      = inputData[5];
        _direction  = Direction.fromInt(inputData[6]);
        _car        = inputData[7];
    }
    
    @Override
    public String toString() {
        return String.format("Timestamp: %s, Floor: %d, Direction: %s, Car: %d", _timestamp.toString(), _floor, _direction, _car);
    }
    
    /** Returns the entry as an array of bytes. First 5 bytes are the timestamp, then the next 3 are the floor, direction and car */
    public byte[] toBytes() {
        return ByteBuffer.allocate(8).put(_timestamp.toBytes()).put((byte)_floor).put((byte)_direction.ordinal()).put((byte)_car).array();
    }
}
