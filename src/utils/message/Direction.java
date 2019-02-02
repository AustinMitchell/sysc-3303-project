package utils.message;

public enum Direction {
    UP,
    DOWN;
    
    @Override
    public String toString() {
        switch(ordinal()) {
        case 0:
            return "UP";
        case 1:
            return "DOWN";
        default:
            return "<invalid>";
        }
    }
    
    public static Direction fromOrdinal(int i) {
        switch(i) {
        case 0:
            return UP;
        case 1:
            return DOWN;
        default:
            return null;
        }
    }
}