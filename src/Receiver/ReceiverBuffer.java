package Receiver;

import Packet.Segment;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ReceiverBuffer {
    private int windowSize;
    private int rcvBase;
    private int lastByteRcvd;
    private Deque<Segment> window;
    private static final ReceiverBuffer instance= new ReceiverBuffer();

    public static ReceiverBuffer getInstance() {
        return instance;
    }

    private ReceiverBuffer() {
        String resource = "config/RDTP.properties";
        Properties properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("can not open configuration file");
        }
        this.windowSize = Integer.parseInt(properties.getProperty("receiver_window_size"));
        this.rcvBase = 0;
        this.lastByteRcvd = 0;
        this.window = new LinkedList<Segment>();
    }

    public int getWindowSize() {
        return windowSize;
    }

    public int getRcvBase() {
        return rcvBase;
    }

    public Deque<Segment> getWindow() {
        return window;
    }

    public void insert(Segment segment) {
        if (!window.contains(segment)) {
            window.add(segment);
        }
        else {
            System.out.println("segment is already in window");
        }
    }
}
