package Receiver;

import Packet.Segment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

public class ReceiverBuffer {
    private int windowSize;
    private int rcvBase;
    private int lastByteRcvd;
    private ArrayList<Segment> window;

    public ReceiverBuffer() {
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
        this.window = new ArrayList<Segment>(windowSize);
    }

    public int getWindowSize() {
        return windowSize;
    }

    public int getRcvBase() {
        return rcvBase;
    }

    public ArrayList<Segment> getWindow() {
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
    // TODO: upper layer에서 data 읽어가는 경우의 작동
}
