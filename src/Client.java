import com.sun.istack.internal.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Samriddha Basu on 9/8/2016.
 */
public class Client extends JFrame {
    JTextField inputField;
    JTextArea textArea;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;
    Socket socket;
    String serverIP;
    int serverPort;
    String user;
    String message = "";
    private JButton sendButton;
    private JPanel contentPane;

    public Client(String host, int port, @Nullable  String user) {
        super("MessenJ IM - Client");
        setContentPane(contentPane);
        setSize(640, 480);
        setLocationByPlatform(true);
        getRootPane().setDefaultButton(sendButton);

        if (!user.equals("")) {
            this.user = user;
        } else this.user = "Client";
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        serverIP = host;
        serverPort = port;
        allowTyping(false);
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = e.getActionCommand();
                if (!text.equals("")) {
                    send(text);
                    inputField.setText("");
                }
            }
        });
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = inputField.getText();
                if (!text.equals("")) {
                    send(text);
                    inputField.setText("");
                }
            }
        });
        setVisible(true);
        setup();
    }

    void setup() {
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

    void allowTyping(final boolean allowed) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                inputField.setEditable(allowed);
            }
        });
    }

    void send(String text) {
        try {
            outputStream.writeObject(user+": " + text);
            outputStream.flush();
            showMessage(user+": " + text);
        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Couldn\'t send your message");
        }
    }

    void showMessage(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textArea.append(text+"\n");
            }
        });
    }

    void connect() throws IOException {
        try {
            showMessage("Attempting connection to server...");
            socket = new Socket(InetAddress.getByName(serverIP), serverPort);
            showMessage("Connecting to " + socket.getInetAddress().getHostName());
        } catch (ConnectException e) {
            showMessage("Could not connect to server at that address");
        }
    }

    void setupStreams() throws IOException {
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(socket.getInputStream());
        showMessage("Connection established");
    }

    void whileConnected() throws IOException {
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

    void close() {
        showMessage("Closing all connections...");
        allowTyping(false);
        try {
            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            showMessage("All connections closed.");
        }
    }
}
