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

    public void updateSegmentList() {
        List<WindowElement> elementToWrite = senderBuffer.bringUnAckedData();
        for(WindowElement obj:elementToWrite) {
            // segment가 생성되지 않은 windowElement라면 Segment로 만들어 segments 리스트에 추가.
            if(!obj.isSegmentCreated()) {
                obj.setSequenceNumber((long) nextSeqNumber);
                this.nextSeqNumber += obj.getLength();
                Segment segment = segmentBuilder.makeSegment(obj.getLength(),obj.getSequenceNumber());
                obj.setSegmentCreated(true);
                segments.add(segment);
            }
        }
    }
    public void write() throws InterruptedException {
        for (Segment segment:segments) {
            // break if advWindow's spare space is smaller than segment's length
            if (segment.getLength() > advWindow) {
                break;
            }
            // not sent yet.
            if (lastByteSent <  segment.getSequenceNumber() && segment.getTimeSent() == null) {
                if (!timer.isRunning()){
                    // set timer and update timeSent field of segment.
                    LocalDateTime now = LocalDateTime.now();
                    timer.setTimer(Math.toIntExact(segment.getSequenceNumber()), now);
                    segment.setTimeSent(now);
                    // TODO: delete debug purpose prt.
                    System.out.println(segment+"를 전송합니다.");
                    System.out.println("");
                    // lastByteSent, advWindow와 channel.input()은 세트
                    this.lastByteSent += segment.getLength();
                    System.out.println("lastByteSent = " + lastByteSent);
                    this.advWindow -= segment.getLength();
                    System.out.println("receiver1234 = " + receiver);
                    channel.senderToReceiver(this, receiver, segment);
                }
                else {
                    // 이미 다른 segment의 타이머가 작동중인 상황, 한번도 안 보내진 segment를 보냄.
                    System.out.println("타이머가 이미 설정되어 있지만, 새로운 segment를 보냅니다.");
                    segment.setTimeSent(LocalDateTime.now());
                    this.lastByteSent += segment.getLength();
                    System.out.println("lastByteSent = " + lastByteSent);
                    this.advWindow -= segment.getLength();
                    channel.senderToReceiver(this, receiver, segment);
                }
            }
            // sent but not acked.
            else {
                // 타임아웃된 것들 처리.
            }
        }
    }

    public void writeProcess() throws InterruptedException {
        /*
        STEP1: List<WindowElement>에서 아직 보내지지 않은, segments에 없는 element를 Segment로 만들어 segments에 추가.
        STEP2: segments의 segment들을 하나씩 보내고 timer 가동.
         */
        updateSegmentList();
        write();
//        }
    }

    public Receiver getReceiver() {
        return receiver;
    }
}
