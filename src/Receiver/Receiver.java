package Receiver;

import Channel.Channel;
import Packet.Ack;
import Packet.Segment;
import Sender.Sender;

import java.io.FileNotFoundException;

public class Receiver {
    private int lastByteRcvd;
    private static final Receiver instance;
    private Channel channel;


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

    public void setChannel(Channel channel) {
        this.channel = channel;
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
        System.out.println("");
        receiverUpperApplication.readScenarioFile();
        receiverUpperApplication.setConnection(true);
        Thread receiverRead = new Thread() {
            @Override
            public void run() {
                try {
                    receiverUpperApplication.read(Receiver.this);
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

    public void receive(Sender sender, Segment segment) throws InterruptedException {
        System.out.println("receiverBuffer.getWindow() = " + receiverBuffer.getWindow());
        if (lastByteRcvd + 1 == segment.getSequenceNumber()) {
            this.lastByteRcvd += segment.getLength();
            receiverBuffer.insert(segment);
            Ack ack = new Ack((int) (segment.getSequenceNumber()+segment.getLength()), receiverBuffer.getRcvBase()+ receiverBuffer.getWindowSize()-lastByteRcvd-1);
            channel.receiverToSender(ack, sender);
        }
        else {
            System.out.println("cannot receive because it is not in-order");
            Ack ack = new Ack((int) (receiverBuffer.getWindow().get(receiverBuffer.getWindow().size()-1).getSequenceNumber()+ receiverBuffer.getWindow().get(receiverBuffer.getWindow().size()-1).getLength()), receiverBuffer.getRcvBase()+ receiverBuffer.getWindowSize()-lastByteRcvd-1);
            channel.receiverToSender(ack,sender);
        }
    }
}
