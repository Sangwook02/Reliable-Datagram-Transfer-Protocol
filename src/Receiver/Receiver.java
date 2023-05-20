package Receiver;

import Packet.Ack;
import Packet.Segment;

public class Receiver {
    private int lastByteRcvd;
    private static final Receiver instance = new Receiver();

    public static Receiver getInstance() {
        return instance;
    }

    private Receiver() {
        this.lastByteRcvd = -1;
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

    public void receive(Segment segment) {
        System.out.println("=====receiver====");
        System.out.println("lastByteRcvd = " + lastByteRcvd);
        System.out.println("segment의 seqNo = " + segment.getSequenceNumber());
        System.out.println("");
        if (lastByteRcvd + 1 == segment.getSequenceNumber()) {
            this.lastByteRcvd += segment.getLength();
            receiverBuffer.insert(segment);
            Ack ack = new Ack((int) (segment.getSequenceNumber()+segment.getLength()), receiverBuffer.getRcvBase()+ receiverBuffer.getWindowSize()-lastByteRcvd-1);
            System.out.println("ack = " + ack.getW());
            // TODO: send ACK
        }
        else {
            System.out.println("cannot receive because it is not in-order");
            Ack ack = new Ack((int) (receiverBuffer.getWindow().get(-1).getSequenceNumber()+ receiverBuffer.getWindow().get(-1).getLength()), receiverBuffer.getRcvBase()+ receiverBuffer.getWindowSize()-lastByteRcvd-1);
        }
    }
}
