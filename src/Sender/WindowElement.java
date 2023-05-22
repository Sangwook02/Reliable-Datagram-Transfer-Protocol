package Sender;

import java.time.LocalDateTime;

public class WindowElement {
    private int length;
    private Long sequenceNumber;
    private boolean acked = false;
    private boolean isSegmentCreated = false;
    private LocalDateTime timeSent;

    public boolean isSegmentCreated() {
        return isSegmentCreated;
    }

    public void setSegmentCreated(boolean segmentCreated) {
        this.isSegmentCreated = segmentCreated;
    }

    public WindowElement(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
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

}
