package clienttcp;

/**
 *
 * @author neidi
 */
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ClientTCP {
    public static void main(String[] args) {
        String serverAddress = "localhost"; // Endereço do servidor
        int serverPort = 12345; // Porta do servidor

        try {
            Socket socket = new Socket(serverAddress, serverPort);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            JFrame frame = new JFrame("Cliente");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 100);

            JTextField textField = new JTextField();
            frame.add(textField);

            JButton sendButton = new JButton("Enviar");
            sendButton.addActionListener(e -> {
                String message = textField.getText();
                out.println(message);
                textField.setText("");
            });
            frame.add(sendButton);

            frame.setVisible(true);

            String serverResponse;
            while ((serverResponse = in.readLine()) != null) {
                // Processar as mensagens recebidas do servidor, se necessário
            }

            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

