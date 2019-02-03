package unittest;

import static org.junit.Assert.*;

import org.junit.Test;

import utils.message.MessageType;

public class MessageTypeTest {

    @Test
    public void test() {
        assertEquals("FLOOR_INPUT_ENTRY", MessageType.FLOOR_INPUT_ENTRY.toString());
        assertEquals("ELEVATOR_ACTION_RESPONSE", MessageType.ELEVATOR_ACTION_RESPONSE.toString());
        assertEquals("ELEVATOR_ACTION_REQUEST", MessageType.ELEVATOR_ACTION_REQUEST.toString());
        assertEquals("ELEVATOR_CONTINUE_RESPONSE", MessageType.ELEVATOR_CONTINUE_RESPONSE.toString());
        assertEquals("ELEVATOR_CONTINUE_REQUEST", MessageType.ELEVATOR_CONTINUE_REQUEST.toString());
        assertEquals("SCHEDULER_DESTINATION_REQUEST", MessageType.SCHEDULER_DESTINATION_REQUEST.toString());
        assertEquals("ELEVATOR_BUTTON_PUSH_EVENT", MessageType.ELEVATOR_BUTTON_PUSH_EVENT.toString());

        assertEquals(MessageType.FLOOR_INPUT_ENTRY, MessageType.fromOrdinal(0));
        assertEquals(MessageType.ELEVATOR_ACTION_RESPONSE, MessageType.fromOrdinal(1));
        assertEquals(MessageType.ELEVATOR_ACTION_REQUEST, MessageType.fromOrdinal(2));
        assertEquals(MessageType.ELEVATOR_CONTINUE_RESPONSE, MessageType.fromOrdinal(3));
        assertEquals(MessageType.ELEVATOR_CONTINUE_REQUEST, MessageType.fromOrdinal(4));
        assertEquals(MessageType.SCHEDULER_DESTINATION_REQUEST, MessageType.fromOrdinal(5));
        assertEquals(MessageType.ELEVATOR_BUTTON_PUSH_EVENT, MessageType.fromOrdinal(6));
    }

}
