package Receiver;

import Channel.Channel;

public class ReceiverMachine {
    private ReceiverUpperApplication receiverUpperApplication = ReceiverUpperApplication.getInstance();
    private ReceiverTransport receiverTransport = ReceiverTransport.getInstance();
    private Channel channel;
    private static final ReceiverMachine instance = new ReceiverMachine();

    public static ReceiverMachine getInstance() {
        return instance;
    }

    public ReceiverTransport getReceiverTransport() {
        return receiverTransport;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
        receiverTransport.setChannel(channel);
    }

    private ReceiverMachine() {
    }
}
