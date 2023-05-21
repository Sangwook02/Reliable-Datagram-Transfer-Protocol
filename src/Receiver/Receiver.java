package Receiver;

import Packet.Ack;
import Packet.Segment;
import Sender.SegmentBuilder;

import java.io.FileNotFoundException;

public class Receiver {
    private int lastByteRcvd;
    private static final Receiver instance;

    private ReceiverUpperApplication receiverUpperApplication = ReceiverUpperApplication.getInstance();


    static {
        try {
            instance = new Receiver();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Receiver getInstance() {
        return instance;
    }

    private Receiver() throws FileNotFoundException, InterruptedException {
        this.lastByteRcvd = -1;
    }

    private int portNumber;
    private ReceiverBuffer receiverBuffer = ReceiverBuffer.getInstance();

    public Ack handShaking(Long initialSequenceNumber) throws FileNotFoundException {
        String returnMessage = "Connection has made. Initial sequence number is " + initialSequenceNumber;
        System.out.println(returnMessage);
        receiverUpperApplication.readScenarioFile();
        receiverUpperApplication.setConnection(true);
        Thread receiverRead = new Thread() {
            @Override
            public void run() {
                try {
                    receiverUpperApplication.read();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        receiverRead.start();
        return new Ack((int) (initialSequenceNumber+1), receiverBuffer.getWindowSize() - receiverBuffer.getRcvBase());
    }
    public boolean close() {
        System.out.println("Connection has been closed.");
        receiverUpperApplication.setConnection(false);
        System.out.println("receiverUpperApplication read these = " + receiverUpperApplication.getDataLength());
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
        }
        else {
            System.out.println("cannot receive because it is not in-order");
            Ack ack = new Ack((int) (receiverBuffer.getWindow().getLast().getSequenceNumber()+ receiverBuffer.getWindow().getLast().getLength()), receiverBuffer.getRcvBase()+ receiverBuffer.getWindowSize()-lastByteRcvd-1);
        }
    }
}
