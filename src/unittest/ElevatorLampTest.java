package unittest;

import static org.junit.Assert.*;

import org.junit.Test;

import main.elevator.*;

public class ElevatorLampTest {

    @Test
    public void test() {
        ElevatorLamp lamp = new ElevatorLamp();

        assertFalse(lamp.isLit());

        lamp.turnOFF();

        assertFalse(lamp.isLit());

        lamp.turnON();

        assertTrue(lamp.isLit());
    }

}
