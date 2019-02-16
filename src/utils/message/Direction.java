package utils.message;

import main.elevator.ElevatorMotor;

public enum Direction {
    UP,
    DOWN;
    
    public ElevatorMotor.MotorState toMotorState() {
        switch(this) {
        case UP:
            return ElevatorMotor.MotorState.UP;
        case DOWN:
            return ElevatorMotor.MotorState.DOWN;
        default:
            return null;
        }
    }
    
    public static Direction fromOrdinal(int i) {
        switch(i) {
        case 0:
            return UP;
        case 1:
            return DOWN;
        default:
            return null;
        }
    }
}
