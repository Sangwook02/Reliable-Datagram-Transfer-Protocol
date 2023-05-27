package Packet;

public class Ack {
    // sequence Number which receiver expects
    private int y;
    // receiver window's spare space
    private int w;

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public Ack(int y, int w) {
        this.y = y;
        this.w = w;
    }
}
