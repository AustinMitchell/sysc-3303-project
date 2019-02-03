package test;

import network.socket.ServerSocket;

public class ServerTest {
    public static void main(String[] args) throws Exception {
        Object observer = new Object();
        ServerSocket socket = new ServerSocket(observer, 3000);

        System.out.println("Waiting for client...");
        socket.runSetupAndStartThreads();
        System.out.println("Client connected");
        
        while(true) {
            String message = new String(socket.getMessageWhenNotEmpty());
            System.out.println("Received message " + message);
            
            if (message.equals("exit")) {
                break;
            }
        }
        
        socket.close();
    }
}
