package utils.message;

import main.ElevatorMotor;

/** Object for responding to an elevator car's action request. */
public class ElevatorActionResponse {
    public static final MessageType MESSAGE_TYPE = MessageType.ELEVATOR_ACTION_RESPONSE;
    
    private byte                        _carID;
    private ElevatorMotor.MotorMovement _action;
    
    /** Returns the car ID property */
    public int carID() { return _carID; }
    
    /** Creates a new response object from the given car ID and action to take */
    public ElevatorActionResponse(int carID, ElevatorMotor.MotorMovement action) {
        _carID  = (byte)carID;
        _action = action;
    }
    
    /** Creates a response object from the given byte array. First byte will be the message type, second byte is the car ID, 
     * third byte is the ElevatorMotor.MotorMovement action */
    public ElevatorActionResponse(byte[] inputData) {
        MESSAGE_TYPE.verifyMessage(inputData);
        _carID = inputData[1];
        _action = ElevatorMotor.MotorMovement.fromOrdinal(inputData[2]);
    }
    
    /** Converts this object into a byte array. First byte is the message ID, second byte is the car ID, third byte is
     * the ElevatorMotor.MotorMovement action */
    public byte[] toBytes() {
        return new byte[] { (byte)MESSAGE_TYPE.ordinal(), _carID, (byte)_action.ordinal() };
    }
}
