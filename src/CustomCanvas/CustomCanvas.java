package CustomCanvas;

import Packet.Segment;
import Sender.WindowElement;

import java.awt.*;
import java.util.ArrayList;

public class CustomCanvas extends Canvas {
    private int sendBase;
    private ArrayList<WindowElement> windowElements;
    private ArrayList<Segment> segments;

    public void setSendBase(int sendBase) {
        this.sendBase = sendBase;
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
        } else if (windowElements != null && segments != null) {
            // print none
        } else {
            // return
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
}
