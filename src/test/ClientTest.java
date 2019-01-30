package test;

import java.util.Scanner;

import network.*;

public class ClientTest {
    public static void main(String[] args) throws Exception {
        ClientSocket socket = new ClientSocket("localhost", 3000);

        System.out.println("Establishing connection...");
        socket.runSetupAndStartThreads();
        System.out.println("Connection established. ID set to " + socket.id());
        
        Scanner scanner = new Scanner(System.in);
        while(true) {
            scanner.hasNextLine();
            String message = scanner.nextLine();
            socket.sendMessage(message.getBytes());
            while(!socket.isSendQueueEmpty());
            
            if (message.equals("exit")) {
                break;
            }
        }
        
        scanner.close();
        while(!socket.isSendQueueEmpty());
        socket.close();
    }
}