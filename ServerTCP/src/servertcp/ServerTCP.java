import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerTCP {
    private ServerSocket serverSocket;
    private DefaultTableModel tableModel;
    private CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public ServerTCP(int port) {
        try {
            serverSocket = new ServerSocket(port);
            tableModel = new DefaultTableModel(new String[]{"Nome", "Status", "Dados"}, 0);

            JFrame frame = new JFrame("Servidor");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);

            JTable table = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(table);

            frame.add(scrollPane);
            frame.setVisible(true);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket);
                handler.start();
                clients.add(handler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private String clientName;
        private int rowIndex;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            this.clientName = "Cliente" + clientSocket.getPort();
            SwingUtilities.invokeLater(() -> {
                rowIndex = tableModel.getRowCount();
                tableModel.addRow(new Object[]{clientName, "Conectado", "Aguardando mensagens..."});
            });
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String message;
                while ((message = in.readLine()) != null) {
                    final String finalMessage = message;
                    SwingUtilities.invokeLater(() -> {
                        tableModel.setValueAt(finalMessage, rowIndex, 2);
                    });
                }

                SwingUtilities.invokeLater(() -> {
                    tableModel.setValueAt("Desconectado", rowIndex, 1);
                });

                in.close();
                clientSocket.close();

                clients.remove(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int port = 12345;
        new ServerTCP(port);
    }
}
