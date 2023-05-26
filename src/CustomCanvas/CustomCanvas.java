package CustomCanvas;

import Packet.Segment;
import Sender.WindowElement;

import java.awt.*;
import java.util.ArrayList;

public class CustomCanvas extends Canvas {
    private int sendBase;
    private int rcvBase;
    private int lastByteRead;
    private ArrayList<WindowElement> windowElements;
    private ArrayList<Segment> segments;

    public void setSendBase(int sendBase) {
        this.sendBase = sendBase;
    }

    public void setRcvBase(int rcvBase) {
        this.rcvBase = rcvBase;
    }

    public void setLastByteRead(int lastByteRead) {
        this.lastByteRead = lastByteRead;
    }

    public void setWindowElements(ArrayList<WindowElement> windowElements) {

        this.windowElements = windowElements;
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
            return;
        }

    }

    private void drawWindow(Graphics g) {
        int x = 40;
        int y = 150;
        for(WindowElement element:windowElements) {
            if (element.getSequenceNumber() == null) {
                x += 30;
                drawNotSentElement(g, element, x, y);
            } else if (element.getSequenceNumber() > sendBase) { // RED가 아닌 것
                x += 30;
                drawWindowElement(g, element, x, y);
            } else if (element.getSequenceNumber() == sendBase) {
                drawArrow(g, x+25,310);
                char a[] = "sendBase".toCharArray();
                g.drawChars(a, 0 ,a.length, x, 360);
                x += 30;
                drawWindowElement(g, element, x, y);
            } else {
                x += 30;
                drawSentAndAckedElement(g, element, x, y);
            }
        }
    }
    private void drawSentAndAckedElement(Graphics g, WindowElement element, int x, int y) {
        g.setColor(Color.RED);
        g.drawRect(x, y, 20, 150);
        g.drawString(element.getSequenceNumber().toString(),x+3, y+50);
        g.drawString(String.valueOf(element.getLength()),x+3, y+100);
    }
    private void drawNotSentElement(Graphics g, WindowElement element, int x, int y) {
        g.setColor(Color.GREEN);
        g.drawRect(x, y, 20, 150);
        g.drawString(String.valueOf(element.getLength()),x+3, y+100);
    }
    private void drawWindowElement(Graphics g, WindowElement element, int x, int y) {
        if (element.getTimeSent() == null) {
            g.setColor(Color.GREEN);
            g.drawRect(x, y, 20, 150);
            g.drawString(element.getSequenceNumber().toString(),x+3, y+50);
            g.drawString(String.valueOf(element.getLength()),x+3, y+100);
        }
        else {
            g.setColor(Color.BLUE);
            g.drawRect(x, y, 20, 150);
            g.drawString(element.getSequenceNumber().toString(),x+3, y+50);
            g.drawString(String.valueOf(element.getLength()),x+3, y+100);
        }
    }
    private void drawArrow(Graphics g, int x, int y) {
        g.drawLine(x,y, x-10, y+10);
        g.drawLine(x,y, x+10, y+10);
        g.drawLine(x,y, x, y+30);
    }
    private void drawSegments(Graphics g) {
        int x = 40;
        int y = 150;
        for(Segment segment:segments) {
            if (segment.getSequenceNumber() < rcvBase) { // RED
                x += 30;
                // TODO: draw method 추가
                drawRcvedAndReadSegment(g, segment, x, y);
            } else if (segment.getSequenceNumber() == rcvBase) {
                drawArrow(g, x+25,310);
                char a[] = "rcvBase".toCharArray();
                g.drawChars(a, 0 ,a.length, x, 360);
                x += 30;
                // TODO: draw method 추가
            } else if (segment.getSequenceNumber() > rcvBase){ // BLUE
                x += 30;
                drawRcvedAndNotReadSegment(g, segment, x, y);
            } else {
                System.out.println("something went wrong");
                return;
            }
        }
        // TODO: show size of spare space
    }

    public void drawRcvedAndReadSegment(Graphics graphics, Segment segment, int x, int y) {
        graphics.setColor(Color.RED);
        graphics.drawRect(x, y, 20, 150);
        graphics.drawString(String.valueOf(segment.getLength()),x+3, y+75);
    }

    public void drawRcvedAndNotReadSegment(Graphics graphics, Segment segment, int x, int y) {
        graphics.setColor(Color.BLUE);
        graphics.drawRect(x, y, 20, 150);
        graphics.drawString(String.valueOf(segment.getLength()),x+3, y+75);
    }
}
