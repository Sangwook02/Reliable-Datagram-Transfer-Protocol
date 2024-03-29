package Sender;

import CustomCanvas.CustomCanvas;
import Packet.WindowElement;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class SenderBuffer {
    private int windowSize;
    private int lastByteWritten;
    private int sendBase;
    private int lastByteAcked;
    private ArrayList<WindowElement> window = new ArrayList<WindowElement>();

    public ArrayList<WindowElement> getWindow() {
        return window;
    }

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
        this.lastByteAcked =  -1;
    }

    public void printBuffer(String msg, int lastByteSent) {
        /*
        sent && acked RED
        sent && not acked BLUE
        not sent GREEN
        spare space YELLOW
         */
        Frame senderBufferFrame = new Frame();
        senderBufferFrame.setVisible(true);
        senderBufferFrame.setSize(1000, 500);
        senderBufferFrame.setBackground(Color.gray);
        senderBufferFrame.setTitle(msg);
        CustomCanvas customCanvas = new CustomCanvas();
        customCanvas.setWindowElements(window);
        customCanvas.setSendBase(sendBase);
        customCanvas.setSenderWindowSize(windowSize);
        senderBufferFrame.add(customCanvas);
        System.out.println("=====sender window info=====");
        System.out.println("lastByteAcked = " + lastByteAcked);
        System.out.println("lastByteSent = " + lastByteSent);
        System.out.println("lastByteWritten = " + lastByteWritten);
        System.out.println("============================");
    }

    public boolean insert(int data, int lastByteSent) throws InterruptedException {
        // spare space == (sendBase+windowSize-lastByteWritten-1)
        if(sendBase+windowSize-lastByteWritten-1 >= data) {
            WindowElement element = new WindowElement(data);
            this.window.add(element);
            this.lastByteWritten += data;
            System.out.print("window에 삽입한 후의 여유 공간은 ");
            System.out.println(sendBase+windowSize-lastByteWritten-1);
            System.out.println("successfully inserted!");
            System.out.println("");
            printBuffer("SenderBuffer: Successfully inserted", lastByteSent);
            return true;
        }
        // advWindow's size is not big enough
        printBuffer("SenderBuffer: failed to insert", lastByteSent);
        System.out.println("failed to insert!");
        return false;
    }

    public void sliding(int y) {
        updateAck(y);
    }

    public void updateAck(int y) {
        if (lastByteAcked + 1 != y) {
            lastByteAcked= y-1;
        }
        ArrayList<WindowElement> copy = new ArrayList<>();
        for (WindowElement e: window) {
            copy.add(e);
        }
        Iterator<WindowElement> iterator = copy.iterator();
        while(iterator.hasNext()) {
            WindowElement element = iterator.next();
            if (element.getSequenceNumber() != null && element.getSequenceNumber() < y) {
                if (!element.isAcked()) {
                    element.setAcked(true);
                    sendBase += element.getLength();
                }

            } else {
              break;
            }
        }
    }
}
