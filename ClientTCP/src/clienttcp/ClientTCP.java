import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ClientTCP {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private JTextArea chatTextArea;
    private JTextField messageField;
    private JTextField serverAddressField;
    private JTextField portField;
    private JButton connectButton;
    private JButton sendButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientTCP().createAndShowGUI());
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Cliente");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        serverAddressField = new JTextField("localhost");
        serverAddressField.setPreferredSize(new Dimension(100, 30));
        panel.add(serverAddressField);

        portField = new JTextField("12345");
        portField.setPreferredSize(new Dimension(70, 30));
        panel.add(portField);

        connectButton = new JButton("Conectar");
        panel.add(connectButton);

        sendButton = new JButton("Enviar");
        sendButton.setEnabled(false);
        panel.add(sendButton);

        frame.add(panel, BorderLayout.NORTH);

        chatTextArea = new JTextArea();
        chatTextArea.setEditable(false);
        frame.add(new JScrollPane(chatTextArea), BorderLayout.CENTER);

        messageField = new JTextField("Teste");
        frame.add(messageField, BorderLayout.SOUTH);

        configureListeners(frame);

        frame.setVisible(true);
    }

    private void configureListeners(JFrame frame) {
        connectButton.addActionListener(e -> handleConnect());

        sendButton.addActionListener(e -> {
            String message = messageField.getText();
            out.println(message);
            messageField.setText("");
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                disconnect();
            }
        });
    }

    private void handleConnect() {
        String serverAddress = serverAddressField.getText();
        int serverPort;
        try {
            serverPort = Integer.parseInt(portField.getText());
        } catch (NumberFormatException ex) {
            showError("A porta deve ser um número válido.");
            return;
        }
    
        try {
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    
            connectButton.setEnabled(false);
            sendButton.setEnabled(true);
    
            new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        final String responseCopy = serverResponse;  // Cópia da variável
                        SwingUtilities.invokeLater(() -> {
                            chatTextArea.append(responseCopy + "\n");
                        });
                    }
                    disconnect();
                } catch (IOException e) {
                    e.printStackTrace();  // Imprimir a pilha de exceções para depuração
                    showError("Erro ao receber mensagens do servidor.");
                }
            }).start();
            
        } catch (IOException ex) {
            showError("Erro ao conectar ao servidor.");
        }
    }

    private void disconnect() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
                out.close();
                in.close();
                connectButton.setEnabled(true);
                sendButton.setEnabled(false);
                chatTextArea.append("Desconectado\n");
            } catch (IOException e) {
                showError("Erro ao desconectar do servidor.");
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
