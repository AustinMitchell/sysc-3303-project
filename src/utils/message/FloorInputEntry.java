package utils.message;

import java.nio.ByteBuffer;

public class FloorInputEntry extends InputEntry {

    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private byte        _floor;
    private Direction   _direction;
    private byte        _destination;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */


    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */

    public static final MessageType MESSAGE_TYPE = MessageType.FLOOR_INPUT_ENTRY;

    /* ============================= */
    /* ========== SETTERS ========== */



    /* ============================= */
    /* ========== GETTERS ========== */

    /** Returns floor selection from entry */
    public int          floor()         { return _floor; }
    /** Returns selected direction from entry */
    public Direction    direction()     { return _direction; }
    /** Returns car selection from entry */
    public int          destination()   { return _destination; }

    /* ================================== */
    /* ========== CONSTRUCTORS ========== */

    /** Creates an entry from a line in the input file */
    public FloorInputEntry(String inputLine) {
        _count = Counter.next();

        String[] splitLine = inputLine.split(" ");

        if (splitLine.length != 5) {
            throw new RuntimeException("Invalid input: Did not have exactly 5 columns with single spaces between.");
        }

        _timestamp = new TimeStamp(splitLine[1]);

        _floor = (byte)Integer.parseInt(splitLine[2]);

        switch(splitLine[3].toUpperCase()) {
        case "UP":
            _direction = Direction.UP;
            break;
        case "DOWN":
            _direction = Direction.DOWN;
            break;
        default:
            throw new RuntimeException("Invalid input in column 3: Did not match 'Up' or 'Down', case insensitive");
        }

        _destination = (byte)Integer.parseInt(splitLine[4]);
    }

    /** Creates a new FloorInputEntry from a byte array. Only considers the first 9 bytes */
    public FloorInputEntry(byte[] inputData) {
        MESSAGE_TYPE.verifyMessage(inputData);
        ByteBuffer buffer = (ByteBuffer) ByteBuffer.wrap(inputData).position(1);

        _count          = buffer.getInt();

        byte[] timeStamp = new byte[5];
        buffer.get(timeStamp);
        _timestamp      = new TimeStamp(timeStamp);

        _floor          = buffer.get();
        _direction      = Direction.fromOrdinal(buffer.get());
        _destination    = buffer.get();
    }

    /* ============================= */
    /* ========== METHODS ========== */

    @Override
    public String toString() {
        return String.format("Timestamp: %s, Floor: %d, Direction: %s, Destination: %d", _timestamp.toString(), _floor, _direction, _destination);
    }

    /** Returns the entry as an array of bytes. First 5 bytes are the timestamp, then the next 3 are the floor, direction and destination */
    public byte[] toBytes() {
        return ByteBuffer.allocate(13)
                .put((byte)MESSAGE_TYPE.ordinal())
                .putInt(_count)
                .put(_timestamp.toBytes())
                .put(_floor)
                .put((byte)_direction.ordinal())
                .put(_destination)
                .array();
    }

}
