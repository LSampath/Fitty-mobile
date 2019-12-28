package com.example.fitty.models;

public class RunningSession {

    private long startTime;
    private long endTime;
    private double distance;

    public RunningSession(long startTime, long endTime, double distance) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.distance = distance;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public double getDistance() {
        return distance;
    }

    public double getAvgSpeed() {
        double duration = (endTime - startTime) / (1000 * 60 * 60);
        if (duration == 0) {
            return 0;
        } else {
            return (this.distance/duration);
        }

     }
}
