package Sender;

import java.time.LocalDateTime;

public class WindowElement {
    private int length;
    private Long sequenceNumber;
    private boolean acked = false;
    private boolean isSegmentCreated = false;
    private LocalDateTime timeSent;

    public int getLength() {
        return length;
    }

    public boolean isAcked() {
        return acked;
    }

    public void setAcked(boolean acked) {
        this.acked = acked;
    }

    public Long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public LocalDateTime getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(LocalDateTime timeSent) {
        this.timeSent = timeSent;
    }

    public WindowElement(int length) {
        this.length = length;
    }
}
