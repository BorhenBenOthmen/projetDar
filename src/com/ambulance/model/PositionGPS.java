package com.ambulance.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class PositionGPS implements Serializable {
    private static final long serialVersionUID = 1L;

    private double latitude;
    private double longitude;
    private LocalDateTime timestamp;

    public PositionGPS(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = LocalDateTime.now();
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "PositionGPS{" +
                "lat=" + latitude +
                ", lon=" + longitude +
                ", time=" + timestamp +
                '}';
    }
}