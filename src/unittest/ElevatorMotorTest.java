package unittest;

import static org.junit.Assert.*;

import org.junit.Test;

import main.elevator.ElevatorMotor;
import main.elevator.ElevatorMotor.MotorState;

public class ElevatorMotorTest {

    @Test
    public void test() {
        MotorState down          = MotorState.DOWN;
        MotorState up            = MotorState.UP;
        MotorState stationary    = MotorState.STATIONARY;

        ElevatorMotor motor1   = new ElevatorMotor();
        ElevatorMotor motor2   = new ElevatorMotor(down);
        ElevatorMotor motor3   = new ElevatorMotor(up);
        ElevatorMotor motor4   = new ElevatorMotor(stationary);

        assertEquals(stationary, motor1.motorState());
        assertEquals(down, motor2.motorState());
        assertEquals(up, motor3.motorState());
        assertEquals(stationary, motor4.motorState());

        motor1.setMotorState(up);

        assertEquals(up, motor1.motorState());
    }

}
