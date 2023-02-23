import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8000);
            System.out.println("Connected to server: " + socket);
            InputThread it = new InputThread(socket);
            OutputThread ot = new OutputThread(socket);
            it.start();
            ot.start();
        } catch (UnknownHostException e) {
            System.out.println("Error connecting to server: " + e);
        } catch (IOException e) {
            System.out.println("Error reading or writing to server: " + e);
        }
    }

    private static class InputThread extends Thread {
        Socket socket;

        public InputThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            Scanner input = new Scanner(System.in);
            try {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                while (input.hasNextLine()) {
                    String line = input.nextLine();
                    writer.println(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class OutputThread extends Thread {
        Socket socket;

        public OutputThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println("Received message from server: " + message);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}