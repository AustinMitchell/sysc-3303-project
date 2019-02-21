package utils.message;

import java.nio.ByteBuffer;

import main.elevator.ElevatorMotor;

/** Object for responding to an elevator car's action request. */
public class ElevatorActionResponse {
    public static final MessageType MESSAGE_TYPE = MessageType.ELEVATOR_ACTION_RESPONSE;

    private int                         _count;
    private byte                        _carID;
    private boolean                     _takeAction;
    private ElevatorMotor.MotorState    _motorState;

    public int                      messageCount()  { return _count; }
    
    /** Returns the car ID property */
    public int                      carID()         { return _carID; }

    /** Returns whether or not to take action */
    public boolean                  takeAction()    { return _takeAction; }

    /** Returns the new motor state */
    public ElevatorMotor.MotorState action()        { return _motorState; }

    /** Creates a new response object from the given car ID and action to take */
    public ElevatorActionResponse(int carID, boolean takeAction, ElevatorMotor.MotorState action) {
        _count      = Counter.next();
        _carID      = (byte)carID;
        _takeAction = takeAction;
        _motorState = action;
    }

    /**
     * Creates a response object from the given byte array. First byte will be the message type, second byte is the car ID, 
     * third byte is the ElevatorMotor.MotorMovement action
     */
    public ElevatorActionResponse(byte[] inputData) {
        MESSAGE_TYPE.verifyMessage(inputData);
        ByteBuffer buffer = ByteBuffer.wrap(inputData).position(1);

        _count      = buffer.getInt();
        _carID      = buffer.get();
        _takeAction = buffer.get() == 1;
        _motorState = ElevatorMotor.MotorState.fromOrdinal(buffer.get());
    }

    /**
     * Converts this object into a byte array. First byte is the message ID, second byte is the car ID, third byte is
     * the ElevatorMotor.MotorMovement action
     */
    public byte[] toBytes() {
        return ByteBuffer.allocate(8)
                .put((byte)MESSAGE_TYPE.ordinal())
                .putInt(_count)
                .put(_carID)
                .put((byte)(_takeAction ? 1 : 0))
                .put((byte)_motorState.ordinal())
                .array();
    }
}
