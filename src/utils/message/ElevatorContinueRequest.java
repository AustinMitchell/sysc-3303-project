package utils.message;

import java.nio.ByteBuffer;

import main.elevator.ElevatorMotor.MotorState;;

/** Object for requesting whether an elevator should continue or stop at the floor. */
public class ElevatorContinueRequest {
    public static final MessageType MESSAGE_TYPE = MessageType.ELEVATOR_CONTINUE_REQUEST;
    
    private int         _count;             
    private byte        _carID;
    private MotorState  _actionTaken;
    
    public int          messageCount()  { return _count; }
    
    /** Returns the car ID property */
    public int          carID()         { return _carID; }
    
    /** returns the action taken by the elevator */
    public MotorState   actionTaken()   { return _actionTaken; }
    
    /** Creates a new request object from the given car ID */
    public ElevatorContinueRequest(int carID, MotorState actionTaken) {
        _count          = Counter.next();
        _carID          = (byte)carID;
        _actionTaken    = actionTaken;
    }
    
    /**
     * Creates a request object from the given byte array. First byte will be the message type, second byte is the car ID, third
     * byte is the action taken by the elevator
     */
    public ElevatorContinueRequest(byte[] inputData) {
        MESSAGE_TYPE.verifyMessage(inputData);
        ByteBuffer buffer = ByteBuffer.wrap(inputData).position(1);

        _count          = buffer.getInt();
        _carID          = buffer.get();
        _actionTaken    = MotorState.fromOrdinal(buffer.get());
    }
    
    /**
     * Converts this object into a byte array. First byte is the message ID, second byte is the car ID third byte is the action
     * taken by the elevator
     */
    public byte[] toBytes() {
        return ByteBuffer.allocate(7)
                .put((byte)MESSAGE_TYPE.ordinal())
                .putInt(_count)
                .put(_carID)
                .put((byte)_actionTaken.ordinal())
                .array();
    }
}
