package main;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
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

    private ServerSocket            _floorSocket;
    private ServerSocket            _elevatorSocket;

    private int                     _numberOfFloors;
    private int                     _numberOfElevators;

    private List<FloorInputEntry>   _floorEntries;

    private ElevatorSchedule[]      _elevatorSchedule;


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

        _elevatorSchedule = new ElevatorSchedule[_numberOfElevators];

        for (int i=0; i<_numberOfElevators; i++) {
            _elevatorSchedule[i] = new ElevatorSchedule();
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

                System.out.println("/////////////////////////////////////////////");
                System.out.println("/////////////////////////////////////////////");
                for (int i=0; i<_numberOfElevators; i++) {
                    System.out.println(String.format("Elevator %d position:             %d", i, _elevatorSchedule[i].currentFloor()));
                    System.out.println(String.format("Elevator %d target:               %s", i, (_elevatorSchedule[i].currentTarget().target() == -1) ? "(none)" : _elevatorSchedule[i].currentTarget().target()));
                    System.out.println(String.format("Elevator %d motor:                %s", i, _elevatorSchedule[i].currentDirection()));
                    System.out.println(String.format("Elevator %d next button presses:  %s", i, Arrays.toString(_elevatorSchedule[i].currentTarget().buttonPresses().toArray(null))));
                    System.out.println("---------------------------");
                }

                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void handleFloorInputEntry(FloorInputEntry newEntry) {
        int leastCost       = -1;
        int bestElevator    = -1;

        // calculate the cost each elevator would take to meet a request. cost == -1 means it rejected the request.
        for(int i=0; i<_numberOfElevators; i++) {
            int cost = _elevatorSchedule[i].cost(newEntry);
            if (cost != -1 && (bestElevator == -1 || cost < leastCost)) {
                leastCost       = cost;
                bestElevator    = i;
            }
        }

        if (bestElevator == -1) {
            // If we got down here, then all elevators rejected the request. Place into the queue.
            System.out.println("> Floor request was rejected by all elevators; Placing into queue");
            _floorEntries.add(newEntry);

        } else {
            System.out.println(String.format(" > Floor request was accepted by elevator %d at a cost of %d", bestElevator, leastCost));

            // Only send a message if the elevator is currently idle, because it's waiting for action. Otherwise wait for the elevator to ask for work
            if (_elevatorSchedule[bestElevator].currentDirection() == ElevatorMotor.MotorState.STATIONARY) {
                System.out.println(String.format(" > Sending new ElevatorActionResponse to elevator %d", bestElevator));
                _elevatorSocket.sendMessage(new ElevatorActionResponse(bestElevator, true, ElevatorMotor.MotorState.STATIONARY).toBytes());
            }

            // Finally modify our internal tracking
            _elevatorSchedule[bestElevator].addFloorEntry(newEntry);
        }
    }

    private void handleElevatorActionRequest(ElevatorActionRequest request) {
        int id = request.carID();

        // Poll what the current schedule says we should do
        switch(_elevatorSchedule[id].currentDirection()) {
        case UP:
            System.out.println(String.format(" > Sending new ElevatorActionResponse to elevator %d to go UP", id));
            _elevatorSocket.sendMessage(new ElevatorActionResponse(id, true, ElevatorMotor.MotorState.UP).toBytes());
            break;
        case DOWN:
            System.out.println(String.format(" > Sending new ElevatorActionResponse to elevator %d to go DOWN", id));
            _elevatorSocket.sendMessage(new ElevatorActionResponse(id, true, ElevatorMotor.MotorState.DOWN).toBytes());
            break;
        case STATIONARY:
            System.out.println(String.format(" > Sending new ElevatorActionResponse to elevator %d to disengage", id));
            _elevatorSocket.sendMessage(new ElevatorActionResponse(id, false, ElevatorMotor.MotorState.STATIONARY).toBytes());
            break;
        }
    }

    private void handleElevatorContinueRequest(ElevatorContinueRequest request) {
        int id = request.carID();

        // If motor is engaged, change floor
        if (request.actionTaken() != ElevatorMotor.MotorState.STATIONARY) {
            _elevatorSchedule[id].moveToNextFloor();
        }


        if (_elevatorSchedule[id].atTargetFloor()) {
            // Elevator met its target, so it needs to stop
            List<Integer> target = _elevatorSchedule[id].updateCurrentTarget();

            System.out.println(String.format(" > Sending new ElevatorContinueResponse to elevator %d for it to stop", id));
            _elevatorSocket.sendMessage(new ElevatorContinueResponse(id, _elevatorSchedule[id].currentFloor()).toBytes());

            SchedulerDestinationRequest newRequest = new SchedulerDestinationRequest(id);
            for (int button: target) {
                // Add all the requested buttons to the request
                newRequest.addFloor(button);
            }            

            System.out.println(String.format(" > Sending new SchedulerDestinationRequest to elevator %d with following floors: %s", id, newRequest.destinationsAsArray()));
            _elevatorSocket.sendMessage(newRequest.toBytes());

        } else {
            // Elevator has not met its target, so it should continue.
            System.out.println(String.format(" > Sending new ElevatorContinueResponse to elevator %d for it to continue", id));
            _elevatorSocket.sendMessage(new ElevatorContinueResponse(id, -1).toBytes());

        }
    }

    private void handleElevatorButtonPushEvent(ElevatorButtonPushEvent event) {
        int id = event.carID();
        _elevatorSchedule[id].addButtonPress(event.floorNumber());
    }

    public static void main(String[] args) throws SocketException, UnknownHostException  {
        new Scheduler().run();
    }

}
