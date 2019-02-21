package utils.message;

import java.nio.ByteBuffer;

public class FloorInputEntry implements Comparable<FloorInputEntry> {
    public static final MessageType MESSAGE_TYPE = MessageType.FLOOR_INPUT_ENTRY;

    private int         _count;
    private TimeStamp   _timestamp;
    private byte        _floor;
    private Direction   _direction;
    private byte        _destination;


    public int          messageCount()  { return _count; }
    /** Returns timestamp from entry */
    public TimeStamp    timestamp()     { return _timestamp; }
    /** Returns floor selection from entry */
    public int          floor()         { return _floor; }
    /** Returns selected direction from entry */
    public Direction    direction()     { return _direction; }
    /** Returns car selection from entry */
    public int          destination()   { return _destination; }

    /** Creates an entry from a line in the input file */
    public FloorInputEntry(String inputLine) {
        _count = Counter.next();

        String[] splitLine = inputLine.split(" ");

        if (splitLine.length != 4) {
            throw new RuntimeException("Invalid input: Did not have exactly 4 columns with single spaces between.");
        }

        _timestamp = new TimeStamp(splitLine[0]);

        _floor = (byte)Integer.parseInt(splitLine[1]);

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

        _destination = (byte)Integer.parseInt(splitLine[3]);
    }

    /** Creates a new FloorInputEntry from a byte array. Only considers the first 9 bytes */
    public FloorInputEntry(byte[] inputData) {
        MESSAGE_TYPE.verifyMessage(inputData);
        ByteBuffer buffer = ByteBuffer.wrap(inputData).position(1);

        _count          = buffer.getInt();

        byte[] timeStamp = new byte[5];
        buffer.get(timeStamp);
        _timestamp      = new TimeStamp(timeStamp);

        _floor          = buffer.get();
        _direction      = Direction.fromOrdinal(buffer.get());
        _destination    = buffer.get();
    }

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

    /**
     * Calculates the difference between self and another entry and returns the
     * result in milliseconds
     *
     * @param entry
     * @return difference in milliseconds
     */
    public int differenceInMilliseconds(FloorInputEntry entry) {
        return this._timestamp.toMilliseconds() - entry.timestamp().toMilliseconds();
    }

    /** Compares the timestamps of this entry and another entry. Earlier timestamps are considered "less than" later timestamps. */
    @Override
    public int compareTo(FloorInputEntry other) {
        return this._timestamp.compareTo(other._timestamp);
    }
}
