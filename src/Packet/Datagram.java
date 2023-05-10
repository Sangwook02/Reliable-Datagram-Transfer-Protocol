package Packet;

import Sender.SegmentBuilder;

public class Datagram {
    private DatagramHeader datagramHeader;
    private Segment datagramData;

    public Datagram(DatagramHeader datagramHeader, Segment datagramData) {
        this.datagramHeader = datagramHeader;
        this.datagramData = datagramData;
    }
}
