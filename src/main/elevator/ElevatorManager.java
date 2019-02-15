package main.elevator;

import java.io.*;
import java.net.*;
import java.util.Arrays;

import main.Scheduler;
import network.socket.ClientSocket;

public class ElevatorManager {

    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */
	
    private ClientSocket    _schedulerSocket;

    private InetAddress     _schedulerIP;

    private Elevator[]      _elevators;

    private int             _numFloors;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */


    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */

    public static final int NUMBER_OF_ELEVATORS = 3;

    /* ============================= */
    /* ========== SETTERS ========== */


    /* ============================= */
    /* ========== GETTERS ========== */


    /* ================================== */
    /* ========== CONSTRUCTORS ========== */

    /**
     * Constructor that uses localhost as the Scheduler IP
     *
     */
    public ElevatorManager() {
        // Initialize the scheduler IP address to the local host
        try {
            _schedulerIP = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        _elevators = new Elevator[NUMBER_OF_ELEVATORS];

        // Initialize the scheduler socket
        this.initializeSchedulerSocket();
    }

    /**
     * Constructor that receives the Scheduler IP
     *
     * @param IPAddress
     */
    public ElevatorManager(String IPAddress) {
        // Initialize the scheduler IP to the passed IP address
        try {
            _schedulerIP = InetAddress.getByName(IPAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        _elevators = new Elevator[NUMBER_OF_ELEVATORS];

        // Initialize the Scheduler socket
        this.initializeSchedulerSocket();
    }

    /* ============================= */
    /* ========== METHODS ========== */

    /**
     * Method to set up the scheduler socket
     */
    private void initializeSchedulerSocket() {
        try {
            _schedulerSocket = new ClientSocket(this, _schedulerIP, Scheduler.PORT_ELEVATOR);
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * The main running loop for Elevator
     * @throws IOException
     *
     */
    public void loop() throws IOException {
        System.out.println("Elevator System Started...");

        if (!_schedulerSocket.runSetupAndStartThreads()) {
            throw new RuntimeException("Something went wrong setting up socket; Aborting");
        }

        // Send the number of elevators to the scheduler
        _schedulerSocket.sendMessage(new byte[] {NUMBER_OF_ELEVATORS});

        // Wait for scheduler to send off number of floors
        _numFloors = _schedulerSocket.getMessageWhenNotEmpty()[0];

        for (int i=0; i<NUMBER_OF_ELEVATORS; i++) {
            _elevators[i] = new Elevator(this, _numFloors, i);
            new Thread(_elevators[i]).start();
        }

        // Start the main loop
        synchronized(this) {
            while (true) {
                byte[] message;

                // Checks the socket for new messages to send to an elevator
                while(_schedulerSocket.hasMessage()) {
                    message = _schedulerSocket.getMessage();
                    if (message != null) {
                        System.out.println("--------------------------------------");
                        System.out.println("Recieved new message: " + Arrays.toString(message));
                        System.out.println("Target elevator:      " + message[1]);

                        _elevators[message[1]].putMessage(message);
                    }
                }

                // Checks elevators for messages to send to the socket
                for (Elevator e: _elevators) {
                    message = e.getMessage();
                    if (message != null) {
                        _schedulerSocket.sendMessage(message);
                    }
                }

                try {
                    // Waits to be notified of a new message
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Starts the main loop
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        ElevatorManager elevator;

        // Construct a new Elevator
        if (args.length == 0) {
            elevator = new ElevatorManager();
        }
        else {
            elevator = new ElevatorManager(args[0]);
        }

        elevator.loop();
    }

}
