package unittest;

import static org.junit.Assert.*;

import java.sql.Date;
import java.util.Timer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import main.elevator.Elevator;
import main.elevator.ElevatorMotor.MotorState;
import utils.message.ElevatorActionResponse;
import utils.message.ElevatorContinueRequest;
import utils.message.ElevatorContinueResponse;
import utils.message.MessageType;

public class ElevatorTest {

    Thread      elevatorThread;
    Elevator    elevator;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        int testNumFloors   = 10;
        int testCarID       = 3;

        byte[] response;

        ElevatorActionResponse actionResponse = new ElevatorActionResponse(testCarID, true, MotorState.UP);
        ElevatorContinueResponse continueResponse1 = new ElevatorContinueResponse(3, -1);
        ElevatorContinueResponse continueResponse2 = new ElevatorContinueResponse(3, 9);

        elevator = new Elevator(this, testNumFloors, testCarID);
        elevatorThread =  new Thread(elevator);

        elevatorThread.start();

        // ===== Testing ElevatorActionResponse =====
        assertEquals(MotorState.STATIONARY, elevator.motorState());

        elevator.putMessage(actionResponse.toBytes());

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(MotorState.UP, elevator.motorState());

        response = elevator.getMessage();

        assertEquals(MessageType.ELEVATOR_CONTINUE_REQUEST.ordinal(), response[0]);
        ElevatorContinueRequest continueRequest = new ElevatorContinueRequest(response);
        assertEquals(testCarID, continueRequest.carID());
        assertEquals(MotorState.STATIONARY, continueRequest.actionTaken());

        // ===== Testing ElevatorContinueResponse =====
        elevator.putMessage(continueResponse1.toBytes());
    }

}
