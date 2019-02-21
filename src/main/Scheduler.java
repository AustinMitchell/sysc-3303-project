package main;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import main.elevator.ElevatorMotor;
import network.socket.ServerSocket;
import utils.WorkerThread;
import utils.message.ElevatorActionRequest;
import utils.message.ElevatorActionResponse;
import utils.message.ElevatorButtonPushEvent;
import utils.message.ElevatorContinueRequest;
import utils.message.ElevatorContinueResponse;
import utils.message.FloorInputEntry;
import utils.message.MessageType;
import utils.message.SchedulerDestinationRequest;

public class Scheduler {
    public static Scheduler generateTestScheduler(int numFloors, int numElevators) {
        Scheduler scheduler;
        try {
            scheduler = new Scheduler();
            scheduler._numberOfFloors       = numFloors;
            scheduler._numberOfElevators    = numElevators;
            scheduler._floorEntries         = new LinkedList<>();
            scheduler._elevatorSchedules    = new ElevatorSchedule[numElevators];
            for (int i=0; i<numElevators; i++) {
                scheduler._elevatorSchedules[i] = new ElevatorSchedule();
            }

        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();            
            return null;
        }

        return scheduler;
    }

    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private ServerSocket            _floorSocket;
    private ServerSocket            _elevatorSocket;

    private int                     _numberOfFloors;
    private int                     _numberOfElevators;

    private List<FloorInputEntry>   _floorEntries;

    private ElevatorSchedule[]      _elevatorSchedules;
    
    private boolean                 _loggingEnabled;


    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */



    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */

    public static final int PORT_FLOOR          = 5000;
    public static final int PORT_ELEVATOR       = 5001;

    /* ============================= */
    /* ========== SETTERS ========== */

    public void disableLogging() { _loggingEnabled = false; }

    /* ============================= */
    /* ========== GETTERS ========== */

    public ElevatorSchedule[] elevatorSchedules() { return _elevatorSchedules; }

    /* ================================== */
    /* ========== CONSTRUCTORS ========== */

    /**
     * Scheduler constructor
     * @throws UnknownHostException
     * @throws SocketException
     */
    public Scheduler() throws SocketException, UnknownHostException {
        _floorSocket        = new ServerSocket(this, PORT_FLOOR);
        _elevatorSocket     = new ServerSocket(this, PORT_ELEVATOR);

        _numberOfFloors     = 0;
        _numberOfElevators  = 0;

        _floorEntries       = new LinkedList<>();
        
        _loggingEnabled     = true;
    }

    /* ============================= */
    /* ========== METHODS ========== */

    /**
     * Sets up both server sockets. They wait for a connection to be established from a client socket and set up the send and receive ports.
     * @return boolean Returns the success of both
     */
    private boolean setupSockets() {
        WorkerThread<Boolean, Void> floorConnect    = _floorSocket.generateSetupWorkerThread();
        WorkerThread<Boolean, Void> elevatorConnect = _elevatorSocket.generateSetupWorkerThread();


        floorConnect.run();
        elevatorConnect.run();

        while(!floorConnect.jobIsFinished() || !elevatorConnect.jobIsFinished()) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return floorConnect.result() && elevatorConnect.result();
    }

