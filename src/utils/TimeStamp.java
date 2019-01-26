package utils;

import java.util.regex.*;

public class TimeStamp {
    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("(\\d\\d):(\\d\\d):(\\d\\d).(\\d\\d\\d)");
    
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
     * Construct a timestamp object from a string with the following regex format: (\d\d):(\d\d):(\d\d).(\d\d\d)
     * group(1): hours
     * group(2): minutes
     * group(3): seconds
     * group(4): milliseconds
     * @param timeStamp     String to parse with regex
     */
    public TimeStamp(String timeStamp) {
        Matcher match = TIMESTAMP_PATTERN.matcher(timeStamp);

        _hour        = Integer.parseInt(match.group(1));
        _minute      = Integer.parseInt(match.group(2));
        _second      = Integer.parseInt(match.group(3));
        _millisecond = Integer.parseInt(match.group(4));
    }
    
    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d.%03d", _hour, _minute, _second, _millisecond);
    }
}
