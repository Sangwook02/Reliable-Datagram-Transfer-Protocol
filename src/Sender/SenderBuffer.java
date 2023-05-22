package Sender;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class SenderBuffer {
    private int windowSize;
    private int lastByteWritten;
    private int sendBase;
    private int lastByteAcked;
    // TODO: Segment가 아니라 int로 바꿔야 함.
    private ArrayList<WindowElement> window = new ArrayList<WindowElement>();

    public SenderBuffer() {
        String resource = "config/RDTP.properties";
        Properties properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("can not open configuration file");
        }
        this.windowSize = Integer.parseInt(properties.getProperty("sender_window_size"));
        this.lastByteWritten = -1;
        this.sendBase = 0;
        this.lastByteAcked = 0;
    }
    // Sender side의 window에 segment 삽입
    public boolean insert(int data) {
        // spare space is (sendBase+windowSize-lastByteWritten-1) bytes
        if(sendBase+windowSize-lastByteWritten-1 >= data) {
            WindowElement element = new WindowElement(data);
            this.window.add(element);
            this.lastByteWritten += data;
            System.out.print("window에 삽입한 후의 여유 공간은 ");
            System.out.println(sendBase+windowSize-lastByteWritten-1);
            System.out.println("lastByteWritten = " + lastByteWritten);
            System.out.println("successfully inserted!");
            System.out.println("");
            return true;
        }
        // 여유 공간이 부족할 경우.

        System.out.println("failed to insert!");
        return false;

    }

    public List<WindowElement> bringUnAckedData() {
        List<WindowElement> unwrittenData = new ArrayList<>();
        // return List of unAcked element from buffer's window.
        for (WindowElement element:window) {
            if (element.isAcked() == false) {
                unwrittenData.add(element);
            }
        }
        return unwrittenData;
    }

    public int getSendBase() {
        return sendBase;
    }

    public ArrayList<WindowElement> getWindow() {
        return window;
    }

    // TODO: Ack 받았을 때의 작동, sliding 등
    public void sliding(int y) {
//        while (window != null) {
//            System.out.println("window1 = " + window);
//            System.out.println("window.getFirst() = " + window.getFirst());
//            System.out.println("window.getFirst().getSequenceNumber() = " + window.getFirst().getSequenceNumber());
//            // TODO: 두번째 element의 seqNo가 null임.
//            if (window.getFirst().getSequenceNumber() < y) {
//                window.remove();
//            }
//            System.out.println("window2 = " + window);
//        }
    }
}
