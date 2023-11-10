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

            SwingUtilities.invokeLater(this::initGUI);

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

    private void initGUI() {
        JFrame frame = new JFrame("Servidor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        frame.add(scrollPane);
        frame.setVisible(true);
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter clientOut;
        private String clientName;
        private int rowIndex;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            this.clientName = "Cliente" + clientSocket.getPort();
            this.rowIndex = tableModel.getRowCount();

            try {
                this.clientOut = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            SwingUtilities.invokeLater(() ->
                    tableModel.addRow(new Object[]{clientName, "Conectado", "Aguardando mensagens..."}));
        }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String message;
                while ((message = in.readLine()) != null) {
                    final String finalMessage = message;
                    SwingUtilities.invokeLater(() -> {
                        tableModel.setValueAt(finalMessage, rowIndex, 2);
                        sendToAllClients("Cliente" + clientSocket.getPort() + ": " + finalMessage);
                    });
                }

                SwingUtilities.invokeLater(() -> tableModel.setValueAt("Desconectado", rowIndex, 1));

                clients.remove(this);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeResources();
            }
        }

        private void closeResources() {
            try {
                clientOut.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void sendToAllClients(String message) {
            for (ClientHandler client : clients) {
                client.clientOut.println(message);
            }
        }
    }

    public static void main(String[] args) {
        int port = 12345;
        new ServerTCP(port);
    }
}