    public void run() {
        log("Scheduler System Started...");

        if (!setupSockets()) {
            throw new RuntimeException("Error setting up sockets; Aborting");
        }

        log("Floor and Elevator sockets setup");

        // Wait for the elevator to send how many elevators there are
        _numberOfElevators = _elevatorSocket.getMessageWhenNotEmpty()[0];
        log("Number of elevators: %d", _numberOfElevators);

        // Wait for the floor to send how many floors there are
        _numberOfFloors = _floorSocket.getMessageWhenNotEmpty()[0];
        log("Number of floors: %d", _numberOfFloors);

        // Send the number of floors to the elevator so it can instantiate properly
        log("Sending number of floors to elevator");
        _elevatorSocket.sendMessage(new byte[] {(byte)_numberOfFloors});

        _elevatorSchedules = new ElevatorSchedule[_numberOfElevators];

        for (int i=0; i<_numberOfElevators; i++) {
            _elevatorSchedules[i] = new ElevatorSchedule();
        }

        synchronized(this) {
            byte[] message;
            while(_floorSocket.isConnected() && _elevatorSocket.isConnected()) {


                while (_floorSocket.hasMessage()) {
                    message = _floorSocket.getMessage();
                    handleMessage(message);
                }

                while (_elevatorSocket.hasMessage()) {
                    message = _elevatorSocket.getMessage();
                    handleMessage(message);
                }

                printElevatorStates();

                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    
    private void sendMessageToElevator(byte[] message) {
        log(" > Sending message: %s", Arrays.toString(message));
        _elevatorSocket.sendMessage(message);
    }

    private void log(String message, Object... formatArgs) {
        if (_loggingEnabled) {
            System.out.println(String.format(message, formatArgs));
        }
    }
    
    private void handleMessage(byte[] message) {
        if (message == null) {
            return;
        }
        
        log(" > Message bytes: %s", Arrays.toString(message));

        switch(MessageType.fromOrdinal(message[0])) {
        case FLOOR_INPUT_ENTRY:
            log(" > Recieved new FloorInputEntry");
            handleFloorInputEntry(new FloorInputEntry(message));
            break;
        case ELEVATOR_ACTION_REQUEST:
            log(" > Recieved new ElevatorActionRequest from elevator %d", message[1]);
            handleElevatorActionRequest(new ElevatorActionRequest(message));
            break;

        case ELEVATOR_CONTINUE_REQUEST:
            log(" > Recieved new ElevatorContinueRequest from elevator %d", message[1]);
            handleElevatorContinueRequest(new ElevatorContinueRequest(message));
            break;

        case ELEVATOR_BUTTON_PUSH_EVENT:
            log(" > Recieved new ElevatorButtonPushEvent from elevator %d", message[1]);
            handleElevatorButtonPushEvent(new ElevatorButtonPushEvent(message));
            break;

        default:
            break;
        }
    }

    /** Prints the states of all the elevators */
    public void printElevatorStates() {
        System.out.println("---------------------------");
        for (int i=0; i<_numberOfElevators; i++) {
            System.out.println(String.format("Elevator %d position:         %d", i, _elevatorSchedules[i].currentFloor()));
            System.out.println(String.format("Elevator %d target:           %s", i, (_elevatorSchedules[i].currentTarget() == null) ? "(none)" : _elevatorSchedules[i].currentTarget()));
            System.out.println(String.format("Elevator %d motor:            %s", i, _elevatorSchedules[i].currentDirection()));
            System.out.println(String.format("Elevator %d target sequence:  %s", i, _elevatorSchedules[i].targetListAsString()));
            System.out.println("---------------------------");
        }
        
        System.out.println("");
        System.out.println("================================================");
        System.out.println("================================================");
        System.out.println("");
    }

    public void handleFloorInputEntry(FloorInputEntry newEntry) {
        int leastCost       = -1;
        int bestElevator    = -1;

        log(" > New entry data: %s", newEntry);

        // calculate the cost each elevator would take to meet a request. cost == -1 means it rejected the request.
        for(int i=0; i<_numberOfElevators; i++) {
            int cost = _elevatorSchedules[i].cost(newEntry);
            if (cost != -1 && (bestElevator == -1 || cost < leastCost)) {
                leastCost       = cost;
                bestElevator    = i;
            }
        }

        if (bestElevator == -1) {
            // If we got down here, then all elevators rejected the request. Place into the queue.
            log("> Floor request was rejected by all elevators; Placing into queue");
            _floorEntries.add(newEntry);

        } else {
            log(" > Floor request was accepted by elevator %d at a cost of %d", bestElevator, leastCost);

            // Only send a message if the elevator is currently idle, because it's waiting for action. Otherwise wait for the elevator to ask for work
            if (_elevatorSchedules[bestElevator].currentDirection() == ElevatorMotor.MotorState.STATIONARY) {
                _elevatorSchedules[bestElevator].addFloorEntry(newEntry);
                log(" > Sending new ElevatorActionResponse to elevator %d", bestElevator);
                sendMessageToElevator(new ElevatorActionResponse(bestElevator, true, _elevatorSchedules[bestElevator].currentDirection()).toBytes());
            } else {
                _elevatorSchedules[bestElevator].addFloorEntry(newEntry);
            }


        }
    }

    public void handleElevatorActionRequest(ElevatorActionRequest request) {
        int id = request.carID();

        // Poll what the current schedule says we should do
        switch(_elevatorSchedules[id].currentDirection()) {
        case UP:
            log(" > Sending new ElevatorActionResponse to elevator %d to go UP", id);
            sendMessageToElevator(new ElevatorActionResponse(id, true, ElevatorMotor.MotorState.UP).toBytes());
            break;
        case DOWN:
            log(" > Sending new ElevatorActionResponse to elevator %d to go DOWN", id);
            sendMessageToElevator(new ElevatorActionResponse(id, true, ElevatorMotor.MotorState.DOWN).toBytes());
            break;
        case STATIONARY:
            log(" > Sending new ElevatorActionResponse to elevator %d to disengage", id);
            sendMessageToElevator(new ElevatorActionResponse(id, false, ElevatorMotor.MotorState.STATIONARY).toBytes());
            break;
        }
    }

    public void handleElevatorContinueRequest(ElevatorContinueRequest request) {
        int id = request.carID();

        // Set our ability to stop at a floor to true. This will be set to false if we continue past this floor.
        _elevatorSchedules[id].setCanStopCurrentFloor(true);
        
        // If motor is engaged, change floor
        if (request.actionTaken() != ElevatorMotor.MotorState.STATIONARY) {
            _elevatorSchedules[id].moveToNextFloor();
        }


        if (_elevatorSchedules[id].atTargetFloor()) {
            // Elevator met its target, so it needs to stop
            List<Integer> target = _elevatorSchedules[id].updateCurrentTarget();

            log(" > Sending new ElevatorContinueResponse to elevator %d for it to stop", id);
            sendMessageToElevator(new ElevatorContinueResponse(id, _elevatorSchedules[id].currentFloor()).toBytes());

            SchedulerDestinationRequest newRequest = new SchedulerDestinationRequest(id);
            for (int button: target) {
                // Add all the requested buttons to the request
                newRequest.addFloor(button);
            }            

            log(" > Sending new SchedulerDestinationRequest to elevator %d with following floors: %s", id, newRequest.destinationsAsArray());
            sendMessageToElevator(newRequest.toBytes());

        } else {
            // Elevator has not met its target, so it should continue.
            log(" > Sending new ElevatorContinueResponse to elevator %d for it to continue", id);
            _elevatorSchedules[id].setCanStopCurrentFloor(false);
            sendMessageToElevator(new ElevatorContinueResponse(id, -1).toBytes());

        }
    }

    public void handleElevatorButtonPushEvent(ElevatorButtonPushEvent event) {
        int id = event.carID();
        _elevatorSchedules[id].addButtonPress(event.floorNumber());
    }

    public static void main(String[] args) throws SocketException, UnknownHostException  {
        new Scheduler().run();
    }

}
