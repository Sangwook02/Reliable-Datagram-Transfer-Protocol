package Sender;

public class SenderUpperApplication {
    private static final Sender sender = Sender.getInstance();
    private String message;
    public void write(String message) {
        sender.getData(message);
    }
}
