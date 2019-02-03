package unittest;

import static org.junit.Assert.*;

import org.junit.Test;

import utils.message.Message;

public class MessageTest {

    @Test
    public void test() {
        String testString = "Hello World!";
        byte[] testBytes = testString.getBytes();

        Message message1 = new Message(testString);
        Message message2 = new Message(testBytes, testBytes.length);

        assertArrayEquals(testBytes, message1.toByteArray());
        assertArrayEquals(testBytes, message2.toByteArray());
    }

}
