package network;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ServerSocket extends Socket {

    /* ===================================== */
    /* ========== PRIVATE MEMBERS ========== */
    
    private byte _id;

    /* ================================== */
    /* ========== CONSTRUCTORS ========== */
    
    /** Constructs a socket set up to receive on a specified port. */
    public ServerSocket(int recvPort) throws SocketException, UnknownHostException {
        this(recvPort, (byte)1);
    }
    /** Constructs a socket set up to receive on a specified port with an ID to send to the client */
    public ServerSocket(int recvPort, byte id) throws SocketException, UnknownHostException {
        super(recvPort);
        _id = id;
    }

    /* ============================= */
    /* ========== METHODS ========== */
    
    @Override
    public boolean setup() {
        try {
            _recvSocket.receive(_recvPacket);
            setSendDestination(_recvPacket.getAddress(), _recvPacket.getPort());

            byte[] sendData = {_id};
            _sendPacket.setData(sendData);
            _sendSocket.send(_sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
