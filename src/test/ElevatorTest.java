package test;

import java.util.Arrays;

import main.Elevator;
import main.ElevatorMotor;
import utils.message.ElevatorActionResponse;
import utils.message.ElevatorContinueRequest;

public class ElevatorTest {
    public static void main(String[] args) {
        Object o = new Object();
        Elevator e = new Elevator(o, 3, 1);
        
        new Thread(e).start();
        
        e.putMessage(new ElevatorActionResponse(1, ElevatorMotor.MotorState.UP).toBytes());
        
        synchronized(o) {
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
