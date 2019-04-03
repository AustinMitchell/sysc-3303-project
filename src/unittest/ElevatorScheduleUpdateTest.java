package unittest;

import static org.junit.Assert.*;

import org.junit.Test;

import main.ElevatorSchedule;
import main.FloorStop;
import main.elevator.ElevatorMotor.MotorState;
import utils.message.ElevatorScheduleUpdate;

public class ElevatorScheduleUpdateTest {

    @Test
    public void test() {
        ElevatorSchedule testSchedule = new ElevatorSchedule();

        testSchedule.setCurrentFloor(5);
        testSchedule.setDoorStuck(true);

        testSchedule.addNewTarget(new FloorStop(1, MotorState.UP));

        testSchedule.addNewTarget(new FloorStop(10, MotorState.UP));
        testSchedule.addNewTarget(new FloorStop(5, MotorState.UP));
        testSchedule.addNewTarget(new FloorStop(20, MotorState.UP));

        ElevatorScheduleUpdate testScheduleUpdate1 = new ElevatorScheduleUpdate(1, testSchedule);

        assertEquals(1, testScheduleUpdate1.elevatorID());
        assertEquals(5, testScheduleUpdate1.currentFloor());
        assertEquals(1, testScheduleUpdate1.currentTarget());
        assertEquals(MotorState.DOWN, testScheduleUpdate1.motorState());
        assertEquals(true, testScheduleUpdate1.doorStuck());
        assertEquals(false, testScheduleUpdate1.motorStuck());

        assertEquals(3, testScheduleUpdate1.elevatorStops().size());
        assertEquals(5, (int) testScheduleUpdate1.elevatorStops().get(0));
        assertEquals(10, (int) testScheduleUpdate1.elevatorStops().get(1));
        assertEquals(20, (int) testScheduleUpdate1.elevatorStops().get(2));

        byte[] testArray = new byte[] {9, 0, 0, 0, 0, 1, 1, 5, 1, 0, 1, 3, 5, 10, 20};

        assertArrayEquals(testArray, testScheduleUpdate1.toBytes());

        ElevatorScheduleUpdate testScheduleUpdate2 = new ElevatorScheduleUpdate(testScheduleUpdate1.toBytes());

        assertEquals(1, testScheduleUpdate2.elevatorID());
        assertEquals(5, testScheduleUpdate2.currentFloor());
        assertEquals(1, testScheduleUpdate2.currentTarget());
        assertEquals(MotorState.DOWN, testScheduleUpdate2.motorState());
        assertEquals(true, testScheduleUpdate2.doorStuck());
        assertEquals(false, testScheduleUpdate2.motorStuck());

        assertEquals(3, testScheduleUpdate2.elevatorStops().size());
        assertEquals(5, (int) testScheduleUpdate2.elevatorStops().get(0));
        assertEquals(10, (int) testScheduleUpdate2.elevatorStops().get(1));
        assertEquals(20, (int) testScheduleUpdate2.elevatorStops().get(2));
    }
}
