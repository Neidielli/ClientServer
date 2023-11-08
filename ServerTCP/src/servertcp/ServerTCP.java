package servertcp;

/**
 *
 * @author neidi
 */

import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ServerTCP {
    private ServerSocket serverSocket;
    private DefaultTableModel tableModel;

    public ServerTCP(int port) {
        try {
            serverSocket = new ServerSocket(port);
            tableModel = new DefaultTableModel(new String[]{"Nome", "Status", "Dados"}, 0);

            JFrame frame = new JFrame("Servidor");
            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);

            frame.add(scrollPane);
            frame.setSize(400, 300);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private String clientName;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            this.clientName = "Cliente" + clientSocket.getPort();
            tableModel.addRow(new Object[]{clientName, "Conectado", "Aguardando mensagens..."});
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String message;
                while ((message = in.readLine()) != null) {
                    tableModel.setValueAt("Recebendo mensagem...", tableModel.getRowCount() - 1, 2);
      
                }

                tableModel.setValueAt("Desconectado", tableModel.getRowCount() - 1, 1);
                out.close();
                in.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int port = 12345; // Porta do servidor
        new ServerTCP(port);
    }
}
