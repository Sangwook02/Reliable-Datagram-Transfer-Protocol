package Receiver;

import Channel.Channel;
import Packet.Ack;
import Packet.Segment;
import Sender.SenderTransport;

import java.io.FileNotFoundException;

public class ReceiverTransport {
    private int lastByteRcvd;
    private static final ReceiverTransport instance;
    private Channel channel;

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    private ReceiverUpperApplication receiverUpperApplication = ReceiverUpperApplication.getInstance();

    static {
        try {
            instance = new ReceiverTransport();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public static ReceiverTransport getInstance() {
        return instance;
    }
    private ReceiverBuffer receiverBuffer = ReceiverBuffer.getInstance();
    private ReceiverTransport() throws FileNotFoundException, InterruptedException {
        this.lastByteRcvd = -1;
    }

    public Ack handShaking(Long initialSequenceNumber) throws FileNotFoundException {
        String returnMessage = "Connection has made. Initial sequence number is " + initialSequenceNumber;
        System.out.println(returnMessage);
        System.out.println("");
        receiverUpperApplication.readScenarioFile();
        receiverUpperApplication.setConnection(true);
        Thread receiverRead = new Thread() {
            @Override
            public void run() {
                try {
                    receiverUpperApplication.read(ReceiverTransport.this);
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

    public void receive(SenderTransport senderTransport, Segment segment) throws InterruptedException {
        System.out.println("receiverBuffer.getWindow() = " + receiverBuffer.getWindow());
        if (lastByteRcvd + 1 == segment.getSequenceNumber()) {
            this.lastByteRcvd += segment.getLength();
            receiverBuffer.insert(segment);
            Ack ack = new Ack((int) (segment.getSequenceNumber()+segment.getLength()), receiverBuffer.getRcvBase()+ receiverBuffer.getWindowSize()-lastByteRcvd-1);
            channel.receiverToSender(ack, senderTransport);
        }
        else {
            System.out.println("cannot receive because it is not in-order");
            Ack ack = new Ack((int) (receiverBuffer.getWindow().get(receiverBuffer.getWindow().size()-1).getSequenceNumber()+ receiverBuffer.getWindow().get(receiverBuffer.getWindow().size()-1).getLength()), receiverBuffer.getRcvBase()+ receiverBuffer.getWindowSize()-lastByteRcvd-1);
            channel.receiverToSender(ack, senderTransport);
        }
    }
}
