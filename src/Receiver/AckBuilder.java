package Receiver;

import Packet.Ack;

public class AckBuilder {
    private static final AckBuilder instance = new AckBuilder();

    private AckBuilder() {
    }

    public static AckBuilder getInstance() {
        return instance;
    }
    public Ack makeAck(int y, int w) {
        Ack ack = new Ack(y, w);
        return ack;
    }
}
