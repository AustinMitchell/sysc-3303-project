package utils.message;

import java.util.Arrays;

public enum MessageType {
    FLOOR_INPUT_ENTRY,
    ELEVATOR_ACTION_RESPONSE,
    ELEVATOR_ACTION_REQUEST;
    
    public static MessageType fromOrdinal(int i) {
        switch(i) {
        case 0:
            return FLOOR_INPUT_ENTRY;
        case 1:
            return ELEVATOR_ACTION_RESPONSE;
        case 2:
            return ELEVATOR_ACTION_REQUEST;
        default:
            return null;
        }
    }
    
    @Override
    public String toString() {
        switch(ordinal()) {
        case 0:
            return "FLOOR_INPUT_ENTRY";
        case 1:
            return "ELEVATOR_ACTION_RESPONSE";
        case 2:
            return "ELEVATOR_ACTION_REQUEST";
        default:
            return "<invalid>";
        }
    }
    
    public void verifyMessage(byte[] data) {
        if (data[0] != ordinal()) {
            throw new RuntimeException("Message failed verification.\n"
                                       + String.format("MessageType: %s\n", this)
                                       + String.format("Data: %s\n", Arrays.toString(data)));
        }
    }
}
