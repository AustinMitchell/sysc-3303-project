package utils;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.regex.*;

public class TimeStamp implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("^(\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{1,3})$");

    private int _hour;
    private int _minute;
    private int _second;
    private int _millisecond;

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
        _hour        = hour;
        _minute      = minute;
        _second      = second;
        _millisecond = millisecond;
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

        _hour        = Integer.parseInt(match.group(1));
        _minute      = Integer.parseInt(match.group(2));
        _second      = Integer.parseInt(match.group(3));
        _millisecond = Integer.parseInt(match.group(4));
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
        return ByteBuffer.allocate(5).put((byte)_hour).put((byte)_minute).put((byte)_second).putShort((short)_millisecond).array();
    }
}
