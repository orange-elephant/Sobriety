package com.orangeelephant.sobriety.counter;

public class Reason {
    private final int reasonId;
    private String reason;

    public Reason(int reasonId, String reason) {
        this.reasonId = reasonId;
        this.reason = reason;
    }

    public int getReasonId() {
        return reasonId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
