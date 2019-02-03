package utils.message;

import java.util.Arrays;

public class Message {

    private String _message;

    public Message(byte[] messageBuffer, int messageLength) {
        _message = new String(Arrays.copyOfRange(messageBuffer, 0, messageLength));
    }

    public Message(String message) {
        _message = message;
    }

    /**
     * Converts message into byte array
     *
     * @return byte array
     */
    public byte[] toByteArray() {
        return _message.getBytes();
    }
}
