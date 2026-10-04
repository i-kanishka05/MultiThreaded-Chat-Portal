import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {

        System.out.println("==========================================");
        System.out.println("          COMPANY CHAT PORTAL");
        System.out.println("==========================================");

        try (
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(
                    socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in)
        ) {

            System.out.println("Connected to chat server.");

            Thread readerThread = new Thread(() -> {
                try {
                    String message;

                    while ((message = input.readLine()) != null) {

                        if (message.equals("ENTER_USERNAME")) {
                            System.out.print("Username: ");
                        } else {
                            System.out.println();
                            System.out.println(message);
                            System.out.print("> ");
                        }
                    }

                } catch (IOException e) {
                    System.out.println();
                    System.out.println("Disconnected from server.");
                }
            });

            readerThread.setDaemon(true);
            readerThread.start();

            while (true) {

                System.out.print("> ");

                String message = scanner.nextLine();

                output.println(message);

                if (message.equalsIgnoreCase("/quit")) {
                    break;
                }
            }

        } catch (ConnectException e) {

            System.out.println("Unable to connect to server.");
            System.out.println("Make sure ChatServer is running first.");

        } catch (IOException e) {

            System.out.println("Connection error: " + e.getMessage());
        }
    }
}