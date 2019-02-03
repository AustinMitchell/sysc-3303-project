package main;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.EnumMap;

import main.elevator.ElevatorMotor;
import network.socket.ServerSocket;
import utils.WorkerThread;
import utils.message.Direction;
import utils.message.ElevatorActionRequest;
import utils.message.ElevatorActionResponse;
import utils.message.ElevatorButtonPushEvent;
import utils.message.ElevatorContinueRequest;
import utils.message.ElevatorContinueResponse;
import utils.message.FloorInputEntry;
import utils.message.MessageType;
import utils.message.SchedulerDestinationRequest;

public class Scheduler {
    @SuppressWarnings("serial")
    private static final Map<Direction, Comparator<Integer>> COMPARATOR = new EnumMap<Direction, Comparator<Integer>>(Direction.class) {{
        put(Direction.UP, new Comparator<Integer>() {
            @Override
            public int compare(Integer i1, Integer i2) {
                return i1 - i2;
            }
        });
        put(Direction.DOWN, new Comparator<Integer>() {
            @Override
            public int compare(Integer i1, Integer i2) {
                return i2 - i1;
            }
        });
    }};

    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private ServerSocket                _floorSocket;
    private ServerSocket                _elevatorSocket;

    private int                         _numberOfFloors;
    private int                         _numberOfElevators;

    private List<FloorInputEntry>       _floorEntries;

    private boolean[]                   _elevatorIdle;
    private int[]                       _currentFloor;
    //private TreeSet<Integer>[]          _targetFloor;
    private int[]                       _targetFloor;
    private FloorInputEntry[]           _targetFloorEntry;
    private ElevatorMotor.MotorState[]  _currentMotorState;


    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */



    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */

    public static final int PORT_FLOOR          = 5000;
    public static final int PORT_ELEVATOR       = 5001;

    /* ============================= */
    /* ========== SETTERS ========== */



    /* ============================= */
    /* ========== GETTERS ========== */



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

