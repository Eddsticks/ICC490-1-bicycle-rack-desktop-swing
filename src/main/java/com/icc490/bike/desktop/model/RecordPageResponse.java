package com.icc490.bike.desktop.model;

import java.util.List;

public class RecordPageResponse {
    private List<Record> records;
    private String nextPageToken;

    public RecordPageResponse() {
    }

    public RecordPageResponse(List<Record> records, String nextPageToken) {
        this.records = records;
        this.nextPageToken = nextPageToken;
    }

    // Getters
    public List<Record> getRecords() {
        return records;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    // Setters
    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    @Override
    public String toString() {
        return "RecordPageResponse{" +
                "records=" + records.size() + " records" +
                ", nextPageToken='" + nextPageToken + '\'' +
                '}';
    }
}
