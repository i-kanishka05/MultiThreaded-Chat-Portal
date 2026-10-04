import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {

    private static final int PORT = 5000;

    private final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private final String logFile = "server.log";

    public static void main(String[] args) {
        new ChatServer().startServer();
    }

    public void startServer() {

        System.out.println("==========================================");
        System.out.println("       COMPANY CHAT PORTAL SERVER");
        System.out.println("==========================================");
        System.out.println("Starting server...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("Server started successfully.");
            System.out.println("Listening on port: " + PORT);
            System.out.println("Waiting for clients...\n");

            log("SERVER_STARTED | Port: " + PORT);

            while (true) {

                Socket clientSocket = serverSocket.accept();

                ClientHandler client =
                        new ClientHandler(clientSocket, this);

                client.start();

                System.out.println(
                        "New connection: " +
                        clientSocket.getInetAddress().getHostAddress()
                );
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
            log("SERVER_ERROR | " + e.getMessage());
        }
    }

    public synchronized void addClient(ClientHandler client) {

        String username = client.getUsername();

        if (username != null) {
            clients.put(username.toLowerCase(), client);

            System.out.println(
                    username + " connected. Online users: " +
                    clients.size()
            );

            log("USER_JOINED | " + username);
        }
    }

    public synchronized void removeClient(ClientHandler client) {

        String username = client.getUsername();

        if (username != null) {
            clients.remove(username.toLowerCase());

            System.out.println(
                    username + " disconnected. Online users: " +
                    clients.size()
            );

            log("USER_LEFT | " + username);
        }
    }

    public boolean isUsernameTaken(String username) {
        return clients.containsKey(username.toLowerCase());
    }

    public Set<String> getOnlineUsers() {

        Set<String> users = new TreeSet<>(
                String.CASE_INSENSITIVE_ORDER
        );

        for (ClientHandler client : clients.values()) {
            if (client.getUsername() != null) {
                users.add(client.getUsername());
            }
        }

        return users;
    }

    public void broadcastMessage(ChatMessage message,
                                 ClientHandler sender) {

        for (ClientHandler client : clients.values()) {

            if (client != sender) {
                client.sendMessage(message.toString());
            }
        }

        System.out.println(message);
        log("MESSAGE | " + message);
    }

    public void broadcastSystemMessage(String message) {

        String formatted =
                "[SYSTEM] " + message;

        for (ClientHandler client : clients.values()) {
            client.sendMessage(formatted);
        }

        System.out.println(formatted);
        log("SYSTEM | " + message);
    }

    public boolean sendPrivateMessage(ChatMessage message) {

        ClientHandler receiver =
                clients.get(message.getReceiver().toLowerCase());

        if (receiver == null) {
            return false;
        }

        receiver.sendMessage(
                "[PRIVATE] " + message.toString()
        );

        ClientHandler sender =
                clients.get(message.getSender().toLowerCase());

        if (sender != null) {
            sender.sendMessage(
                    "[PRIVATE] " + message.toString()
            );
        }

        log("PRIVATE_MESSAGE | " + message);

        return true;
    }

    private synchronized void log(String message) {

        try (FileWriter writer =
                     new FileWriter(logFile, true)) {

            String time =
                    LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern(
                                    "yyyy-MM-dd HH:mm:ss"
                            )
                    );

            writer.write(
                    "[" + time + "] " +
                    message +
                    System.lineSeparator()
            );

        } catch (IOException e) {
            System.out.println(
                    "Unable to write server log."
            );
        }
    }
}