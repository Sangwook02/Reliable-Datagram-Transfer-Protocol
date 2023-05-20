package Sender;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Properties;

public class Timer {
    private int sequenceNumber;
    private LocalDateTime expireAt;
    private int timeoutValue;
    private boolean isRunning;
    private static final Timer instance = new Timer();
    public static Timer getInstance() {
        return instance;
    }
    private Timer() {
        String resource = "config/RDTP.properties";
        Properties properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.println("can not open configuration file");
        }
        this.timeoutValue = Integer.parseInt(properties.getProperty("sender_timeout_value"));
    }

    public boolean isRunning() {
        System.out.println("expireAt = " + expireAt);
        if(isRunning) { // 만료 여부
            return LocalDateTime.now().isAfter(expireAt);
        }
        return false;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void setTimer(int sequenceNumber, LocalDateTime startAt){
        this.sequenceNumber = sequenceNumber;
        this.isRunning = true;
        this.expireAt = startAt.plusSeconds(timeoutValue);
    }
}
