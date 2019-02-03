package unittest;

import static org.junit.Assert.*;

import org.junit.Test;

import utils.message.TimeStamp;

public class TimeStampTest {

    @Test
    public void test() {

        int hour            = 14;
        int minute          = 10;
        int second          = 11;
        int millisecond     = 500;

        TimeStamp timeStamp1 = new TimeStamp(hour, minute, second, millisecond);
        TimeStamp timeStamp2 = new TimeStamp(String.valueOf(hour) + ":" +
                                             String.valueOf(minute) + ":" +
                                             String.valueOf(second) + "." +
                                             String.valueOf(millisecond));
        TimeStamp timeStamp3 = new TimeStamp(new byte[] {(byte) hour,
                                                         (byte) minute,
                                                         (byte) second,
                                                         (byte) 1,
                                                         (byte) 244});

        // ==================================================
        assertEquals(hour, timeStamp1.hour());
        assertEquals(minute, timeStamp1.minute());
        assertEquals(second, timeStamp1.second());
        assertEquals(millisecond, timeStamp1.millisecond());

        assertEquals(51011500, timeStamp1.toMilliseconds());

        assertEquals("14:10:11.500", timeStamp1.toString());

        assertEquals((byte)hour, timeStamp1.toBytes()[0]);
        assertEquals((byte)minute, timeStamp1.toBytes()[1]);
        assertEquals((byte)second, timeStamp1.toBytes()[2]);

        // ==================================================
        assertEquals(hour, timeStamp2.hour());
        assertEquals(minute, timeStamp2.minute());
        assertEquals(second, timeStamp2.second());
        assertEquals(millisecond, timeStamp2.millisecond());

        assertEquals(51011500, timeStamp2.toMilliseconds());

        assertEquals("14:10:11.500", timeStamp2.toString());

        assertEquals((byte)hour, timeStamp2.toBytes()[0]);
        assertEquals((byte)minute, timeStamp2.toBytes()[1]);
        assertEquals((byte)second, timeStamp2.toBytes()[2]);

        // ==================================================
        assertEquals(hour, timeStamp3.hour());
        assertEquals(minute, timeStamp3.minute());
        assertEquals(second, timeStamp3.second());
        assertEquals(millisecond, timeStamp3.millisecond());

        assertEquals(51011500, timeStamp3.toMilliseconds());

        assertEquals("14:10:11.500", timeStamp3.toString());

        assertEquals((byte)hour, timeStamp3.toBytes()[0]);
        assertEquals((byte)minute, timeStamp3.toBytes()[1]);
        assertEquals((byte)second, timeStamp3.toBytes()[2]);
    }

}
