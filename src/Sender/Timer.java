package Sender;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Properties;

public class Timer {
    private Integer sequenceNumber;
    private LocalDateTime expireAt;
    private int timeoutValue;
    private boolean isRunning;

    public boolean isRunning() {
        if(isRunning) {
            return LocalDateTime.now().isAfter(expireAt);
        }
        return false;
    }

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

    public void resetRunning() {
        this.sequenceNumber = null;
        this.isRunning = false;
        this.expireAt = null;
    }

    public LocalDateTime getExpireAt() {
        return expireAt;
    }
    public void setTimer(int sequenceNumber, LocalDateTime startAt){
        this.sequenceNumber = sequenceNumber;
        this.isRunning = true;
        this.expireAt = startAt.plusSeconds(timeoutValue);
    }

    public void updateTimer(int y, ArrayList<WindowElement> windowElements) {
        WindowElement timerElement = null;
        for(WindowElement element:windowElements) {
            if (element.getSequenceNumber() == y) {
                timerElement = element;
                break;
            }
        }
        this.sequenceNumber = Math.toIntExact(timerElement.getSequenceNumber());
        this.isRunning = true;
        this.expireAt = timerElement.getTimeSent().plusSeconds(timeoutValue);
    }
}
