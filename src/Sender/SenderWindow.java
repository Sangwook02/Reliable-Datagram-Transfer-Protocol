package Sender;

import Packet.Segment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SenderWindow {
    private int windowSize;
    private int lastByteWritten;
    private int sendBase;
    private int lastByteAcked;

    public SenderWindow() {
        String resource = "config/RDTP.properties";
        Properties properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("can not open configuration file");
        }
        this.windowSize = Integer.parseInt(properties.getProperty("sender_window_size"));
        this.lastByteWritten = 0;
        this.sendBase = 0;
        this.lastByteAcked = 0;

    }
    public void insert(Segment segment) {

    }
}
