package main;

import java.io.*;
import java.net.*;
import java.util.Arrays;

import network.socket.ClientSocket;
import utils.*;
import utils.message.FloorInputEntry;

public class ElevatorManager {

    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private ClientSocket    _schedulerSocket;

    private InetAddress     _schedulerIP;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */


    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */

    public static final byte[] NUMBER_OF_ELEVATORS = { 1 };

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
            _schedulerSocket = new ClientSocket(_schedulerIP, Scheduler.PORT_ELEVATOR);
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Helper function to log the data of a packet
     *
     * @param packet
     */
    private void logPacket(DatagramPacket packet) {
        System.out.println("===== Elevator: Packet Data =====");
        System.out.println("To host:          " + packet.getAddress());
        System.out.println("Destination port: " + packet.getPort());
        System.out.println("Length:           " + packet.getLength());
        System.out.println("Contains:         " + new String(packet.getData(), 0, packet.getLength()));
        System.out.print("Byte array:       ");
        for (int j = 0; j < packet.getLength(); j++) {
            System.out.print(packet.getData()[j] + " ");
        }

        System.out.println("");
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
        _schedulerSocket.sendMessage(NUMBER_OF_ELEVATORS);

        // Start the main loop
        while (true) {
            // Wait for message from floor socket and send it off to the elevator
            _schedulerSocket.waitForMessage();
            byte[] message = _schedulerSocket.getMessage();
            System.out.println(String.format("Entry as bytes: %s", Arrays.toString(message)));

            FloorInputEntry returnMessage = new FloorInputEntry(message);
            System.out.println(String.format("Recieved message from Scheduler: %s", returnMessage));

            _schedulerSocket.sendMessage(message);

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
