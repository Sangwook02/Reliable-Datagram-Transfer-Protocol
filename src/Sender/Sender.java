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
        // TODO: sender window에 여유 공간이 없으면 어떻게 하지?
        return senderBuffer.insert(data);
    }
    // TODO: handShaking 과정에서 오고 갈 정보 설정하기
    public void connectionSetup(Receiver receiver) {
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
                    channel.input(segment);
                }
                else {
                    // 이미 다른 segment의 타이머가 작동중인 상황, 한번도 안 보내진 segment를 보냄.
                    System.out.println("타이머가 이미 설정되어 있지만, 새로운 segment를 보냅니다.");
                    segment.setTimeSent(LocalDateTime.now());
                    this.lastByteSent += segment.getLength();
                    System.out.println("lastByteSent = " + lastByteSent);
                    this.advWindow -= segment.getLength();
                    channel.input(segment);
                }
            }
            // sent but not acked.
            else {
                // 타임아웃된 것들 처리.
            }
        }
    }
    // TODO: selective-N이므로 List로 받아와서 처리하는게 맞음.
    // TODO: get rid of List<WindowElement> and use List<Segment> from SenderWindow.
    public void writeProcess() throws InterruptedException {
        /*
        STEP1: List<WindowElement>에서 아직 보내지지 않은, segments에 없는 element를 Segment로 만들어 segments에 추가.
        STEP2: segments의 segment들을 하나씩 보내고 timer 가동.
         */
        updateSegmentList();
        write();
//        List<WindowElement> elementToWrite = senderWindow.bringUnwrittenData();
//        for (WindowElement obj:elementToWrite) {
//            if (lastByteSent < obj.getSequenceNumber()) {
//                // 아직 한번도 보내지 않은 element
//                // TODO: nextSeqNumber 설정이 잘못됨.
//                this.nextSeqNumber += obj.getLength();
//                this.advWindow -= obj.getLength();
//                this.lastByteSent = nextSeqNumber;
//                if (!timer.isRunning()) { // if timer is not running.
//                    timer.setTimer(nextSeqNumber);
//                    channel.input(segment);
//                    System.out.println(1);
//                }
//                else {
//                    channel.input(segment);
//                    System.out.println(2);
//                }
//            }
//            else {
//                // 이미 보낸 적이 있는 element
//                if (!timer.isRunning()) {
//                    timer.setTimer(Math.toIntExact(obj.getSequenceNumber()));
//                    // TODO: handle the parameter error right below this comment.
//                    //channel.input(segment);
//                    System.out.println(3);
//                }
//            }
//        }
    }

    public Receiver getReceiver() {
        return receiver;
    }
}
