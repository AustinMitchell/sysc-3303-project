package utils.message;

import java.nio.ByteBuffer;
import java.util.Arrays;

/** Wrapper around byte array to get basic message details */
public class Message {

    public static String bytesToString(byte[] message) {
        if (message.length < 5) {
            return String.format("<invalid message %s>", Arrays.toString(message));
        }
        
        StringBuilder   sb      = new StringBuilder("[");
        ByteBuffer      buffer  = ByteBuffer.wrap(message);
        
        sb.append(MessageType.fromOrdinal(buffer.get()) + ", ");
        sb.append("id: " + buffer.getInt() + ", ");
        
        while(buffer.hasRemaining()) {
            sb.append(buffer.get() + ", ");
        }
        
        // Delete the last ', ' and place a close bracket
        return sb.delete(sb.length()-2, sb.length()).append("]").toString();
    }
    
    private byte[] _message;
    
    /** Returns the count given in this message */
    public int messageCount() { return ByteBuffer.wrap(_message).position(1).getInt(); }
    
    /** Returns the message type */
    public MessageType messageType() { return MessageType.fromOrdinal(_message[0]); }
    
    /** Returns the attached carID in the message */
    public int carID() { return _message[5]; }
    
    /** Returns the backing array */
    public byte[] bytes() { return _message; }

    public Message(byte[] message) {
        _message = message;
    }
    
    /** Returns a string representing this message. */
    public String toString() {
        return bytesToString(_message);
    }
}
