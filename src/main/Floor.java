package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.net.UnknownHostException;

import network.ClientSocket;
import utils.FloorInputEntry;
import utils.ResLoader;

public class Floor {
    public static final String INPUT_FILE_PATH = "floor_input.in";
    
    private ClientSocket _socket;
    
    public Floor() throws UnknownHostException, SocketException{
        _socket = new ClientSocket("localhost", Scheduler.PORT_FLOOR);
    }
    
    public void run() {
        try {
            BufferedReader inputFile = new BufferedReader(new InputStreamReader(ResLoader.load(INPUT_FILE_PATH)));
            for (String line = inputFile.readLine(); line != null; line = inputFile.readLine()) {
                System.out.println(new FloorInputEntry(line));
            }
        } catch (IOException e) {
            System.err.println("Cannot read input file. ");
            e.printStackTrace();
            return;
        }

    }
    
    public static void main(String[] args) throws UnknownHostException, SocketException { new Floor().run(); }
}
