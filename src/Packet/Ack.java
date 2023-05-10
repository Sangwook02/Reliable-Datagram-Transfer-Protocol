package Packet;

public class Ack {
    // receiver가 기대하는 sequence number
    private int y;
    // receiver's window의 여유 공간
    private int w;

    public Ack(int y, int w) {
        this.y = y;
        this.w = w;
    }
}
