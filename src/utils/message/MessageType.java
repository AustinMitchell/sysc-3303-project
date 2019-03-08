package utils.message;

import java.util.Arrays;

public enum MessageType {
    FLOOR_INPUT_ENTRY,
    ELEVATOR_ACTION_RESPONSE,
    ELEVATOR_ACTION_REQUEST,
    ELEVATOR_CONTINUE_RESPONSE,
    ELEVATOR_CONTINUE_REQUEST,
    SCHEDULER_DESTINATION_REQUEST,
    ELEVATOR_BUTTON_PUSH_EVENT,
    ERROR_INPUT_ENTRY,
    ELEVATOR_ERROR;

    private static final MessageType[] VALUES = MessageType.values();

    public static MessageType fromOrdinal(int i) {
        return VALUES[i];
    }

    public void verifyMessage(byte[] data) {
        if (data[0] != ordinal()) {
            throw new RuntimeException("Message failed verification.%n"
                                       + String.format("MessageType: %s%n", this)
                                       + String.format("Data: %s", Arrays.toString(data)));
        }
    }
}