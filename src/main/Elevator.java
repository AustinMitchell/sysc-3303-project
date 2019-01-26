package main;

import java.io.*;
import java.net.*;
import utils.*;

public class Elevator {

    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private InetAddress     _schedulerIP;
    private DatagramPacket  _schedulerReceivePacket;
    private DatagramSocket  _schedulerReceiveSocket;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */


    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */


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
    public Elevator() {
        // Initialize the scheduler IP address to the local host
        try {
            _schedulerIP = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Initialize the scheduler socket
        this.initializeSchedulerScoket();
    }

    /**
     * Constructor that receives the Scheduler IP
     *
     * @param IPAddress
     */
    public Elevator(String IPAddress) {
        // Initialize the scheduler IP to the passed IP address
        try {
            _schedulerIP = InetAddress.getByName(IPAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Initialize the Scheduler socket
        this.initializeSchedulerScoket();
    }

    /* ============================= */
    /* ========== METHODS ========== */

    /**
     * Method to set up the scheduler socket
     */
    private void initializeSchedulerScoket() {
        try {
            _schedulerReceiveSocket = new DatagramSocket(Scheduler.PORT_ELEVATOR);
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
     *
     */
    public void loop() {
        System.out.println("Elevator System Started...");

        // Start the main loop
        while (true) {
            byte scheduleReceiveData[] = new byte[512];

            _schedulerReceivePacket = new DatagramPacket(scheduleReceiveData,
                                                         scheduleReceiveData.length);

            // Elevator waits for a request from the Scheduler
            try {
                _schedulerReceiveSocket.receive(_schedulerReceivePacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            logPacket(_schedulerReceivePacket);

            /* ====================================== */
            /* this will be where the message is read */


        }


    }

    /**
     * Starts the main loop
     *
     * @param args
     */
    public static void main(String[] args) {

        Elevator elevator;

        // Construct a new Elevator
        if (args.length == 0) {
            elevator = new Elevator();
        }
        else {
            elevator = new Elevator(args[0]);
        }

        elevator.loop();
    }

}
