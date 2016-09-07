import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Samriddha Basu on 9/8/2016.
 */
public class Client extends JFrame {
    private JTextField inputField;
    private JTextArea textArea;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Socket socket;
    private String serverIP;
    private int serverPort;
    private String message = "";

    public Client(String host, int port) {
        super("MessenJ IM - Client");
        serverIP = host;
        serverPort = port;
        inputField = new JTextField();
        allowTyping(false);
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send(e.getActionCommand());
                inputField.setText("");
            }
        });
        add(inputField, BorderLayout.SOUTH);
        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea));
        setSize(640, 480);
        setVisible(true);
    }

    public static void main(String arg[]) {
        new Client("localhost", 1181).setup();
    }

    private void setup() {
        try {
            connect();
            setupStreams();
            whileConnected();
        } catch (EOFException e) {
            showMessage("Client terminated connection");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void allowTyping(final boolean allowed) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                inputField.setEditable(allowed);
            }
        });
    }

    private void send(String text) {
        try {
            outputStream.writeObject("Client: " + text);
            outputStream.flush();
            showMessage("Client: " + text);
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Couldn\'t send your message");
        }
    }

    private void showMessage(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textArea.append(text+"\n");
            }
        });
    }

    private void connect() throws IOException {
        showMessage("Attempting connection to server...");
        socket = new Socket(InetAddress.getByName(serverIP), serverPort);
        showMessage("Connected to "+ socket.getInetAddress().getHostName());
    }

    private void setupStreams() throws IOException {
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(socket.getInputStream());
        showMessage("Connection established");
    }

    private void whileConnected() throws IOException {
        String message = "";
        allowTyping(true);
        do {
            try {
                message = (String) inputStream.readObject();
                showMessage(message);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                showMessage("Something went wrong, cannot display message");
            }
        } while (!message.equals("SERVER: END"));
    }

    private void close() {
        showMessage("Closing all connections...");
        allowTyping(false);
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
