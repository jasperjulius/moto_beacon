package com.example.motobeacon;

public class LogItem {
    private String number;
    private String time;
    private String action;

    public LogItem(String number, String time, String action) {
        this.number = number;
        this.time = time;
        this.action = action;
    }

    public String getNumber() {
        return number;
    }

    public String getTime() {
        return time;
    }

    public String getAction() {
        return action;
    }
}
