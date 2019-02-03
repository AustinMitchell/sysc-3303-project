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
    private int             _numberOfElevators;

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
        _floorSocket    = new ServerSocket(this, PORT_FLOOR);
        _elevatorSocket = new ServerSocket(this, PORT_ELEVATOR);
        _numberOfFloors = 0;
        _numberOfElevators = 0;
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


        synchronized(this) {
            byte[] message;
            while(_floorSocket.isConnected() && _elevatorSocket.isConnected()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                message = _floorSocket.getMessage();
                if (message != null) {
                    _elevatorSocket.sendMessage(message);
                }
                
                message = _elevatorSocket.getMessage();
                if (message != null) {
                    _floorSocket.sendMessage(message);
                }    
            }
        }

    }

    public static void main(String[] args) throws SocketException, UnknownHostException  {
        new Scheduler().run();
    }

}
