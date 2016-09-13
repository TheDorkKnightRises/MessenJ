import java.io.Serializable;

/**
 * Created by Samriddha Basu on 9/11/2016.
 */
public class Message implements Serializable {
    public static final byte TYPE_DISCONNECT = 0;
    public static final byte TYPE_ANNOUNCE = 1;
    public static final byte TYPE_TEXT = 2;
    public static final byte TYPE_FILE = 3;
    private byte type;
    private byte[] data;
    private String text;
    private String sender;

    public Message(String text) {
        this.type = TYPE_ANNOUNCE;
        this.text = text;
    }

    public Message(byte type, String text, String sender) {
        this.type = type;
        this.text = text;
        this.sender = sender;
    }

    public Message(byte type, byte[] data, String text, String sender) {
        this.type = type;
        this.data = data;
        this.text = text;
        this.sender = sender;
    }

    public byte getType() {
        return type;
    }

    public byte[] getData() {
        return data;
    }

    public String getText() {
        return text;
    }

    public String getSender() {
        return sender;
    }
}
