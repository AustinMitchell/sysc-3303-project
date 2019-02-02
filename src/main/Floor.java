package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import network.socket.ClientSocket;
import utils.ResLoader;
import utils.message.FloorInputEntry;

public class Floor {
    public static final String INPUT_FILE_PATH = "floor_input.in";

    private ClientSocket _schedulerSocket;

    public Floor() throws UnknownHostException, SocketException{
        _schedulerSocket = new ClientSocket("localhost", Scheduler.PORT_FLOOR);
    }

    public void run() throws IOException {
        // Establish a connection with the Scheduler
        if (!_schedulerSocket.runSetupAndStartThreads()) {
            throw new RuntimeException("Something went wrong setting up socket; Aborting");
        }

        List<FloorInputEntry> entryList = new ArrayList<FloorInputEntry>();

        // Read the input file and establish a stack of entries
        BufferedReader inputFile = new BufferedReader(new InputStreamReader(ResLoader.load(INPUT_FILE_PATH)));
        for (String line = inputFile.readLine(); line != null; line = inputFile.readLine()) {
            FloorInputEntry newEntry = new FloorInputEntry(line);
            entryList.add(newEntry);
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
                FloorInputEntry currentEntry = entryList.get(i);
                FloorInputEntry nextEntry = entryList.get(i + 1);

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
    private void printList(List<FloorInputEntry> list) {
        System.out.println("===== Floor entry list:");

        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).toString());
        }

        System.out.println("");
    }

    private void sendEntryToScheduler(FloorInputEntry entry) {
        System.out.println(String.format("Sending out new entry to Scheduler: %s", entry));

        byte[] bytes = entry.toBytes();
        System.out.println(String.format("Entry as bytes: %s", Arrays.toString(bytes)));

        _schedulerSocket.sendMessage(bytes);
        //_schedulerSocket.waitForMessage();

        //FloorInputEntry returnMessage = new FloorInputEntry(_schedulerSocket.getMessage());
        //System.out.println(String.format("Recieved message from Scheduler: %s", returnMessage));
    }

    public static void main(String[] args) throws IOException {
        new Floor().run();
    }
}

