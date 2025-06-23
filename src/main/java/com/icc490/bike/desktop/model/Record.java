package com.icc490.bike.desktop.model;

import java.time.Instant;

public class Record {
    private Long id;
    private String studentId;
    private String studentName;
    private String bicycleDescription;
    private Instant checkIn;
    private Instant checkOut;

    public Record() {
    }

    public Record(Long id, String studentId, String studentName, String bicycleDescription, Instant checkIn, Instant  checkOut) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.bicycleDescription = bicycleDescription;
        this.checkIn  = checkIn;
        this.checkOut = checkOut;
    }

    //Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getBicycleDescription() {
        return bicycleDescription;
    }

    public void setBicycleDescription(String bicycleDescription) {
        this.bicycleDescription = bicycleDescription;
    }

    public Instant getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(Instant checkIn) {
        this.checkIn = checkIn;
    }

    public Instant getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(Instant checkOut) {
        this.checkOut = checkOut;
    }

    @Override
    public String toString() {
        return "ID: " + id +
                ", Estudiante: " + studentName +
                "(" + studentId + ")" +
                ", Bicicleta: '" + bicycleDescription + '\'' +
                ", Check-in: " + (checkIn != null ? checkIn.toString() : "N/A") +
                ", Check-out: " + (checkOut != null ? checkOut.toString(): "N/A");
    }
}