    @SuppressWarnings("unchecked")
    public void run() {
        System.out.println("Scheduler System Started...");

        if (!setupSockets()) {
            throw new RuntimeException("Error setting up sockets; Aborting");
        }

        System.out.println("Floor and Elevator sockets setup");

        // Wait for the elevator to send how many elevators there are
        _numberOfElevators = _elevatorSocket.getMessageWhenNotEmpty()[0];
        System.out.print("Number of elevators: ");
        System.out.println(_numberOfElevators);

        // Wait for the floor to send how many floors there are
        _numberOfFloors = _floorSocket.getMessageWhenNotEmpty()[0];
        System.out.print("Number of floors: ");
        System.out.println(_numberOfFloors);

        // Send the number of floors to the elevator so it can instantiate properly
        System.out.println("Sending number of floors to elevator");
        _elevatorSocket.sendMessage(new byte[] {(byte)_numberOfFloors});

        _elevatorIdle       = new boolean[_numberOfElevators];
        _currentFloor       = new int[_numberOfElevators];
        //_targetFloor        = (TreeSet<Integer>[])new TreeSet[_numberOfElevators];
        _targetFloor        = new int[_numberOfElevators];
        _targetFloorEntry   = new FloorInputEntry[_numberOfElevators];
        _currentMotorState  = new ElevatorMotor.MotorState[_numberOfElevators];

        for (int i=0; i<_numberOfElevators; i++) {
            _elevatorIdle[i]        = true;
            _currentFloor[i]        = 0;
            _targetFloor[i]         = -1;
            _targetFloorEntry[i]    = null;
            _currentMotorState[i]   = ElevatorMotor.MotorState.STATIONARY;
        }

        synchronized(this) {
            byte[] message;
            while(_floorSocket.isConnected() && _elevatorSocket.isConnected()) {

                while (_floorSocket.hasMessage()) {
                    message = _floorSocket.getMessage();
                    if (message != null) {
                        switch(MessageType.fromOrdinal(message[0])) {
                        case FLOOR_INPUT_ENTRY:
                            System.out.println("    Recieved new FloorInputEntry");
                            handleFloorInputEntry(new FloorInputEntry(message));
                            break;
                        default:
                            break;
                        }
                    }
                }

                while (_elevatorSocket.hasMessage()) {
                    message = _elevatorSocket.getMessage();
                    if (message != null) {
                        switch(MessageType.fromOrdinal(message[0])) {
                        case ELEVATOR_ACTION_REQUEST:
                            System.out.println("    Recieved new ElevatorActionRequest");
                            handleElevatorActionRequest(new ElevatorActionRequest(message));
                            break;
                        case ELEVATOR_CONTINUE_REQUEST:
                            System.out.println("    Recieved new ElevatorContinueRequest");
                            handleElevatorContinueRequest(new ElevatorContinueRequest(message));
                            break;
                        case ELEVATOR_BUTTON_PUSH_EVENT:
                            System.out.println("    Recieved new ElevatorButtonPushEvent");
                            handleElevatorButtonPushEvent(new ElevatorButtonPushEvent(message));
                            break;
                        default:
                            break;
                        }
                    }    
                }


                System.out.println(String.format("Elevator 0 position:      %d", _currentFloor[0]));
                System.out.println(String.format("Elevator 0 target:        %s", (_targetFloor[0] == -1) ? "(none)" : _targetFloor[0]));
                System.out.println(String.format("Elevator 0 motor:         %s", _currentMotorState[0]));
                System.out.println(String.format("Elevator 0 floor entry:   %s", _targetFloorEntry[0]));
                System.out.println("---------------------------");

                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void handleFloorInputEntry(FloorInputEntry newEntry) {
        for(int i=0; i<_numberOfElevators; i++) {
            if (_elevatorIdle[i]) {
                // Set up next elevator destination
                System.out.println("> Assigning new entry from floor");
                assignNewEntry(i, newEntry);
                // Send a message to startup the elevator
                System.out.println("    Sending new ElevatorActionResponse");
                _elevatorSocket.sendMessage(new ElevatorActionResponse(i, true, _currentMotorState[i]).toBytes());
                return;
            }
        }

        // If we got down here, then no elevators were free. Just stick it in the queue.
        System.out.println("> Putting input into the queue");
        _floorEntries.add(newEntry);
    }

    private void handleElevatorActionRequest(ElevatorActionRequest request) {
        int id = request.carID();

        if (_targetFloor[id] != -1) {
            System.out.println("> Going to destination");
            _targetFloorEntry[id]   = null;
            _elevatorIdle[id]       = false;
            _targetFloorEntry[id]   = null;
            _currentMotorState[id]  = (_targetFloor[id] > _currentFloor[id]) ? ElevatorMotor.MotorState.UP : ElevatorMotor.MotorState.DOWN;
        } else if (_floorEntries.isEmpty()) {
            // Disengage elevator
            System.out.println("> Disengaging elevator");
            _elevatorIdle[id]       = true;
            _targetFloorEntry[id]   = null;
            _currentMotorState[id]  = ElevatorMotor.MotorState.STATIONARY;
        } else {
            // Set up next elevator destination
            System.out.println("> Assigning new entry from queue");
            FloorInputEntry newEntry =  _floorEntries.remove(0);
            assignNewEntry(id, newEntry);
        }

        // Send a response to the elevator with direction it will need to go
        System.out.println("    Sending new ElevatorActionResponse");
        _elevatorSocket.sendMessage(new ElevatorActionResponse(id, !_elevatorIdle[id], _currentMotorState[id]).toBytes());
    }

    private void handleElevatorContinueRequest(ElevatorContinueRequest request) {
        int id = request.carID();
        int response;

        _currentMotorState[id] = request.actionTaken();

        // If motor is engaged, change floor
        if (_currentMotorState[id] == ElevatorMotor.MotorState.UP) {
            _currentFloor[id] += 1;
        } else if (_currentMotorState[id] == ElevatorMotor.MotorState.DOWN) {
            _currentFloor[id] -= 1;
        }

        if (_currentFloor[id] == _targetFloor[id]) {
            _targetFloor[id] = -1;
            response = _currentFloor[id];
        } else {
            response = -1;
        }

        // Send the response 
        System.out.println("    Sending new ElevatorContinueResponse");
        _elevatorSocket.sendMessage(new ElevatorContinueResponse(id, response).toBytes());

        // If response is not continue, then also send along the user's destination
        if (response != -1) {
            SchedulerDestinationRequest newRequest = new SchedulerDestinationRequest(id);
            if (_targetFloorEntry[id] != null) {
                // If we have an entry, add a destination
                System.out.println("> Sending over button input");
                newRequest.addFloor(_targetFloorEntry[id].destination());
            } 
            System.out.println("    Sending new SchedulerDestinationRequest");
            _elevatorSocket.sendMessage(newRequest.toBytes());
        }
    }

    private void handleElevatorButtonPushEvent(ElevatorButtonPushEvent event) {
        int id = event.carID();

        _targetFloor[id]        = _targetFloorEntry[id].destination();
        _targetFloorEntry[id]   = null;
    }
    
    private void assignNewEntry(int carID, FloorInputEntry entry) {
        _targetFloor[carID]         = entry.floor();
        _elevatorIdle[carID]        = false;
        _targetFloorEntry[carID]    = entry;
        if (entry.floor() > _currentFloor[carID]) {
            _currentMotorState[carID] = ElevatorMotor.MotorState.UP;
        } else if (entry.floor() < _currentFloor[carID]) {
            _currentMotorState[carID] = ElevatorMotor.MotorState.DOWN;
        } else {
            _currentMotorState[carID] = ElevatorMotor.MotorState.STATIONARY;
        }
    }

    public static void main(String[] args) throws SocketException, UnknownHostException  {
        new Scheduler().run();
    }

}
