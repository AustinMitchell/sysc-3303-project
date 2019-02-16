package unittest;

import static org.junit.Assert.*;

import org.junit.Test;

import main.elevator.ElevatorMotor.*;
import utils.message.ElevatorActionResponse;
import utils.message.MessageType;

public class ElevatorActionResponseTest {

    @Test
    public void test() {
        MessageType messageTestType = MessageType.ELEVATOR_ACTION_RESPONSE;
        int carID = 5;
        MotorState motorMovement = MotorState.STATIONARY;
        byte[] data = new byte[] {(byte)messageTestType.ordinal(), (byte)carID, 0, (byte)motorMovement.ordinal()};

        ElevatorActionResponse request1 = new ElevatorActionResponse(data);
        ElevatorActionResponse request2 = new ElevatorActionResponse(carID, false, motorMovement);

        assertEquals((byte)carID, request1.carID());
        assertEquals(messageTestType.ordinal(), request1.toBytes()[0]);
        assertEquals((byte)carID, request1.toBytes()[1]);
        assertEquals((byte)motorMovement.ordinal(), request1.toBytes()[3]);

        assertEquals((byte)carID, request2.carID());
        assertEquals(messageTestType.ordinal(), request2.toBytes()[0]);
        assertEquals((byte)carID, request2.toBytes()[1]);
        assertEquals((byte)motorMovement.ordinal(), request2.toBytes()[3]);
    }

}
