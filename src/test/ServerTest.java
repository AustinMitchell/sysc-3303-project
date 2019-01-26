package test;

import network.*;

public class ServerTest {
    public static void main(String[] args) throws Exception {
        ServerSocket socket = new ServerSocket(5000);

        System.out.println("Waiting for client...");
        socket.runSetupAndStartThreads();
        System.out.println("Client connected");
        
        while(true) {
            socket.waitForMessage();
            String message = new String(socket.getMessage());
            System.out.println("Received message " + message);
            
            if (message.equals("exit")) {
                break;
            }
        }
        
        socket.close();
    }
}
