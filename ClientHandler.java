import java.io.*;
import java.net.Socket;
import java.util.Set;

public class ClientHandler extends Thread {

    private Socket socket;
    private ChatServer server;
    private BufferedReader input;
    private PrintWriter output;
    private String username;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            requestUsername();

            server.addClient(this);

            sendMessage("\nWelcome to the Company Chat Portal, " + username + "!");
            sendMessage("Type /help to view available commands.");
            server.broadcastSystemMessage(username + " joined the chat.");

            String message;

            while ((message = input.readLine()) != null) {

                message = message.trim();

                if (message.isEmpty()) {
                    continue;
                }

                if (message.equalsIgnoreCase("/quit")) {
                    break;
                }

                if (message.equalsIgnoreCase("/help")) {
                    showHelp();
                } 
                else if (message.equalsIgnoreCase("/users")) {
                    showOnlineUsers();
                } 
                else if (message.startsWith("/msg ")) {
                    handlePrivateMessage(message);
                } 
                else {
                    server.broadcastMessage(
                            new ChatMessage(username, null, message),
                            this
                    );
                }
            }

        } catch (IOException e) {
            System.out.println("Connection lost with " + username);
        } finally {
            disconnect();
        }
    }

    private void requestUsername() throws IOException {
        while (true) {
            output.println("ENTER_USERNAME");
            String name = input.readLine();

            if (name == null) {
                throw new IOException("Client disconnected.");
            }

            name = name.trim();

            if (name.isEmpty()) {
                output.println("Username cannot be empty.");
                continue;
            }

            if (!name.matches("[a-zA-Z0-9_]{3,15}")) {
                output.println("Username must contain 3-15 letters, numbers or underscore.");
                continue;
            }

            if (server.isUsernameTaken(name)) {
                output.println("Username already exists. Choose another one.");
                continue;
            }

            username = name;
            break;
        }
    }

    private void handlePrivateMessage(String command) {

        String data = command.substring(5).trim();

        int spaceIndex = data.indexOf(" ");

        if (spaceIndex == -1) {
            sendMessage("Usage: /msg username message");
            return;
        }

        String receiver = data.substring(0, spaceIndex).trim();
        String message = data.substring(spaceIndex + 1).trim();

        if (message.isEmpty()) {
            sendMessage("Message cannot be empty.");
            return;
        }

        if (receiver.equalsIgnoreCase(username)) {
            sendMessage("You cannot send a private message to yourself.");
            return;
        }

        ChatMessage privateMessage =
                new ChatMessage(username, receiver, message);

        if (!server.sendPrivateMessage(privateMessage)) {
            sendMessage("User '" + receiver + "' is not online.");
        }
    }

    private void showOnlineUsers() {

        Set<String> users = server.getOnlineUsers();

        sendMessage("\n----- ONLINE USERS -----");

        for (String user : users) {
            sendMessage("• " + user);
        }

        sendMessage("------------------------");
    }

    private void showHelp() {

        sendMessage("\n========== CHAT COMMANDS ==========");
        sendMessage("/users              - Show online users");
        sendMessage("/msg user message   - Send private message");
        sendMessage("/help               - Show commands");
        sendMessage("/quit               - Leave chat");
        sendMessage("===================================");
    }

    public void sendMessage(String message) {
        if (output != null) {
            output.println(message);
        }
    }

    public String getUsername() {
        return username;
    }

    private void disconnect() {

        if (username != null) {
            server.removeClient(this);
            server.broadcastSystemMessage(username + " left the chat.");
        }

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing client connection.");
        }
    }
}