package com.icc490.bike.desktop.model;

public class RecordRequest {
    private String studentId;
    private String studentName;
    private String bicycleDescription;
    private Long rackId; // Nuevo campo
    private Long hook;   // Nuevo campo

    public RecordRequest(String studentId, String studentName, String bicycleDescription, Long rackId, Long hook) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.bicycleDescription = bicycleDescription;
        this.rackId = rackId;
        this.hook = hook;
    }

    public RecordRequest() {
    }

    // Getters
    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getBicycleDescription() {
        return bicycleDescription;
    }

    public Long getRackId() {
        return rackId;
    }

    public Long getHook() {
        return hook;
    }

    // Setters
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setBicycleDescription(String bicycleDescription) {
        this.bicycleDescription = bicycleDescription;
    }

    public void setRackId(Long rackId) {
        this.rackId = rackId;
    }

    public void setHook(Long hook) {
        this.hook = hook;
    }

    @Override
    public String toString() {
        return "RecordRequest{" +
                "studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", bicycleDescription='" + bicycleDescription + '\'' +
                ", rackId=" + rackId +
                ", hook=" + hook +
                '}';
    }
}