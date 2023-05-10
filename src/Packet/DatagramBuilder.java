package Packet;

public class DatagramBuilder {
    private static final DatagramBuilder instance = new DatagramBuilder();

    private DatagramBuilder() {
    }
    public static DatagramBuilder getInstance() {
        return instance;
    }
    public Datagram makeDatagram(DatagramHeader header, Segment segment) {
        Datagram datagram = new Datagram(header, segment);
        return datagram;
    }
}
