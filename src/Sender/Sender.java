package Sender;

import Channel.Channel;
import Packet.Ack;
import Packet.Segment;
import Receiver.Receiver;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class Sender {

    private int advWindow;
    private int nextSeqNumber = 0;
    private int lastByteSent = -1;
    private Timer timer = Timer.getInstance();
    private Receiver receiver;
    private boolean isConnected = false;

    private static final Sender instance = new Sender();
    private SenderBuffer senderBuffer = new SenderBuffer();
    private SegmentBuilder segmentBuilder = SegmentBuilder.getInstance();
    private Channel channel = Channel.getInstance();

    private Sender() {
    }

    public static Sender getInstance() {
        return instance;
    }

    public boolean getData(int data) throws InterruptedException {
        System.out.println(data+" 크기의 데이터를 window에 삽입합니다.");
        return senderBuffer.insert(data);
    }

    public void connectionSetup(Receiver receiver) throws FileNotFoundException {
        String resource = "config/RDTP.properties";
        Properties properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("can not open configuration file");
        }
        this.advWindow = receiver.handShaking(Long.valueOf(properties.getProperty("sender_init_seq_no"))).getW();
        this.isConnected = true;
        this.receiver = receiver;
    }

    public Receiver getReceiver() {
        return receiver;
    }
    public String connectionClose() {
        boolean isConnected =  receiver.close();
        receiver = null;
        return "close";
    }

    public void acked(Ack ack) {
        this.advWindow = ack.getW();
        int y = ack.getY();
        senderBuffer.sliding(y);
        senderBuffer.printBuffer("acked");
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
                copy.add(e);
            }
            Iterator<WindowElement> iterator = copy.iterator();
            while (iterator.hasNext()){
                WindowElement element = iterator.next();
                if (element.getSequenceNumber() == null) {
                    element.setSequenceNumber((long) nextSeqNumber);
                    nextSeqNumber += element.getLength();
                    if (!timer.isRunning()) {
                        LocalDateTime now = LocalDateTime.now();
                        timer.setTimer(Math.toIntExact(element.getSequenceNumber()), now);
                        element.setTimeSent(now);
                        this.lastByteSent += element.getLength();
                        this.advWindow -= element.getLength();
                        Segment segment = segmentBuilder.makeSegment(element.getLength(), element.getSequenceNumber());
                        channel.senderToReceiver(this, receiver, segment);
                    }
                    else { // timer is running but this element is never sent.
                        element.setTimeSent(LocalDateTime.now());
                        this.lastByteSent += element.getLength();
                        System.out.println("lastByteSent = " + lastByteSent);
                        this.advWindow -= element.getLength();
                        Segment segment = segmentBuilder.makeSegment(element.getLength(), element.getSequenceNumber());
                        channel.senderToReceiver(this, receiver, segment);
                    }
                    senderBuffer.printBuffer("Sender sent new segment");
                }
            }
        }
    }
    public void checkTimeOut() throws InterruptedException {
        LocalDateTime now = LocalDateTime.now();
        if (!timer.isRunning()) {
            return;
        }
        if (timer.getExpireAt().isAfter(now)) { // timeout occured
            senderBuffer.printBuffer("timeout occured");
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
                    break;
                }
            }
        }
    }
}
