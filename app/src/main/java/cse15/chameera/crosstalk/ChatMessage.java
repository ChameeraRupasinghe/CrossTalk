package cse15.chameera.crosstalk;

public class ChatMessage {

    private String message;
    private String sender;
    private String recipient;
    private int senderOrReciver;

    public ChatMessage() {
    }


    public ChatMessage(String sender, String recipient, String message) {
        this.message = message;
        this.sender = sender;
        this.recipient = recipient;

    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setSenderOrReciver(int senderOrReciver) {
        this.senderOrReciver = senderOrReciver;
    }

    public int getSenderOrReciver() {
        return senderOrReciver;
    }
}
