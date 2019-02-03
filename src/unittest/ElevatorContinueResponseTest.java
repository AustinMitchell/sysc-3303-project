package unittest;

import static org.junit.Assert.*;

import org.junit.Test;

import utils.message.ElevatorContinueResponse;
import utils.message.MessageType;

public class ElevatorContinueResponseTest {

    @Test
    public void test() {
        MessageType messageTestType = MessageType.ELEVATOR_CONTINUE_RESPONSE;
        int    carID = 4;
        int    floor = 7;
        byte[] data = new byte[] {(byte)messageTestType.ordinal(), (byte)carID, (byte)floor};

        ElevatorContinueResponse request1 = new ElevatorContinueResponse(data);
        ElevatorContinueResponse request2 = new ElevatorContinueResponse(carID, (byte)floor);

        assertEquals(carID, request1.carID());
        assertEquals(messageTestType.ordinal(), request1.toBytes()[0]);
        assertEquals((byte)carID, request1.toBytes()[1]);
        assertEquals((byte)floor, request1.toBytes()[2]);

        assertEquals((byte)carID, request2.carID());
        assertEquals(messageTestType.ordinal(), request2.toBytes()[0]);
        assertEquals((byte)floor, request2.toBytes()[2]);
    }

}
