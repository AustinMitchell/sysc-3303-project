package unittest;

import static org.junit.Assert.*;

import org.junit.Test;

import utils.message.MessageType;
import utils.message.SchedulerDestinationRequest;

public class SchedulerDestinationRequestTest {

    @Test
    public void test() {
        MessageType messageTestType = MessageType.SCHEDULER_DESTINATION_REQUEST;
        int carID = 5;
        int floor1 = 4;
        int floor2 = 6;
        int numFloors = 2;

        byte[] data = new byte[] {(byte)messageTestType.ordinal(),
                                  (byte)carID,
                                  (byte)numFloors,
                                  (byte)floor1,
                                  (byte)floor2};

        SchedulerDestinationRequest request1 = new SchedulerDestinationRequest(data);
        SchedulerDestinationRequest request2 = new SchedulerDestinationRequest(carID);

        assertEquals((byte)messageTestType.ordinal(), request1.toBytes()[0]);
        assertEquals((byte)carID, request1.toBytes()[1]);
        assertEquals((byte)numFloors, request1.toBytes()[2]);
        assertEquals((byte)floor1, request1.toBytes()[3]);
        assertEquals((byte)floor2, request1.toBytes()[4]);

        request2.addFloor(floor1);
        request2.addFloor(floor2);

        assertEquals((byte)messageTestType.ordinal(), request2.toBytes()[0]);
        assertEquals((byte)carID, request2.toBytes()[1]);
        assertEquals((byte)numFloors, request2.toBytes()[2]);
        assertEquals((byte)floor1, request2.toBytes()[3]);
        assertEquals((byte)floor2, request2.toBytes()[4]);
    }

}
