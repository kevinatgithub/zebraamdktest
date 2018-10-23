package com.example.kevin.henobarcodetest;

public class HenoItem {

    private String code = null;
    private String location = null;
    private String remarks = null;
    private String timestamps = null;

    public HenoItem(String code, String location, String remarks, String timestamps) {
        this.code = code;
        this.location = location;
        this.remarks = remarks;
        this.timestamps = timestamps;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(String timestamps) {
        this.timestamps = timestamps;
    }

    @Override
    public String toString() {
        return "Item{" +
                "code='" + code + '\'' +
                ", location='" + location + '\'' +
                ", remarks='" + remarks + '\'' +
                ", timestamps='" + timestamps + '\'' +
                '}';
    }
}
