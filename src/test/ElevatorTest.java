package test;

import java.util.Arrays;

import main.elevator.Elevator;
import main.elevator.ElevatorMotor;
import utils.message.ElevatorActionResponse;
import utils.message.ElevatorContinueRequest;

public class ElevatorTest {
    public static void main(String[] args) {
        Object o = new Object();
        Elevator e = new Elevator(o, 3, 1);
        
        new Thread(e).start();
        
        synchronized(o) {
            e.putMessage(new ElevatorActionResponse(1, true, ElevatorMotor.MotorState.UP).toBytes());
            try {
                o.wait();
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        
        System.out.println(Arrays.toString(new ElevatorContinueRequest(e.getMessage()).toBytes()));
    }
}
