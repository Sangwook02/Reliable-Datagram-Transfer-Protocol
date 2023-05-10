package Packet;

import java.io.*;
import java.util.Properties;
public class Segment {
    private int srcPortNumber;

    private int destPortNumber;
    private int length;
    // checksum은 생략.
    private String applicationData;


    public int getSrcPortNumber() {
        return srcPortNumber;
    }

    public int getDestPortNumber() {
        return destPortNumber;
    }

    public int getLength() {
        return length;
    }
    public String getApplicationData() {
        return applicationData;
    }
    public Segment(String applicationData) {
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
        this.applicationData = applicationData;
        this.length = applicationData.getBytes().length;
    }
}
