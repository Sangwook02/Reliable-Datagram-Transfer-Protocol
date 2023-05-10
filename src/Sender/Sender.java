package Sender;

import Packet.DatagramBuilder;
import Packet.Segment;

public class Sender {
    private static  final Sender instance = new Sender();
    private SenderWindow senderWindow = new SenderWindow();

    private Timer timer;
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
}
