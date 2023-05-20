package Packet;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Properties;
public class Segment {
    // checksum은 생략.
    private int srcPortNumber;

    private int destPortNumber;
    private int length;
    private Long sequenceNumber;
    private LocalDateTime timeSent;

    public Segment(int dataLength, Long sequenceNumber) {
        String resource = "config/RDTP.properties";
        Properties properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("can not open configuration file");
        }

        this.srcPortNumber= Integer.parseInt(properties.getProperty("sender_port_number"));
        this.destPortNumber = Integer.parseInt(properties.getProperty("receiver_port_number"));
        this.length = dataLength;
        this.sequenceNumber = sequenceNumber;
    }




    public Long getSequenceNumber() {
        return sequenceNumber;
    }

    public int getLength() {
        return length;
    }

    public LocalDateTime getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(LocalDateTime timeSent) {
        this.timeSent = timeSent;
    }
}
