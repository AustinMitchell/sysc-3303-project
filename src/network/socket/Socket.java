package network.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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

    protected InetAddress       _targetAddress;

    protected DatagramSocket    _sendSocket;
    protected DatagramSocket    _recvSocket;
    protected DatagramPacket    _sendPacket;
    protected DatagramPacket    _recvPacket;

    protected Queue<byte[]>     _sendQueue;
    protected Queue<byte[]>     _recvQueue;

    protected boolean           _running;

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

    /** Constructs a new Socket with a specific port to recieve on (usually for server sockets) */
    public Socket(int recievePort) throws SocketException {
        _sendSocket = new DatagramSocket();
        _recvSocket = new DatagramSocket(recievePort);

        _recvPacket = new DatagramPacket(new byte[BUFFER_LENGTH], BUFFER_LENGTH);
        _sendPacket = new DatagramPacket(new byte[BUFFER_LENGTH], BUFFER_LENGTH);

        _sendQueue  = new LinkedList<>();
        _recvQueue  = new LinkedList<>();
    }

    /** Constructs a new Socket without a specific port to recieve on (usually for client sockets) */
    public Socket() throws SocketException {
        _sendSocket = new DatagramSocket();
        _recvSocket = new DatagramSocket();

        _recvPacket = new DatagramPacket(new byte[BUFFER_LENGTH], BUFFER_LENGTH);
        _sendPacket = new DatagramPacket(new byte[BUFFER_LENGTH], BUFFER_LENGTH);

        _sendQueue  = new LinkedList<>();
        _recvQueue  = new LinkedList<>();
    }

    /* ============================= */
    /* ========== METHODS ========== */

    /** Returns true if there are any items in the send queue that haven't been sent */
    public boolean isSendQueueEmpty() { 
        synchronized (_sendQueue) {
            return _sendQueue.isEmpty();
        }
    }

    /** Appends a new message in the queue to send */
    public void sendMessage(byte[] bytes) {
        synchronized (_sendQueue) {
            _sendQueue.add(Arrays.copyOf(bytes, bytes.length));
        }
    }

    /** Returns true if there are any items in the receiving queue to get */
    public boolean hasMessage() {
        synchronized (_recvQueue) {
            return !_recvQueue.isEmpty();
        }
    }

    /** Blocks execution until there's a message in the receiving queue */
    public void waitForMessage() {
        while(!hasMessage());
    }

    /** Polls the receiving queue for a new message */
    public byte[] getMessage() {
        synchronized (_recvQueue) {
            return _recvQueue.poll();
        }
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
                        synchronized (_recvQueue) {
                            _recvQueue.add(data);
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
                boolean hasData = false;
                while(_running) {
                    try {
                        Thread.sleep(10);
                        synchronized (_sendQueue) {
                            if (!_sendQueue.isEmpty()) {
                                byte[] data = _sendQueue.poll();
                                _sendPacket.setData(data);
                                _sendPacket.setLength(data.length);
                                hasData = true;
                            }
                        }
                        if (hasData) {
                            _sendSocket.send(_sendPacket);
                            hasData = false;
                        }
                    } catch (InterruptedException e) {
                        System.err.println();
                        e.printStackTrace();
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
