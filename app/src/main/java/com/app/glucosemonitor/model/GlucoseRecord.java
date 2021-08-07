package com.app.glucosemonitor.model;

import java.util.Date;

public class GlucoseRecord {

    public String user_id;

    public String glucose_level;

    public String date;

    public String time;

    public String notes;

    public Date timestamp;

    public GlucoseRecord() {
    }

    public GlucoseRecord(String user_id, String glucose_level, String date, String time, String notes, Date timestamp) {
        this.user_id = user_id;
        this.glucose_level = glucose_level;
        this.date = date;
        this.time = time;
        this.notes = notes;
        this.timestamp = timestamp;
    }
}
