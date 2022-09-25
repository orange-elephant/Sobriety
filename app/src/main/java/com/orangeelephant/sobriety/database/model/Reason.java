package com.orangeelephant.sobriety.database.model;

import org.json.JSONException;
import org.json.JSONObject;

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

    public JSONObject toJson() throws JSONException {
        JSONObject jsonReasons = new JSONObject();

        jsonReasons.put("ReasonId", reasonId);
        jsonReasons.put("Reason", reason);

        return jsonReasons;
    }
}
