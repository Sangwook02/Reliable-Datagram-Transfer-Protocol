package Sender;

import Packet.Ack;
import Packet.DatagramBuilder;
import Packet.Segment;
import Receiver.Receiver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Sender {
    private static final Sender instance = new Sender();
    private SenderWindow senderWindow = new SenderWindow();
    private Integer advWindow;

    private Timer timer;
    private Receiver receiver;
    private boolean isConnected = false;
    private SegmentBuilder segmentBuilder = SegmentBuilder.getInstance();
    private int nextSeqNumber;

    private DatagramBuilder datagramBuilder = DatagramBuilder.getInstance();

    private Sender() {
    }

    public static Sender getInstance() {
        return instance;
    }
    public void getData(String data) {
        Segment segment = segmentBuilder.makeSegment(data);
        senderWindow.insert(segment);
    }
    // TODO: handShaking 과정에서 오고 갈 정보 설정하기
    public boolean connectionSetup(Receiver receiver) {
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
        return isConnected;
    }

    public boolean connectionClose() {
        boolean isConnected =  receiver.close();
        receiver = null;
        return isConnected;
    }

    public void acked(Ack ack) {
        this.advWindow = ack.getW();
        // TODO: y에 대한 처리
    }
}
