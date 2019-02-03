package network.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/** Socket class with setup interface indended to be used as a Client */
public class ClientSocket extends Socket {
    
    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */
    
    private int _id;

    /* ============================= */
    /* ========== GETTERS ========== */
    
    /** After connecting with server, this is the ID offered by server */
    public int id() { return _id; }

    /* ================================== */
    /* ========== CONSTRUCTORS ========== */
    
    /** Constructs an socket with the destination address and port set */
    public ClientSocket(Object observer, String sendAddress, int sendPort) throws SocketException, UnknownHostException {
        super(observer);
        setSendDestination(sendAddress, sendPort);
    }
    
    /** Constructs an socket with the destination address and port set */
    public ClientSocket(Object observer, InetAddress sendAddress, int sendPort) throws SocketException {
        super(observer);
        setSendDestination(sendAddress, sendPort);
    }
    
    /* ============================= */
    /* ========== METHODS ========== */

    @Override
    public boolean setup() {
        return initialSendToServer() && initialReceiveFromServer();
    }

    /** For the setup function, sends a message to the server using its own receiving port */
    public boolean initialSendToServer() {
        byte[] sendData = {1};
        _sendPacket.setData(sendData);
        try {
            _recvSocket.send(_sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /** Waits for the server to respond and send a message back through its receiving port */
    public boolean initialReceiveFromServer() {
        try {
            _recvSocket.receive(_recvPacket);
            _id = _recvPacket.getData()[0];
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
