package unittest;

import static org.junit.Assert.*;

import org.junit.Test;

import utils.message.Direction;
import utils.message.FloorInputEntry;
import utils.message.TimeStamp;

public class FloorInputEntryTest {

    @Test
    public void test() {

        TimeStamp   timeStamp   = new TimeStamp("14:10:50.300");
        TimeStamp   timeStamp2  = new TimeStamp("14:10:50.600");
        byte        floor       = 10;
        Direction   direction   = Direction.UP;
        byte        destination = 15;

        FloorInputEntry entry1 = new FloorInputEntry(String.format("%s %d %s %d", timeStamp.toString(), floor, direction.toString(), destination));
        FloorInputEntry entry2 = new FloorInputEntry(String.format("%s %d %s %d", timeStamp2.toString(), floor, direction.toString(), destination));

        assertEquals(timeStamp.toString(), entry1.timestamp().toString());
        assertEquals(floor, entry1.floor());
        assertEquals(direction, entry1.direction());
        assertEquals(destination, entry1.destination());

        assertEquals(String.format("Timestamp: %s, Floor: %d, Direction: %s, Car: %d", timeStamp.toString(), floor, direction.toString(), destination), entry1.toString());

        assertEquals(14, entry1.toBytes()[1]);
        assertEquals(10, entry1.toBytes()[2]);
        assertEquals(50, entry1.toBytes()[3]);
        assertEquals(1, entry1.toBytes()[4]);
        assertEquals(44, entry1.toBytes()[5]);
        assertEquals(10, entry1.toBytes()[6]);
        assertEquals(0, entry1.toBytes()[7]);
        assertEquals(15, entry1.toBytes()[8]);

        assertEquals(300, entry2.differenceInMilliseconds(entry1));
    }

}
