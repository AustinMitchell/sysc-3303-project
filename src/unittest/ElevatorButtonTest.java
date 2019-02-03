package unittest;

import static org.junit.Assert.*;

import org.junit.Test;

import main.elevator.*;

public class ElevatorButtonTest {

    @Test
    public void test() {
        ElevatorLamp    lamp1   = new ElevatorLamp();
        ElevatorButton  button1 = new ElevatorButton(lamp1);

        assertFalse(lamp1.isLit());
        button1.pressButton();
        assertTrue(lamp1.isLit());
    }

}
