package unittest;

import static org.junit.Assert.*;

import org.junit.Test;

import utils.message.ElevatorActionRequest;
import utils.message.MessageType;

public class ElevatorActionRequestTest {

    @Test
    public void test() {
        MessageType messageTestType = MessageType.ELEVATOR_ACTION_REQUEST;
        byte carID = 4;
        byte[] data = new byte[] {(byte)messageTestType.ordinal(), carID};

        ElevatorActionRequest request1 = new ElevatorActionRequest(data);
        ElevatorActionRequest request2 = new ElevatorActionRequest(carID);

        assertEquals(carID, request1.carID());
        assertEquals(messageTestType.ordinal(), request1.toBytes()[0]);
        assertEquals(carID, request1.toBytes()[1]);

        assertEquals(carID, request2.carID());
        assertEquals(messageTestType.ordinal(), request2.toBytes()[0]);
    }

}
