package utils.message;

import java.util.Arrays;

public enum MessageType {
    FLOOR_INPUT_ENTRY,
    ELEVATOR_ACTION_RESPONSE,
    ELEVATOR_ACTION_REQUEST,
    ELEVATOR_CONTINUE_RESPONSE,
    ELEVATOR_CONTINUE_REQUEST,
    SCHEDULER_DESTINATION_REQUEST,
    ELEVATOR_BUTTON_PUSH_EVENT;

    public static MessageType fromOrdinal(int i) {
        switch(i) {
        case 0:
            return FLOOR_INPUT_ENTRY;
        case 1:
            return ELEVATOR_ACTION_RESPONSE;
        case 2:
            return ELEVATOR_ACTION_REQUEST;
        case 3:
            return ELEVATOR_CONTINUE_RESPONSE;
        case 4:
            return ELEVATOR_CONTINUE_REQUEST;
        case 5:
            return SCHEDULER_DESTINATION_REQUEST;
        case 6:
            return ELEVATOR_BUTTON_PUSH_EVENT;
        default:
            return null;
        }
    }

    public void verifyMessage(byte[] data) {
        if (data[0] != ordinal()) {
            throw new RuntimeException("Message failed verification.%n"
                                       + String.format("MessageType: %s%n", this)
                                       + String.format("Data: %s", Arrays.toString(data)));
        }
    }
}