package utils.message;

import main.elevator.ElevatorMotor;

/** Object for responding to an elevator car's action request. */
public class ElevatorActionResponse {
    public static final MessageType MESSAGE_TYPE = MessageType.ELEVATOR_ACTION_RESPONSE;

    private byte                     _carID;
    private boolean                  _takeAction;
    private ElevatorMotor.MotorState _motorState;

    /** Returns the car ID property */
    public int                      carID()         { return _carID; }

    /** Returns whether or not to take action */
    public boolean                  takeAction()    { return _takeAction; }

    /** Returns the new motor state */
    public ElevatorMotor.MotorState action()        { return _motorState; }

    /** Creates a new response object from the given car ID and action to take */
    public ElevatorActionResponse(int carID, boolean takeAction, ElevatorMotor.MotorState action) {
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
        _carID      = inputData[1];
        _takeAction = (inputData[2]) == 1 ? true : false;
        _motorState = ElevatorMotor.MotorState.fromOrdinal(inputData[3]);
    }

    /**
     * Converts this object into a byte array. First byte is the message ID, second byte is the car ID, third byte is
     * the ElevatorMotor.MotorMovement action
     */
    public byte[] toBytes() {
        return new byte[] { (byte)MESSAGE_TYPE.ordinal(), _carID, (byte)(_takeAction ? 1 : 0), (byte)_motorState.ordinal() };
    }
}
