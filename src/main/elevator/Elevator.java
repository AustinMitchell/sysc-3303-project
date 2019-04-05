package main.elevator;

import main.elevator.ElevatorMotor.MotorState;
import utils.DataBox;
import utils.DataQueueBox;
import utils.message.ElevatorActionRequest;
import utils.message.ElevatorActionResponse;
import utils.message.ElevatorButtonPushEvent;
import utils.message.ElevatorContinueRequest;
import utils.message.ElevatorContinueResponse;
import utils.message.ElevatorError;
import utils.message.ErrorInputEntry;
import utils.message.Message;
import utils.message.MessageType;
import utils.message.SchedulerDestinationRequest;
import utils.message.SystemFault;

public class Elevator implements Runnable {

    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private DataBox<byte[]>      _messageOutgoing;
    private DataQueueBox<byte[]> _messageIncoming;

    private Object _observer;

    private int    _numFloors;
    private int    _carID;
    private String _report;

    private ElevatorDoor     _door;
    private ElevatorMotor    _motor;
    private ElevatorButton[] _buttons;
    private ElevatorLamp[]   _lamps;

    private boolean _doorStuckOpenError;
    private boolean _doorStuckClosedError;
    private boolean _motorStuckError;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */


    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */

    public static final int FLOOR_MOVEMENT_TIMEOUT = 2000;
    public static final int DOOR_MOVEMENT_TIMEOUT  = 500;
    public static final int DOOR_ERROR_TIMEOUT     = 1000;

    /* ============================= */
    /* ========== SETTERS ========== */



    /* ============================= */
    /* ========== GETTERS ========== */

    /** Return the state of the elevator's motor */
    public ElevatorMotor.MotorState motorState() { return _motor.motorState(); }

    /* ================================== */
    /* ========== CONSTRUCTORS ========== */

    /** Creates a new Elevator
     * @param observer  Object which will be notified when messages come out of the Elevator
     * @param numFloors Number of floors to accommodate
     * @param carID     ID of this car, used for message construction */
    public Elevator(Object observer, int numFloors, int carID) {
        _messageIncoming = new DataQueueBox<>();
        _messageOutgoing = new DataBox<>();

        _observer  = observer;
        _numFloors = numFloors;
        _carID     = carID;

        _door    = new ElevatorDoor();
        _motor   = new ElevatorMotor();
        _buttons = new ElevatorButton[_numFloors];
        _lamps   = new ElevatorLamp[_numFloors];

        _report = "";

        for (int i = 0; i < _numFloors; i++) {
            // Connects the lamps to the corresponding button
            _lamps[i]   = new ElevatorLamp();
            _buttons[i] = new ElevatorButton(_lamps[i]);
        }
    }

    /* ============================= */
    /* ========== METHODS ========== */

    private void appendReport(String s, Object... args) { _report += String.format(s + "\n", args); }

    private void printReport() {
        ElevatorManager.getLog().println(_report);
        _report = "";
    }

    @Override
    public void run() {
        byte[] message;
        while (true) {
            message = _messageIncoming.getWhenNotEmpty();

            appendReport("--------------------------------------");
            appendReport("ELEVATOR %d Recieved new message: %s", _carID, Message.bytesToString(message));

            switch (MessageType.fromOrdinal(message[0])) {
                case ELEVATOR_ACTION_RESPONSE:
                    appendReport("Recieved new ElevatorActionResponse");
                    handleActionResponse(new ElevatorActionResponse(message));
                    break;
                case ELEVATOR_CONTINUE_RESPONSE:
                    appendReport("Recieved new ElevatorContinueResponse");
                    handleContinueResponse(new ElevatorContinueResponse(message));
                    break;
                case SCHEDULER_DESTINATION_REQUEST:
                    appendReport("Recieved new SchedulerDestinationRequest");
                    handleSchedulerDestinationRequest(new SchedulerDestinationRequest(message));
                    break;
                case ERROR_INPUT_ENTRY:
                    appendReport("Recieved new ErrorInputEntry");
                    handleErrorInputEntry(new ErrorInputEntry(message));
                    break;
                default:
                    break;
            }

            appendReport(String.format("Motor state: %s", _motor.motorState()));

            printReport();
        }

    }

    /** Gets an outgoing message from the elevator. If there's no message, it returns null. */
    public byte[] getMessage() { return _messageOutgoing.get(); }

    /** Delivers an incoming message to the elevator. */
    public synchronized void putMessage(byte[] message) {
        this.notifyAll();
        _messageIncoming.put(message);
    }

