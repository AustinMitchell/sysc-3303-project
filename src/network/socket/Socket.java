package network.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import utils.DataQueueBox;
import utils.WorkerThread;

/**
 * Wrapper around DatagramSocket objects and provides sending and receiving methods in 
 * alternate threads for non-blocking network IO. Abstract as subclasses determine how
 * to establish connection to target machine.
 */
public abstract class Socket {

    /* ======================================= */
    /* ========== PROTECTED MEMBERS ========== */

    protected static final int BUFFER_LENGTH = 100;

    protected InetAddress           _targetAddress;

    protected DatagramSocket        _sendSocket;
    protected DatagramSocket        _recvSocket;
    protected DatagramPacket        _sendPacket;
    protected DatagramPacket        _recvPacket;

    protected DataQueueBox<byte[]>  _sendQueue;
    protected DataQueueBox<byte[]>  _recvQueue;

    protected boolean               _running;
    protected Object                _observer;

    /* ============================= */
    /* ========== GETTERS ========== */

    /** Get the port to send on */
    public int          sendPort()          { return _sendSocket.getPort(); }
    /** Get the address for the sending socket */
    public InetAddress  sendAddress()       { return _sendSocket.getInetAddress(); }
    /** Get the port the socket is listening on */
    public int          recvPort()          { return _recvSocket.getPort(); }
    /** Get the address for the listening socket */
    public InetAddress  recvAddress()       { return _recvSocket.getInetAddress(); }

    /** Return the connection status of the socket */
    public boolean      isConnected()       { return _running; }

    /* ============================= */
    /* ========== SETTERS ========== */

    /** Sets address and port for packet destination */
    public void setSendDestination(String address, int port) throws UnknownHostException {
        InetAddress targetAddress = InetAddress.getByName(address);
        setSendDestination(targetAddress, port);
    }
    /** Sets address and port for packet destination */
    public void setSendDestination(InetAddress address, int port) {
        _sendPacket.setAddress(address);
        _sendPacket.setPort(port);
    }

    /* ================================== */
    /* ========== CONSTRUCTORS ========== */

    /**
     * Constructs a new Socket with a specific port to recieve on (usually for server sockets)
     * @param observer      Object to be notified of new messages
     * @param recievePort   Port to listen on
     */
    public Socket(Object observer, int recievePort) throws SocketException {
        _sendSocket = new DatagramSocket();
        _recvSocket = new DatagramSocket(recievePort);

        _recvPacket = new DatagramPacket(new byte[BUFFER_LENGTH], BUFFER_LENGTH);
        _sendPacket = new DatagramPacket(new byte[BUFFER_LENGTH], BUFFER_LENGTH);

        _sendQueue  = new DataQueueBox<>();
        _recvQueue  = new DataQueueBox<>();

        _observer   = observer;
    }

    /** Constructs a new Socket without a specific port to recieve on (usually for client sockets)
     * @param observer      Object to be notified of new messages
     */
    public Socket(Object observer) throws SocketException {
        _sendSocket = new DatagramSocket();
        _recvSocket = new DatagramSocket();

        _recvPacket = new DatagramPacket(new byte[BUFFER_LENGTH], BUFFER_LENGTH);
        _sendPacket = new DatagramPacket(new byte[BUFFER_LENGTH], BUFFER_LENGTH);

        _sendQueue  = new DataQueueBox<>();
        _recvQueue  = new DataQueueBox<>();

        _observer   = observer;
    }

    /* ============================= */
    /* ========== METHODS ========== */

    /** Returns true if there are any items in the send queue that haven't been sent */
    public boolean isSendQueueEmpty() { 
        return _sendQueue.isEmpty();
    }

    /** Appends a new message in the queue to send */
    public void sendMessage(byte[] bytes) {
        _sendQueue.put(Arrays.copyOf(bytes, bytes.length));
    }

    /** Returns true if there are any items in the receiving queue to get */
    public boolean hasMessage() {
        return !_recvQueue.isEmpty();
    }

    /** Blocks execution until there's a message in the receiving queue */
    public byte[] getMessageWhenNotEmpty() {
        return _recvQueue.getWhenNotEmpty();
    }

    /** Polls the receiving queue for a new message */
    public byte[] getMessage() {
        return _recvQueue.get();
    }

    /** This function will attempt to establish communications with another socket. How this is accomplished is determined by a child class*/
    public abstract boolean setup();

    /** Instantiates connection using setup() and sets up threads for input and output message queues */
    public boolean runSetupAndStartThreads() {
        _running = setup();
        if (!_running) {
            close();
            return false;
        }

        // Input thread
        new Thread(new Runnable(){
            @Override
            public void run() {
                byte[] data;
                while(_running) {
                    try {
                        _recvSocket.receive(_recvPacket);
                        data = _recvPacket.getData();
                        data = Arrays.copyOf(data, _recvPacket.getLength());
                        if (_observer != null) {
                            synchronized(_observer) {
                                _recvQueue.put(data);
                                _observer.notifyAll();
                            }
                        } else {
                            _recvQueue.put(data);
                        }
                    } catch (IOException e) {
                        System.err.println();
                        e.printStackTrace();
                        _running = false;
                        break;
                    }
                }
            }
        }).start();

        // Output thread
        new Thread(new Runnable(){
            @Override
            public void run() {
                while(_running) {
                    try {
                        byte[] data = _sendQueue.getWhenNotEmpty();
                        _sendPacket.setData(data);
                        _sendPacket.setLength(data.length);
                        _sendSocket.send(_sendPacket);
                    } catch (IOException e) {
                        System.err.println();
                        e.printStackTrace();
                        _running = false;
                        break;
                    }
                }
            }
        }).start();

        return true;
    }

    /** Kills threads and closes sockets */
    public void close() {
        _running = false;
        if (!_sendSocket.isClosed()) {
            _sendSocket.close();
        }
        if (!_recvSocket.isClosed()) {
            _recvSocket.close();
        }
    }

    /** Creates a WorkerThread object which is given a Job that executes the runSetupAndStartThreads() function. */
    public WorkerThread<Boolean, Void> generateSetupWorkerThread() {
        return new WorkerThread<>(new WorkerThread.Job<Boolean, Void>() {
            @Override
            public Boolean execute(List<Void> inputData) {
                return runSetupAndStartThreads();
            }
        });
    }
}
