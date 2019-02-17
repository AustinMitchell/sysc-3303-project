package unittest;

import static org.junit.Assert.*;

import org.junit.Test;

import main.elevator.ElevatorMotor;
import utils.message.ElevatorContinueRequest;
import utils.message.MessageType;

public class ElevatorContinueRequestTest {

    @Test
    public void test() {
        MessageType              messageTestType = MessageType.ELEVATOR_CONTINUE_REQUEST;
        ElevatorMotor.MotorState motorState      = ElevatorMotor.MotorState.UP;
        byte carID = 4;
        byte[] data = new byte[] {(byte)messageTestType.ordinal(), carID, (byte)motorState.ordinal()};

        ElevatorContinueRequest request1 = new ElevatorContinueRequest(data);
        ElevatorContinueRequest request2 = new ElevatorContinueRequest(carID, motorState);

        assertEquals(carID, request1.carID());
        assertEquals(messageTestType.ordinal(), request1.toBytes()[0]);
        assertEquals(carID, request1.toBytes()[1]);

        assertEquals(carID, request2.carID());
        assertEquals(messageTestType.ordinal(), request2.toBytes()[0]);
    }

}
