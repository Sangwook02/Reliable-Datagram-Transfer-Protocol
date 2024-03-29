package CustomCanvas;

import Packet.Segment;
import Packet.WindowElement;

import java.awt.*;
import java.util.ArrayList;

public class CustomCanvas extends Canvas {
    private int sendBase;
    private int senderWindowSize;
    private ArrayList<WindowElement> windowElements;
    private int rcvBase;
    private int receiverWindowSize;
    private ArrayList<Segment> segments;

    public void setSendBase(int sendBase) {
        this.sendBase = sendBase;
    }

    public void setSenderWindowSize(int senderWindowSize) {
        this.senderWindowSize = senderWindowSize;
    }

    public void setWindowElements(ArrayList<WindowElement> windowElements) {
        this.windowElements = windowElements;
    }

    public void setRcvBase(int rcvBase) {
        this.rcvBase = rcvBase;
    }

    public void setReceiverWindowSize(int receiverWindowSize) {
        this.receiverWindowSize = receiverWindowSize;
    }

    public void setSegments(ArrayList<Segment> segments) {
        this.segments = segments;
    }

    @Override
    public void paint(Graphics graphics) {
        if (windowElements != null && segments == null) {
            // print senderBuffer
            drawWindow(graphics);
        } else if (windowElements == null && segments != null) {
            // print segments
            drawSegments(graphics);
        } else {
            System.out.println("something went wrong");
        }
    }

    private void drawWindow(Graphics graphics) {
        int x = 40;
        int y = 150;
        for(WindowElement element:windowElements) {
            if (element.getSequenceNumber() == null) {
                x += 40;
                drawNotSentElement(graphics, element, x, y);
            } else if (element.getSequenceNumber() > sendBase) { // RED가 아닌 것
                x += 40;
                drawWindowElement(graphics, element, x, y);
            } else if (element.getSequenceNumber() == sendBase) {
                drawArrow(graphics, x+35,310);
                char a[] = "sendBase".toCharArray();
                graphics.drawChars(a, 0 ,a.length, x, 360);
                x += 40;
                drawWindowElement(graphics, element, x, y);
            } else {
                x += 40;
                drawSentAndAckedElement(graphics, element, x, y);
            }
        }
        int spareSpace = senderWindowSize + sendBase;
        for (WindowElement element:windowElements){
            spareSpace -= element.getLength();
        }
        x += 40;
        drawSpareSpace(graphics, spareSpace, x, y);
    }

    private void drawSentAndAckedElement(Graphics graphics, WindowElement element, int x, int y) {
        graphics.setColor(Color.RED);
        graphics.drawRect(x, y, 30, 150);
        graphics.drawString(element.getSequenceNumber().toString(),x+3, y+50);
        graphics.drawString(String.valueOf(element.getLength()),x+3, y+100);
    }

    private void drawNotSentElement(Graphics graphics, WindowElement element, int x, int y) {
        graphics.setColor(Color.GREEN);
        graphics.drawRect(x, y, 30, 150);
        graphics.drawString(String.valueOf(element.getLength()),x+3, y+100);
    }

    private void drawWindowElement(Graphics graphics, WindowElement element, int x, int y) {
        if (element.getTimeSent() == null) {
            graphics.setColor(Color.GREEN);
            graphics.drawRect(x, y, 30, 150);
            graphics.drawString(element.getSequenceNumber().toString(),x+3, y+50);
            graphics.drawString(String.valueOf(element.getLength()),x+3, y+100);
        }
        else {
            graphics.setColor(Color.BLUE);
            graphics.drawRect(x, y, 30, 150);
            graphics.drawString(element.getSequenceNumber().toString(),x+3, y+50);
            graphics.drawString(String.valueOf(element.getLength()),x+3, y+100);
        }
    }

    private void drawArrow(Graphics graphics, int x, int y) {
        graphics.setColor(Color.BLACK);
        graphics.drawLine(x,y, x-10, y+10);
        graphics.drawLine(x,y, x+10, y+10);
        graphics.drawLine(x,y, x, y+30);
    }

    private void drawSegments(Graphics graphics) {
        int x = 40;
        int y = 150;
        for(Segment segment:segments) {
            if (segment.getSequenceNumber() < rcvBase) { // RED
                x += 40;
                drawRcvedAndReadSegment(graphics, segment, x, y);
            } else if (segment.getSequenceNumber() == rcvBase) {
                drawArrow(graphics, x+35,310);
                char a[] = "rcvBase".toCharArray();
                graphics.drawChars(a, 0 ,a.length, x, 360);
                x += 40;
                drawRcvedAndNotReadSegment(graphics, segment, x, y);
            } else if (segment.getSequenceNumber() > rcvBase){ // BLUE
                x += 40;
                drawRcvedAndNotReadSegment(graphics, segment, x, y);
            } else {
                System.out.println("something went wrong");
                return;
            }
        }
        int spareSpace = receiverWindowSize + rcvBase;
        for (Segment segment:segments){
            spareSpace -= segment.getLength();
        }
        x += 40;
        drawSpareSpace(graphics, spareSpace, x, y);
    }

    public void drawRcvedAndReadSegment(Graphics graphics, Segment segment, int x, int y) {
        graphics.setColor(Color.RED);
        graphics.drawRect(x, y, 30, 150);
        graphics.drawString(String.valueOf(segment.getLength()),x+3, y+75);
    }

    public void drawRcvedAndNotReadSegment(Graphics graphics, Segment segment, int x, int y) {
        graphics.setColor(Color.BLUE);
        graphics.drawRect(x, y, 30, 150);
        graphics.drawString(String.valueOf(segment.getLength()),x+3, y+75);
    }

    public void drawSpareSpace(Graphics graphics,int size, int x, int y) {
        graphics.setColor(Color.YELLOW);
        graphics.drawRect(x,y, 100, 150);
        graphics.drawString("not used:", x+30, y+65);
        graphics.drawString(String.valueOf(size),x+30, y+95);
    }
}