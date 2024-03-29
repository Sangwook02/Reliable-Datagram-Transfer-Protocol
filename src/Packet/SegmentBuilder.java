package Packet;

public class SegmentBuilder {
    private static final SegmentBuilder instance = new SegmentBuilder();

    private SegmentBuilder() {
    }

    public static SegmentBuilder getInstance() {
        return instance;
    }

    public Segment makeSegment(int data, Long sequenceNumber) {
        Segment segment = new Segment(data, sequenceNumber);
        return segment;
    }
}