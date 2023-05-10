package Sender;


import Packet.Segment;

public class SegmentBuilder {
    private static final SegmentBuilder instance = new SegmentBuilder();

    private SegmentBuilder() {
    }

    public static SegmentBuilder getInstance() {
        return instance;
    }

    public Segment makeSegment(String data) {
        Segment segment = new Segment(data);
        return segment;
    }
}
