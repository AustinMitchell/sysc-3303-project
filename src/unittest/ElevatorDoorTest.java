package unittest;

import static org.junit.Assert.*;

import org.junit.Test;

import main.elevator.ElevatorDoor;

public class ElevatorDoorTest {

    @Test
    public void test() {
        ElevatorDoor door = new ElevatorDoor();

        assertFalse(door.isOpen());

        door.openDoor();

        assertTrue(door.isOpen());

        door.closeDoor();

        assertFalse(door.isOpen());
    }

}
