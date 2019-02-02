package main;

import java.net.SocketException;
import java.net.UnknownHostException;

import network.*;
import network.socket.ServerSocket;
import utils.WorkerThread;

public class Scheduler {
    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private ServerSocket    _floorSocket;
    private ServerSocket    _elevatorSocket;

    private int             _numberOfFloors;

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
        _floorSocket    = new ServerSocket(PORT_FLOOR);
        _elevatorSocket = new ServerSocket(PORT_ELEVATOR);
        _numberOfFloors = 0;
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
        System.out.println("Scheduler System Started...");

        if (!setupSockets()) {
            throw new RuntimeException("Error setting up sockets; Aborting");
        }

        System.out.println("Floor and Elevator sockets setup");

        // Wait for the floor to send how many floors there are, and then reply
        _floorSocket.waitForMessage();
        _numberOfFloors = _floorSocket.getMessage()[0];
        System.out.print("Number of floors: ");
        System.out.println(_numberOfFloors);


        while(_floorSocket.isConnected() && _elevatorSocket.isConnected()) {
            // Wait for message from floor socket and send it off to the elevator
            _floorSocket.waitForMessage();
            _elevatorSocket.sendMessage(_floorSocket.getMessage());

            // Wait for message from elevator socket and send it off to the floor
            _elevatorSocket.waitForMessage();
            _floorSocket.sendMessage(_elevatorSocket.getMessage());
        }

    }

    public static void main(String[] args) throws SocketException, UnknownHostException  {
        new Scheduler().run();
    }

}
