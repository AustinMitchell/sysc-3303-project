package utils.message;

public enum SystemFault {
    ELEVATOR_STUCK,
    DOOR_STUCK_OPEN,
    DOOR_STUCK_CLOSED;

    public static SystemFault fromOrdinal(int i) {
        switch(i) {
            case 0:
                return ELEVATOR_STUCK;
            case 1:
                return DOOR_STUCK_OPEN;
            case 2:
                return DOOR_STUCK_CLOSED;
            default:
                return null;
        }
    }
}
