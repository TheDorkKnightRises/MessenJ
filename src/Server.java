import com.sun.istack.internal.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Samriddha Basu on 9/8/2016.
 */
public class Server extends JFrame {
    private JTextField inputField;
    private JTextArea textArea;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private ServerSocket serverSocket;
    private Socket socket;
    private int port;
    private String user;
    private JButton sendButton;
    private JPanel contentPane;

    public Server(int port, @Nullable String user) {
        super("MessenJ IM - Server");
        setContentPane(contentPane);
        setSize(640, 480);
        setLocationByPlatform(true);
        getRootPane().setDefaultButton(sendButton);

        this.port = port;
        if (!user.equals("")) {
            this.user = user;
        } else this.user = "SERVER";
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
        setupServer();
    }

    void setupServer() {
        try {
            serverSocket =  new ServerSocket(port, 5);
            while (true) {
                try {
                    waitForConnection();
                    setupStreams();
                    whileConnected();
                } catch (EOFException e) {
                    showMessage("Server ended the connection");
                } finally {
                    close();
                }
            }
        } catch (BindException e) {
            showMessage("This port is already in use. Try hosting on a different port");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void allowTyping(final boolean allowed) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                inputField.setEditable(allowed);
                sendButton.setEnabled(allowed);
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

    void waitForConnection() throws IOException {
        showMessage("Waiting to connect...");
        socket = serverSocket.accept();
        showMessage("Connecting to " + socket.getInetAddress().getHostName());
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
        } while (!message.equals("Client: END"));
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
