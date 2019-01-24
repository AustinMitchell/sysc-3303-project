package utils;

import java.io.InputStream;

public class ResLoader {
    public static InputStream load(String path) {
        InputStream input = ResLoader.class.getResourceAsStream(path);
        if (input == null) {
            input = ResLoader.class.getResourceAsStream("/"+path);
        }
        return input;
    }
}
