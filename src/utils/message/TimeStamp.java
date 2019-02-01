package utils.message;

import java.nio.ByteBuffer;
import java.util.regex.*;

public class TimeStamp implements Comparable<TimeStamp> {

    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("^(\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{1,3})$");

    private byte  _hour;
    private byte  _minute;
    private byte  _second;
    private short _millisecond;

    /** Return the hour of timestamp */
    public int hour()        { return _hour; }
    /** Return the minute of timestamp */
    public int minute()      { return _minute; }
    /** Return the second of timestamp */
    public int second()      { return _second; }
    /** Return the millisecond of timestamp */
    public int millisecond() { return _millisecond; }

    /**
     * Construct a TimeStamp object with values for each piece
     * @param hour
     * @param minute
     * @param second
     * @param millisecond
     */
    public TimeStamp(int hour, int minute, int second, int millisecond) {
        _hour        = (byte)hour;
        _minute      = (byte)minute;
        _second      = (byte)second;
        _millisecond = (short)millisecond;
    }

    /**
     * Construct a timestamp object from a string with the following regex format: ^(\d{2}):(\d{2}):(\d{2})\.(\d{1,3})$
     * group(1): hours
     * group(2): minutes
     * group(3): seconds
     * group(4): milliseconds
     * @param timeStamp     String to parse with regex
     */
    public TimeStamp(String timeStamp) {
        Matcher match = TIMESTAMP_PATTERN.matcher(timeStamp);
        match.matches();

        _hour        = (byte)Integer.parseInt(match.group(1));
        _minute      = (byte)Integer.parseInt(match.group(2));
        _second      = (byte)Integer.parseInt(match.group(3));
        _millisecond = (short)Integer.parseInt(match.group(4));
    }
    
    /**
     * Construct a timestamp object from a byte array. Only the first 5 bytes will be considered: 1 byte for hours, minutes and seconds, 2 bytes for milliseconds
     * @param bytes
     */
    public TimeStamp(byte[] bytes) {
        ByteBuffer bytesWrapper = ByteBuffer.wrap(bytes);
        _hour        = bytesWrapper.get();
        _minute      = bytesWrapper.get();
        _second      = bytesWrapper.get();
        _millisecond = bytesWrapper.getShort();
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d.%03d", _hour, _minute, _second, _millisecond);
    }
    
    /** Converts TimeStamp to an array of bytes. First three bytes are the hour, minutes and second, last two bytes are milliseconds */
    public byte[] toBytes() {
        return ByteBuffer.allocate(5).put(_hour).put(_minute).put(_second).putShort(_millisecond).array();
    }
    
    /** Compares this timestamp to another timestamp. Earlier timestamps are considered "less than" later timestamps */
    @Override
    public int compareTo(TimeStamp other) {
        if (this._hour != other._hour) {
            return this._hour - other._hour;
        } else if (this._minute != other._minute) {
            return this._minute - other._minute;
        } else if (this._second != other._second) {
            return this._second - other._second;
        } else {
            return this._millisecond - other._millisecond;
        }
    }
}
