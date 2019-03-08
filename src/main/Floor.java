package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import network.socket.ClientSocket;
import utils.ResLoader;
import utils.message.ErrorInputEntry;
import utils.message.FloorInputEntry;
import utils.message.InputEntry;
import utils.message.Message;

public class Floor {


    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */

    private ClientSocket _schedulerSocket;

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */


    /* ==================================== */
    /* ========== PUBLIC MEMBERS ========== */

    public static final String  INPUT_FILE_PATH     = "floor_input.in";
    public static final int     NUMBER_OF_FLOORS    = 10;

    /* ============================= */
    /* ========== SETTERS ========== */

    /* ============================= */
    /* ========== GETTERS ========== */

    /* ================================== */
    /* ========== CONSTRUCTORS ========== */

    /**
     * Main constructor for the Floor class. Takes care of setting up the socket
     * connection with the scheduler
     *
     * @throws UnknownHostException
     * @throws SocketException
     */
    public Floor() throws UnknownHostException, SocketException{
        _schedulerSocket = new ClientSocket(this, "localhost", Scheduler.PORT_FLOOR);
    }

    /* ============================= */
    /* ========== METHODS ========== */

    /**
     * The main execution loop for the Floor class
     *
     * @throws IOException
     */
    public void run() throws IOException {
        // Establish a connection with the Scheduler
        if (!_schedulerSocket.runSetupAndStartThreads()) {
            throw new RuntimeException("Something went wrong setting up socket; Aborting");
        }

        // Send the number of floors there are to the Scheduler
        _schedulerSocket.sendMessage(new byte[] {NUMBER_OF_FLOORS});

        // Read the input file and establish a stack of entries
        List<InputEntry> entryList = new ArrayList<>();
        BufferedReader inputFile = new BufferedReader(new InputStreamReader(ResLoader.load(INPUT_FILE_PATH)));

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
        }
        else {
            for (int i = 0; i < entryList.size() - 1; i++) {
                InputEntry currentEntry = entryList.get(i);
                InputEntry nextEntry = entryList.get(i + 1);

                int delay = nextEntry.differenceInMilliseconds(currentEntry);

                sendEntryToScheduler(currentEntry);
                try {
                    TimeUnit.MILLISECONDS.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            sendEntryToScheduler(entryList.get(entryList.size()-1));
        }
    }

    /**
     * Helper function used to print out the list of entries
     *
     * @param list
     */
    private void printList(List<InputEntry> list) {
        System.out.println("===== Floor entry list:");

        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).toString());
        }

        System.out.println("");
    }

    /**
     * Sends a FloorInputEntry to the scheduler
     *
     * @param entry
     */
    private void sendEntryToScheduler(InputEntry entry) {
        System.out.println();
        System.out.println(String.format("Sending out new entry to Scheduler: %s", entry));

        byte[] bytes = entry.toBytes();
        System.out.println(String.format("Raw message: %s", Message.bytesToString(bytes)));

        _schedulerSocket.sendMessage(bytes);
    }

    /**
     * Launches the main loop for the Floor object
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        new Floor().run();
    }
}

