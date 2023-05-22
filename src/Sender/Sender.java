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
    // necessary to distinguish whether sent or not
    private List<Segment> segments = new ArrayList<>();

    /*
    connection Setup을 위한 receiver와 isConnected.
     */
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

    public boolean getData(int data) {
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

    public String  connectionClose() {
        boolean isConnected =  receiver.close();
        receiver = null;
        return "close";
    }

    public void acked(Ack ack) {
        this.advWindow = ack.getW();
        int y = ack.getY();
        senderBuffer.sliding(y);
        // TODO: y에 대한 처리
    }
    public void writeProcess() throws InterruptedException {
        /*
        STEP1: List<WindowElement>에서 아직 보내지지 않은, segments에 없는 element를 Segment로 만들어 segments에 추가.
        STEP2: segments의 segment들을 하나씩 보내고 timer 가동.
         */
        // only when window is not null
        if (senderBuffer.getWindow().size() != 0) {
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
                    else {
                        // timer is running but this element is never sent.
                        element.setTimeSent(LocalDateTime.now());
                        this.lastByteSent += element.getLength();
                        System.out.println("lastByteSent = " + lastByteSent);
                        this.advWindow -= element.getLength();
                        Segment segment = segmentBuilder.makeSegment(element.getLength(), element.getSequenceNumber());
                        channel.senderToReceiver(this, receiver, segment);
                    }

                } else {
                    if (senderBuffer.getSendBase() <= element.getSequenceNumber()) {
                        // already sent but not acked yet.
                    }
                }
            }
        }
    }

    public Receiver getReceiver() {
        return receiver;
    }
}
