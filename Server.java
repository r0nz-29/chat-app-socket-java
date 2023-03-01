import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static ArrayList<ClientThread> clientList = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8000)) {
            System.out.println("Server is running and listening on port 8000");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket);
                ClientThread clientThread = new ClientThread(socket);
                clientList.add(clientThread);
                clientThread.start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e);
        }
    }

    private static class ClientThread extends Thread {
        private Socket socket;
        private PrintWriter writer;

        public ClientThread(Socket socket) {
            this.socket = socket;
            try {
                writer = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                System.out.println("Error getting output stream: " + e);
//                close();
            }
        }

        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("Received message: " + message);
                    int clientIndex = Integer.parseInt(message.substring(0, 1));
                    clientList.get(clientIndex).sendMessage(message);
                }
            } catch (IOException e) {
                System.out.println("Error reading from client: " + e);
            }
        }

        private void close() {
            try {
                socket.close();
                clientList.remove(this);
                System.out.println("Closed connection with client " + socket);
            } catch (IOException e) {
                System.out.println("Error closing connection with client " + socket + ": " + e);
            }
        }

        private void sendMessage(String message) {
            writer.println(message);
        }
    }
}