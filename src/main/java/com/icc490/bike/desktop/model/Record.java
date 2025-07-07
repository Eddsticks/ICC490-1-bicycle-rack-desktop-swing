package com.icc490.bike.desktop.model;

import java.time.Instant; // Necesario para Instant

public class Record {
    private Long id;
    private String studentId;
    private String studentName;
    private String bicycleDescription;
    private Instant checkIn;
    private Instant checkOut;
    private Rack rack;
    private Long hook;

    public Record() {
    }

    public Record(Long id, String studentId, String studentName, String bicycleDescription,
                  Instant checkIn, Instant checkOut, Rack rack, Long hook) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.bicycleDescription = bicycleDescription;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.rack = rack;
        this.hook = hook;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getBicycleDescription() {
        return bicycleDescription;
    }

    public Instant getCheckIn() {
        return checkIn;
    }

    public Instant getCheckOut() {
        return checkOut;
    }

    public Rack getRack() {
        return rack;
    }

    public Long getHook() {
        return hook;
    }

    // Setters

    public void setId(Long id) {
        this.id = id;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setBicycleDescription(String bicycleDescription) {
        this.bicycleDescription = bicycleDescription;
    }

    public void setCheckIn(Instant checkIn) {
        this.checkIn = checkIn;
    }

    public void setCheckOut(Instant checkOut) {
        this.checkOut = checkOut;
    }

    public void setRack(Rack rack) {
        this.rack = rack;
    }

    public void setHook(Long hook) {
        this.hook = hook;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", bicycleDescription='" + bicycleDescription + '\'' +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", rack=" + (rack != null ? rack.getId() : "null") + // Mostrar solo ID del rack
                ", hook=" + hook +
                '}';
    }
}
