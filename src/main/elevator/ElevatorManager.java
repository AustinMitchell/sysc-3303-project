package main.elevator;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketException;
import java.net.UnknownHostException;

import main.Scheduler;
import network.socket.ClientSocket;
import utils.message.Message;

public class ElevatorManager {

    private static PrintStream LOG;

    public static PrintStream getLog() { return LOG; }

    public static void setLogSTDOut() { LOG = new PrintStream(new FileOutputStream(FileDescriptor.out)); }

    public static void setLogFile() throws FileNotFoundException { LOG = new PrintStream(new FileOutputStream("bin/ElevatorManager.log")); }

    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private ClientSocket _schedulerSocket;
    private Elevator[]   _elevators;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */


    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */

    public static final int NUMBER_OF_ELEVATORS = 4;

    public static final String SCHEDULER_IP = "localhost";

    /* ============================= */
    /* ========== SETTERS ========== */


    /* ============================= */
    /* ========== GETTERS ========== */


    /* ================================== */
    /* ========== CONSTRUCTORS ========== */

    /** Constructor that uses localhost as the Scheduler IP */
    public ElevatorManager() {
        _elevators = new Elevator[NUMBER_OF_ELEVATORS];

        // Initialize the scheduler socket
        this.initializeSchedulerSocket();
    }

    /** Constructor that receives the Scheduler IP
     * @param IPAddress */
    public ElevatorManager(String IPAddress) {

        _elevators = new Elevator[NUMBER_OF_ELEVATORS];

        // Initialize the Scheduler socket
        this.initializeSchedulerSocket();
    }

    /* ============================= */
    /* ========== METHODS ========== */

    /** Method to set up the scheduler socket */
    private void initializeSchedulerSocket() {
        try {
            _schedulerSocket = new ClientSocket(this, SCHEDULER_IP, Scheduler.PORT_ELEVATOR);
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /** The main running loop for Elevator
     * @throws IOException */
    public void loop() {
        LOG.println("Elevator System Started...");

        if (!_schedulerSocket.runSetupAndStartThreads()) {
            throw new RuntimeException("Something went wrong setting up socket; Aborting");
        }

        // Send the number of elevators to the scheduler
        _schedulerSocket.sendMessage(new byte[] { NUMBER_OF_ELEVATORS });

        // Wait for scheduler to send off number of floors
        int numFloors = _schedulerSocket.getMessageWhenNotEmpty()[0];

        for (int i = 0; i < NUMBER_OF_ELEVATORS; i++) {
            _elevators[i] = new Elevator(this, numFloors, i);
            new Thread(_elevators[i]).start();
        }

        // Start the main loop
        synchronized (this) {
            while (true) {
                // Checks the socket for new messages to send to an elevator
                while (_schedulerSocket.hasMessage()) {
                    Message message = new Message(_schedulerSocket.getMessage());
                    if (message.bytes() != null) {
                        _elevators[message.carID()].putMessage(message.bytes());
                    }
                }

                // Checks elevators for messages to send to the socket
                for (Elevator e: _elevators) {
                    byte[] message = e.getMessage();
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

    /** Starts the main loop
     * @param  args
     * @throws FileNotFoundException
     * @throws IOException */
    public static void main(String[] args) throws FileNotFoundException {

        setLogSTDOut();

        ElevatorManager elevator;

        // Construct a new Elevator
        if (args.length == 0) {
            elevator = new ElevatorManager();
        } else {
            elevator = new ElevatorManager(args[0]);
        }

        elevator.loop();
    }

}
