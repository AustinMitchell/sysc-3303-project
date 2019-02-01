package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

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
        if (!_schedulerSocket.runSetupAndStartThreads()) {
            throw new RuntimeException("Something went wrong setting up socket; Aborting");
        }
        
        BufferedReader inputFile = new BufferedReader(new InputStreamReader(ResLoader.load(INPUT_FILE_PATH)));
        for (String line = inputFile.readLine(); line != null; line = inputFile.readLine()) {
            FloorInputEntry newEntry = new FloorInputEntry(line);
            
            System.out.println(String.format("Sending out new entry to Scheduler: %s", newEntry));            
            
            byte[] bytes = newEntry.toBytes();
            System.out.println(String.format("Entry as bytes: %s", Arrays.toString(bytes)));
            
            _schedulerSocket.sendMessage(bytes);
            _schedulerSocket.waitForMessage();
            
            FloorInputEntry returnMessage = new FloorInputEntry(_schedulerSocket.getMessage());
            System.out.println(String.format("Recieved message from Scheduler: %s", returnMessage));
            
        }

    }
    
    public static void main(String[] args) throws IOException {
        new Floor().run();
    }
}
