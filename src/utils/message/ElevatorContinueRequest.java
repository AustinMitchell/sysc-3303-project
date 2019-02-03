package utils.message;

import main.elevator.ElevatorMotor;

/** Object for requesting whether an elevator should continue or stop at the floor. */
public class ElevatorContinueRequest {
    public static final MessageType MESSAGE_TYPE = MessageType.ELEVATOR_CONTINUE_REQUEST;
    
    private byte                     _carID;
    private ElevatorMotor.MotorState _actionTaken;
    
    /** Returns the car ID property */
    public int                      carID()         { return _carID; }
    
    /** returns the action taken by the elevator */
    public ElevatorMotor.MotorState actionTaken()   { return _actionTaken; }
    
    /** Creates a new request object from the given car ID */
    public ElevatorContinueRequest(int carID, ElevatorMotor.MotorState actionTaken) {
        _carID          = (byte)carID;
        _actionTaken    = actionTaken;
    }
    
    /**
     * Creates a request object from the given byte array. First byte will be the message type, second byte is the car ID, third
     * byte is the action taken by the elevator
     */
    public ElevatorContinueRequest(byte[] inputData) {
        MESSAGE_TYPE.verifyMessage(inputData);
        _carID       = inputData[1];
        _actionTaken = ElevatorMotor.MotorState.fromOrdinal(inputData[2]);
    }
    
    /**
     * Converts this object into a byte array. First byte is the message ID, second byte is the car ID third byte is the action
     * taken by the elevator
     */
    public byte[] toBytes() {
        return new byte[] { (byte)MESSAGE_TYPE.ordinal(), _carID, (byte)_actionTaken.ordinal() };
    }
}
