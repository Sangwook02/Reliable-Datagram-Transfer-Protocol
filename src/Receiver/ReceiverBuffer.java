package Receiver;

import CustomCanvas.CustomCanvas;
import Packet.Segment;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ReceiverBuffer {
    private int windowSize;
    private int rcvBase;
    private ArrayList<Segment> window;
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
        this.window = new ArrayList<>();
    }

    public Segment bring() {
        if (window.size() == 0) {
            System.out.println("return null from bring()");
            return null;
        }
        int total = 0;
        if (rcvBase == 0) {
            return window.get(0);
        }
        if (window.get(window.size()-1).getSequenceNumber() < rcvBase) {
            return null;
        }
        for(Segment seg: window) {
            total += seg.getLength();
            if (seg.getSequenceNumber() == rcvBase) {
                return seg;
            }
        }
        System.out.println("return null from bring()");
        return null;
    }


    public int getWindowSize() {
        return windowSize;
    }

    public int getRcvBase() {
        return rcvBase;
    }

    public void setRcvBase(int rcvBase) {
        this.rcvBase += rcvBase;
    }

    public ArrayList<Segment> getWindow() {
        return window;
    }

    public void insert(Segment segment) {
        if (!window.contains(segment)) {
            window.add(segment);
            printBuffer("ReceiverBuffer: Successfully inserted");
        }
        else {
            System.out.println("segment is already in window");
        }
    }

    public void printBuffer(String msg) {
        /*
        rcved && read RED
        rcved && not read BLUE
        spare space YELLOW
         */
        Frame receiverBufferFrame = new Frame();
        receiverBufferFrame.setVisible(true);
        receiverBufferFrame.setSize(1000, 500);
        receiverBufferFrame.setBackground(Color.gray);
        receiverBufferFrame.setTitle(msg);
        CustomCanvas customCanvas = new CustomCanvas();
        customCanvas.setSegments(window);
        customCanvas.setRcvBase(rcvBase);
        receiverBufferFrame.add(customCanvas);

    }
}
