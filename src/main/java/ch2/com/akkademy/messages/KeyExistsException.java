package ch2.com.akkademy.messages;

import java.io.Serializable;

public class KeyExistsException extends Exception implements Serializable {
    public final String key;

    public KeyExistsException(String key) {
        this.key = key;
    }
}
