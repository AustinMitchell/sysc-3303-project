package main;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import gui.ElevatorGUI;
import network.socket.ClientSocket;
import simple.run.SimpleGUIApp;
import utils.ResLoader;
import utils.message.ErrorInputEntry;
import utils.message.FloorInputEntry;
import utils.message.InputEntry;
import utils.message.Message;

public class Floor {

    private static PrintStream LOG;

    public static void setLogSTDOut() { LOG = new PrintStream(new FileOutputStream(FileDescriptor.out)); }

    public static void setLogFile() throws FileNotFoundException { LOG = new PrintStream(new FileOutputStream("bin/Floor.log")); }

    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private ClientSocket _schedulerSocket;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */


    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */

    public static final String INPUT_FILE_PATH  = "floor_input.in";
    public static final int    NUMBER_OF_FLOORS = 22;
    public static final String SCHEDULER_IP     = "localhost";

    /* ============================= */
    /* ========== SETTERS ========== */

    /* ============================= */
    /* ========== GETTERS ========== */

    /* ================================== */
    /* ========== CONSTRUCTORS ========== */

    /** Main constructor for the Floor class. Takes care of setting up the socket connection with the scheduler
     * @throws UnknownHostException
     * @throws SocketException */
    public Floor() throws UnknownHostException, SocketException { _schedulerSocket = new ClientSocket(this, SCHEDULER_IP, Scheduler.PORT_FLOOR); }

    /* ============================= */
    /* ========== METHODS ========== */

    /** The main execution loop for the Floor class
     * @throws IOException */
    public void run() throws IOException {
        // Establish a connection with the Scheduler
        if (!_schedulerSocket.runSetupAndStartThreads()) {
            throw new RuntimeException("Something went wrong setting up socket; Aborting");
        }

        // Send the number of floors there are to the Scheduler
        _schedulerSocket.sendMessage(new byte[] { NUMBER_OF_FLOORS });

        int numElevators = _schedulerSocket.getMessageWhenNotEmpty()[0];

        // Read the input file and establish a stack of entries
        List<InputEntry> entryList = new ArrayList<>();
        BufferedReader   inputFile = new BufferedReader(new InputStreamReader(ResLoader.load(INPUT_FILE_PATH)));

        SimpleGUIApp.start(new ElevatorGUI(NUMBER_OF_FLOORS, numElevators), "Elevator GUI");

        for (String line = inputFile.readLine(); line != null; line = inputFile.readLine()) {

            switch (line.charAt(0)) {
                case '0':
                    entryList.add(new FloorInputEntry(line));
                    break;
                case '1':
                    entryList.add(new ErrorInputEntry(line));
                    break;

                default:
                    break;
            }

        }

        // Sort the list of entries to ensure that the times are in order and log it
        Collections.sort(entryList);
        printList(entryList);

        // Send out the entries to the Scheduler, simulating the time, starting with the first entry
        if (entryList.size() == 1) {
            sendEntryToScheduler(entryList.get(0));
        } else {
            for (int i = 0; i < (entryList.size() - 1); i++) {
                InputEntry currentEntry = entryList.get(i);
                InputEntry nextEntry    = entryList.get(i + 1);

                int delay = nextEntry.differenceInMilliseconds(currentEntry);

                sendEntryToScheduler(currentEntry);
                try {
                    TimeUnit.MILLISECONDS.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            sendEntryToScheduler(entryList.get(entryList.size() - 1));
        }
    }

    /** Helper function used to print out the list of entries
     * @param list */
    private void printList(List<InputEntry> list) {
        LOG.println("===== Floor entry list:");

        for (int i = 0; i < list.size(); i++) {
            LOG.println(list.get(i).toString());
        }

        LOG.println("");
    }

    /** Sends a FloorInputEntry to the scheduler
     * @param entry */
    private void sendEntryToScheduler(InputEntry entry) {
        LOG.println();
        LOG.println(String.format("Sending out new entry to Scheduler: %s", entry));

        byte[] bytes = entry.toBytes();
        LOG.println(String.format("Raw message: %s", Message.bytesToString(bytes)));

        _schedulerSocket.sendMessage(bytes);
    }

    /** Launches the main loop for the Floor object
     * @param  args
     * @throws IOException */
    public static void main(String[] args) throws IOException {
        setLogSTDOut();
        new Floor().run();
    }
}

