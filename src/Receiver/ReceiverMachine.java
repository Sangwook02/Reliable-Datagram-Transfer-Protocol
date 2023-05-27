package Receiver;

import Channel.Channel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReceiverMachine {
    private String ipAddress;
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
        String resource = "config/RDTP.properties";
        Properties properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("can not open configuration file");
        }
        this.ipAddress = properties.getProperty("receiver_ip_addr");
    }
}
