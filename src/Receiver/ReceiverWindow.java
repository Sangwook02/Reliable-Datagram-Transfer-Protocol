package Receiver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ReceiverWindow {
    private int windowSize;
    private int rcvBase;
    private int lastByteRcvd;

    public ReceiverWindow() {
        String resource = "config/RDTP.properties";
        Properties properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("can not open configuration file");
        }
        this.windowSize = Integer.parseInt(properties.getProperty("receiver_window_size"));
        this.rcvBase = 0;
        this.lastByteRcvd = 0;

    }

    public int getWindowSize() {
        return windowSize;
    }

    public int getRcvBase() {
        return rcvBase;
    }
    // TODO: upper layer에서 data 읽어가는 경우의 작동
}
