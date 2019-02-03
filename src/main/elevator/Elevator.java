package main.elevator;


import utils.DataBox;
import utils.message.*;

public class Elevator implements Runnable {
    public static final int FLOOR_MOVEMENT_TIMEOUT = 5000;
    public static final int DOOR_MOVEMENT_TIMEOUT = 2000;

    private DataBox<byte[]>     _messageOutgoing;
    private DataBox<byte[]>     _messageIncoming;
    
    private Object              _observer;
    
    private int                 _numFloors;
    private int                 _carID;
    
    private ElevatorDoor        _door;
    private ElevatorMotor       _motor;
    private ElevatorButton[]    _buttons;
    private ElevatorLamp[]      _lamps;
    
    /** Return the state of the elevator's motor */
    public ElevatorMotor.MotorState motorState() { return _motor.motorState(); }
    
    /**
     * Creates a new Elevator
     * @param observer      Object which will be notified when messages come out of the Elevator
     * @param numFloors     Number of floors to accomodate
     * @param carID         ID of this car, used for message construction
     */
    public Elevator(Object observer, int numFloors, int carID) {
        _messageIncoming = new DataBox<>();
        _messageOutgoing = new DataBox<>();
        
        _observer       = observer;
        _numFloors      = numFloors;
        _carID          = carID;
        
        _door           = new ElevatorDoor();
        _motor          = new ElevatorMotor();
        _buttons        = new ElevatorButton[_numFloors];
        _lamps          = new ElevatorLamp[_numFloors];
        
        for (int i=0; i<_numFloors; i++) {
            // Connects the lamps to the corresponding button
            _lamps[i]   = new ElevatorLamp();
            _buttons[i] = new ElevatorButton(_lamps[i]);
        }
    }
    
    @Override
    public void run() {
        while(true) {
            byte[] message = _messageIncoming.getWhenNotEmpty();
            
            switch(MessageType.fromOrdinal(message[0])) {
            case ELEVATOR_ACTION_RESPONSE:
                handleActionResponse(new ElevatorActionResponse(message));
                break;
            case ELEVATOR_CONTINUE_RESPONSE:
                handleContinueResponse(new ElevatorContinueResponse(message));
                break;
            case SCHEDULER_DESTINATION_REQUEST:
                handleSchedulerDestinationRequest(new SchedulerDestinationRequest(message));
                break;
            default:
                break;
            }
        }
        
    }
    
    /** Gets an outgoing message from the elevator. If there's no message, it returns null. */
    public byte[] getMessage() {
        return _messageOutgoing.get();
    }
    
    /** Delivers an incoming message to the elevator. */
    public synchronized void putMessage(byte[] message) {
        this.notifyAll();
        _messageIncoming.put(message);
    }
    
    // Puts a new message into the outgoing box and lets the observer know about it
    private void putOutgoingMessage(byte[] message) {
        _messageOutgoing.putWhenEmpty(message);
        synchronized(_observer) {
            _observer.notifyAll();
        }
    }
    
    // Handles a ElevatorActionResponse message object
    private void handleActionResponse(ElevatorActionResponse actionResponse) {
        _motor.setMotorState(actionResponse.action());
        if (_motor.motorState() != ElevatorMotor.MotorState.STATIONARY) {
            try {
                Thread.sleep(FLOOR_MOVEMENT_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Emulating an elevator sensor. Request whether to stop or continue
            putOutgoingMessage(new ElevatorContinueRequest(_carID).toBytes());

        }
    }
    
    private void handleContinueResponse(ElevatorContinueResponse continueResponse) {
        // Checks if the car needs to stop or not
        if (continueResponse.response() != -1) {
            try {
                Thread.sleep(FLOOR_MOVEMENT_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Emulating an elevator sensor. Request whether to stop or continue
            putOutgoingMessage(new ElevatorContinueRequest(_carID).toBytes());

        } else {
            // Stop elevator
            _motor.setMotorState(ElevatorMotor.MotorState.STATIONARY);
            // Turn off the lamp for this floor
            _lamps[continueResponse.response()].turnOFF();
            
            // Open doors
            try {
                Thread.sleep(DOOR_MOVEMENT_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            _door.openDoor();
        }
    }
    
    private void handleSchedulerDestinationRequest(SchedulerDestinationRequest destinationRequest) {
        // Sends out each floor as a separate button push event
        for(int i=0; i<destinationRequest.destinationFloorCount(); i++) {
            putOutgoingMessage(new ElevatorButtonPushEvent(_carID, destinationRequest.destinationFloor(i)).toBytes());
        }
        
        // Close doors
        try {
            Thread.sleep(DOOR_MOVEMENT_TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        _door.closeDoor();
        
        // Requests for a new action
        putOutgoingMessage(new ElevatorActionRequest(_carID).toBytes());
    }
}
