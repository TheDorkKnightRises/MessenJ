import javax.swing.*;
import java.awt.event.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Launch extends JFrame {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton serverRadioButton;
    private JRadioButton clientRadioButton;
    private JTextField host;
    private JTextField port;
    private JLabel hostnameLabel;
    private JLabel portLabel;
    private JTextField username;
    private JLabel launchLabel;

    public Launch() {
        setContentPane(contentPane);
        setLocationByPlatform(true);
        getRootPane().setDefaultButton(buttonOK);

        serverRadioButton.setAction(new AbstractAction() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            serverRadioButton.setLabel("Server");
                                            clientRadioButton.setLabel("Client");
                                            clientRadioButton.setSelected(!serverRadioButton.isSelected());
                                            host.setEnabled(clientRadioButton.isSelected());
                                            hostnameLabel.setEnabled(clientRadioButton.isSelected());
                                            serverRadioButton.updateUI();
                                        }
                                    });
        clientRadioButton.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serverRadioButton.setLabel("Server");
                clientRadioButton.setLabel("Client");
                serverRadioButton.setSelected(!clientRadioButton.isSelected());
                host.setEnabled(clientRadioButton.isSelected());
                hostnameLabel.setEnabled(clientRadioButton.isSelected());
                clientRadioButton.updateUI();
            }
        });
        serverRadioButton.setLabel("Server");
        serverRadioButton.setSelected(true);
        hostnameLabel.setEnabled(false);
        host.setEnabled(false);
        clientRadioButton.setLabel("Client");

        buttonOK.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        onOK();
                    }
                });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        final String hostname = host.getText().trim();
        final int portNo = Integer.parseInt(port.getText().trim());
        final String user = username.getText().trim();
        Runnable r;
        if (clientRadioButton.isSelected()) {
            r = new Runnable() {
                @Override
                public void run() {
                    new Client(hostname, portNo, user);
                }
            };
        } else {
            r = new Runnable() {
                @Override
                public void run() {
                    new Server(portNo, user);
                }
            };
        }
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(r);
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        Launch dialog = new Launch();
        dialog.pack();
        dialog.setVisible(true);
    }
}
