package unittest;

import static org.junit.Assert.*;

import org.junit.Test;

import utils.message.ElevatorButtonPushEvent;
import utils.message.MessageType;

public class ElevatorButtonPushEventTest {

    @Test
    public void test() {
        MessageType messageTestType = MessageType.ELEVATOR_BUTTON_PUSH_EVENT;
        int carID       = 5;
        int floorNumber = 7;
        byte[] data = new byte[] {(byte)messageTestType.ordinal(), (byte)carID, (byte)floorNumber};

        ElevatorButtonPushEvent request1 = new ElevatorButtonPushEvent(data);
        ElevatorButtonPushEvent request2 = new ElevatorButtonPushEvent(carID, floorNumber);

        assertEquals((byte)carID, request1.carID());
        assertEquals(messageTestType.ordinal(), request1.toBytes()[0]);
        assertEquals((byte)carID, request1.toBytes()[1]);
        assertEquals((byte)floorNumber, request1.toBytes()[2]);

        assertEquals((byte)carID, request2.carID());
        assertEquals(messageTestType.ordinal(), request2.toBytes()[0]);
        assertEquals((byte)carID, request2.toBytes()[1]);
        assertEquals((byte)floorNumber, request2.toBytes()[2]);
    }

}