    // Puts a new message into the outgoing box and lets the observer know about it
    private void putOutgoingMessage(byte[] message) {
        appendReport("Sending raw message: %s", Message.bytesToString(message));
        synchronized (_observer) {
            _messageOutgoing.putWhenEmpty(message);
            _observer.notifyAll();
        }
    }

    // Handles a ElevatorActionResponse message object
    private void handleActionResponse(ElevatorActionResponse actionResponse) {
        appendReport("Setting motor state to %s", actionResponse.action());
        _motor.setMotorState(actionResponse.action());
        if (actionResponse.takeAction()) {
            // Emulating an elevator sensor. Request whether to stop or continue
            appendReport("Sending new ElevatorContinueRequest");
            putOutgoingMessage(new ElevatorContinueRequest(_carID, MotorState.STATIONARY).toBytes());

        }
    }

    private void handleContinueResponse(ElevatorContinueResponse continueResponse) {
        // Checks if the car needs to stop or not
        if (continueResponse.response() == -1) {
            if (this._motorStuckError) {
                appendReport("SYSTEM FAULT DETECTED: ELEVATOR %d motor is stuck", this._carID);
                this._motor.setMotorState(MotorState.BROKEN);
                putOutgoingMessage(new ElevatorError(SystemFault.ELEVATOR_STUCK, this._carID).toBytes());
            } else {
                appendReport("Elevator is set to continue");
                // Simulates the time the elevator would take to move to a new floor
                try {
                    Thread.sleep(FLOOR_MOVEMENT_TIMEOUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Emulating an elevator sensor. Request whether to stop or continue
                appendReport("Sending new ElevatorContinueRequest");
                putOutgoingMessage(new ElevatorContinueRequest(_carID, _motor.motorState()).toBytes());
            }

        } else {
            appendReport("Elevator has been requested to stop");
            // Stop elevator
            _motor.setMotorState(ElevatorMotor.MotorState.STATIONARY);
            // Turn off the lamp for this floor
            _lamps[continueResponse.response()].turnOFF();

            // Check if the door is stuck closed and resolve the error
            if (this._doorStuckClosedError) {
                this._doorStuckClosedError = false;
                appendReport("SYSTEM FAULT DETECTED: ELEVATOR %d doors stuck closed, attempting to resolve fault", this._carID);
                putOutgoingMessage(new ElevatorError(SystemFault.DOOR_STUCK_CLOSED, this._carID).toBytes());
                try {
                    Thread.sleep(DOOR_ERROR_TIMEOUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                appendReport("ELEVATOR %d resolved door stuck fault", this._carID);
            }

            // Open doors
            try {
                Thread.sleep(DOOR_MOVEMENT_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            appendReport("ELEVATOR %d door has opened", this._carID);
            _door.openDoor();
        }
    }

    private void handleSchedulerDestinationRequest(SchedulerDestinationRequest destinationRequest) {
        // Sends out each floor as a separate button push event
        for (int i = 0; i < destinationRequest.destinationFloorCount(); i++) {
            appendReport("Sending button %d push event", destinationRequest.destinationFloor(i));
            putOutgoingMessage(new ElevatorButtonPushEvent(_carID, destinationRequest.destinationFloor(i)).toBytes());
        }

        // Check if the door is stuck open and resolve the error
        if (this._doorStuckOpenError) {
            this._doorStuckOpenError = false;
            appendReport("SYSTEM FAULT DETECTED: ELEVATOR %d doors stuck open, attempting to resolve fault", this._carID);
            putOutgoingMessage(new ElevatorError(SystemFault.DOOR_STUCK_OPEN, this._carID).toBytes());
            try {
                Thread.sleep(DOOR_ERROR_TIMEOUT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            appendReport("ELEVATOR %d resolved door stuck fault", this._carID);
        }

        // Close doors
        try {
            Thread.sleep(DOOR_MOVEMENT_TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        appendReport("ELEVATOR %d door has closed", this._carID);
        _door.closeDoor();

        // Requests for a new action
        appendReport("Sending new ElevatorActionRequest");
        putOutgoingMessage(new ElevatorActionRequest(_carID).toBytes());
    }

    private void handleErrorInputEntry(ErrorInputEntry error) {
        appendReport("Elevator encountered an error: %s", error.faultType());
        switch (error.faultType()) {
            case DOOR_STUCK_CLOSED:
                _doorStuckClosedError = true;
                break;
            case DOOR_STUCK_OPEN:
                _doorStuckOpenError = true;
                break;
            case ELEVATOR_STUCK:
                _motorStuckError = true;
                break;
            default:
                break;
        }
    }
}
