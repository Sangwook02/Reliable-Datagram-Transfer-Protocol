package Receiver;

import Packet.Ack;

public class Receiver {
    private static final Receiver instance = new Receiver();

    public static Receiver getInstance() {
        return instance;
    }

    private Receiver() {
    }

    private int portNumber;
    private ReceiverBuffer receiverBuffer = new ReceiverBuffer();
    public Ack handShaking(Long initialSequenceNumber) {
        String returnMessage = "Connection has made. Initial sequence number is " + initialSequenceNumber;
        System.out.println(returnMessage);
        return new Ack((int) (initialSequenceNumber+1), receiverBuffer.getWindowSize() - receiverBuffer.getRcvBase());
    }
    public boolean close() {
        System.out.println("Connection has been closed.");
        return false;
    }
}
