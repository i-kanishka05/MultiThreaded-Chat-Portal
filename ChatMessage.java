import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatMessage {

    private String sender;
    private String receiver;
    private String message;
    private LocalDateTime time;

    public ChatMessage(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.time = LocalDateTime.now();
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public String getFormattedTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return time.format(formatter);
    }

    public boolean isPrivateMessage() {
        return receiver != null && !receiver.isEmpty();
    }

    @Override
    public String toString() {
        if (isPrivateMessage()) {
            return "[" + getFormattedTime() + "] " + sender + " -> " + receiver + ": " + message;
        }

        return "[" + getFormattedTime() + "] " + sender + ": " + message;
    }
}