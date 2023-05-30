package Sender;

import Channel.Channel;
import Packet.Ack;
import Packet.Segment;
import Packet.SegmentBuilder;
import Packet.WindowElement;
import Receiver.ReceiverTransport;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class SenderTransport {

    private int advWindow;
    private int nextSeqNumber = 0;
    private int lastByteSent = -1;
    private Timer timer = Timer.getInstance();
    private ReceiverTransport receiverTransport;
    private boolean isConnected = false;

    private static final SenderTransport instance = new SenderTransport();
    private SenderBuffer senderBuffer = new SenderBuffer();
    private SegmentBuilder segmentBuilder = SegmentBuilder.getInstance();
    private Channel channel = Channel.getInstance();

    private SenderTransport() {
    }

    public static SenderTransport getInstance() {
        return instance;
    }

    public boolean getData(int data) throws InterruptedException {
        System.out.println(data+" 크기의 데이터를 window에 삽입합니다.");
        return senderBuffer.insert(data, lastByteSent);
    }

    public SenderBuffer getSenderBuffer() {
        return senderBuffer;
    }

    public void connectionSetup(ReceiverTransport receiverTransport) throws FileNotFoundException {
        String resource = "config/RDTP.properties";
        Properties properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("can not open configuration file");
        }
        this.advWindow = receiverTransport.handShaking(Long.valueOf(properties.getProperty("sender_init_seq_no"))).getW();
        this.isConnected = true;
        this.receiverTransport = receiverTransport;
    }

    public ReceiverTransport getReceiver() {
        return receiverTransport;
    }
    public String connectionClose() {
        boolean isConnected =  receiverTransport.close();
        receiverTransport = null;
        return "close";
    }

    public void acked(Ack ack) {
        this.advWindow = ack.getW();
        int y = ack.getY();
        senderBuffer.sliding(y);
        senderBuffer.printBuffer("acked", lastByteSent);
        if (lastByteSent > y) {
            timer.updateTimer(y, senderBuffer.getWindow());
        } else if (lastByteSent < y){
            timer.resetRunning();
        }
    }

    public void writeProcess() throws InterruptedException {
        if (senderBuffer.getWindow().size() != 0) { // only when window is not null
            ArrayList<WindowElement> copy = new ArrayList<>();
            for (WindowElement e: senderBuffer.getWindow()) {
                if (e.getSequenceNumber() == null) {
                    copy.add(e);
                }
            }
            Iterator<WindowElement> iterator = copy.iterator();
            while (iterator.hasNext()){
                WindowElement element = iterator.next();
                if (element.getSequenceNumber() == null && advWindow >= element.getLength()) {
                    element.setSequenceNumber((long) nextSeqNumber);
                    nextSeqNumber += element.getLength();
                    if (!timer.isRunning()) {
                        LocalDateTime now = LocalDateTime.now();
                        timer.setTimer(Math.toIntExact(element.getSequenceNumber()), now);
                        element.setTimeSent(now);
                        this.lastByteSent += element.getLength();
                        this.advWindow -= element.getLength();
                        Segment segment = segmentBuilder.makeSegment(element.getLength(), element.getSequenceNumber());
                        System.out.println("data sent now = " + now);
                        channel.senderToReceiver(this, receiverTransport, segment);
                    }
                    else { // timer is running but this element is never sent.
                        element.setTimeSent(LocalDateTime.now());
                        this.lastByteSent += element.getLength();
                        this.advWindow -= element.getLength();
                        Segment segment = segmentBuilder.makeSegment(element.getLength(), element.getSequenceNumber());
                        LocalDateTime now = LocalDateTime.now();
                        System.out.println("data sent now = " + now);
                        channel.senderToReceiver(this, receiverTransport, segment);
                    }
                    senderBuffer.printBuffer("Sender sent new segment", lastByteSent);
                } else if(advWindow < element.getLength()) {
                    return;
                }
            }
        }
    }

    public void checkTimeOut() throws InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        if (!timer.isRunning()) {
            return;
        }
        if (timer.getExpireAt().isBefore(now)) { // timeout occured
            senderBuffer.printBuffer("timeout occured", lastByteSent);
            ArrayList<WindowElement> copy = new ArrayList<>();
            copy.addAll(senderBuffer.getWindow());
            Iterator<WindowElement> iterator = copy.iterator();
            while (iterator.hasNext()) {
                WindowElement element = iterator.next();
                if (element.isAcked() == true) {
                    continue;
                }
                else {
                    timer.setTimer(Math.toIntExact(element.getSequenceNumber()), now);
                    Segment segment = segmentBuilder.makeSegment(element.getLength(), element.getSequenceNumber());
                    System.out.println("data sent now = " + now);
                    channel.senderToReceiver(this, receiverTransport, segment);
                    System.out.println("sent again");
                    break;
                }
            }
        }
    }
}
