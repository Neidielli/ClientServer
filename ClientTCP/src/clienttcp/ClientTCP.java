import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientTCP {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private JTextArea chatTextArea;
    private JTextField messageField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ClientTCP().createAndShowGUI();
        });
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Cliente");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        JTextField serverAddressField = new JTextField("localhost");
        serverAddressField.setPreferredSize(new Dimension(100, 30));
        panel.add(serverAddressField);

        JTextField portField = new JTextField("12345");
        portField.setPreferredSize(new Dimension(70, 30));
        panel.add(portField);

        JButton connectButton = new JButton("Conectar");
        panel.add(connectButton);

        frame.add(panel, BorderLayout.NORTH);

        chatTextArea = new JTextArea();
        chatTextArea.setEditable(false);
        frame.add(new JScrollPane(chatTextArea), BorderLayout.CENTER);

        messageField = new JTextField();
        frame.add(messageField, BorderLayout.SOUTH);

        JButton sendButton = new JButton("Enviar");
        sendButton.setEnabled(false);

        sendButton.addActionListener(e -> {
            String message = messageField.getText();
            out.println(message);
            messageField.setText("");
        });

        frame.add(sendButton, BorderLayout.SOUTH);

        frame.setVisible(true);

        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String serverAddress = serverAddressField.getText();
                int serverPort = Integer.parseInt(portField.getText());

                try {
                    socket = new Socket(serverAddress, serverPort);
                    out = new PrintWriter(socket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    connectButton.setEnabled(false);
                    sendButton.setEnabled(true);

                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        chatTextArea.append("Servidor: " + serverResponse + "\n");
                    }

                    // Cliente foi desconectado, então feche a conexão e atualize a interface
                    socket.close();
                    out.close();
                    in.close();
                    connectButton.setEnabled(true);
                    sendButton.setEnabled(false);
                    chatTextArea.append("Desconectado\n");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "A porta deve ser um número válido.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Erro ao conectar ao servidor.");
                }
            }
        });

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (socket != null && !socket.isClosed()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
