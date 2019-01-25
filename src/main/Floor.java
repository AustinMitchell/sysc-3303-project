package main;

import java.net.DatagramSocket;

public class Floor { 
    public static final int PORT = 5000;
    
    private DatagramSocket _sendSocket;      // Socket to send data on
    private DatagramSocket _recvSocket;      // Socket to recieve data on
    
    public Floor() {
        _sendSocket = null;
        _recvSocket = null;
        
        
    }
    
    public void run() {
        
    }
    
    public static void main(String[] args) { new Floor().run(); }
}
