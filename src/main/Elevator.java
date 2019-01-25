package main;

import java.io.*;
import java.net.*;

public class Elevator {

    /* ========== PRIVATE VARIABLES ========== */
    private InetAddress schedulerIP;

    private DatagramPacket schedulerReceivePacket;

    private DatagramSocket schedulerReceiveSocket;

    /* ========== PUBLIC VARIABLES ========== */

    /* ========== PRIVATE METHODS ========== */

    private void initializeSchedulerScoket() {
        try {
            schedulerReceiveSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /* ========== PUBLIC METHODS ========== */

    /**
     * Constructor that uses localhost as the Scheduler IP
     *
     */
    public Elevator() {
        // Initialize the scheduler IP address to the local host
        try {
            schedulerIP = InetAddress.getLocalHost();
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
            schedulerIP = InetAddress.getByName(IPAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Initialize the Scheduler socket
        this.initializeSchedulerScoket();
    }

    /**
     * The main running loop for Elevator
     *
     */
    public void loop() {
        System.out.println("Elevator System Started...");
        System.out.println("Waiting for event from Scheduler at address: " + schedulerIP.getHostName());
    }

    /**
     * Starts the main loop
     *
     * @param args
     */
    public static void main(String[] args) {

        Elevator elevator;

        if (args.length == 0) {
            elevator = new Elevator();
        }
        else {
            elevator = new Elevator(args[0]);
        }

        elevator.loop();
    }

}
