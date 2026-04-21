package com.smartbus.app.models;

public class TripOption {
    private String title;
    private String time;
    private String price;
    private String type; // fastest, cheapest, comfort
    private String description;

    public TripOption(String title, String time, String price, String type, String description) {
        this.title = title;
        this.time = time;
        this.price = price;
        this.type = type;
        this.description = description;
    }

    public String getTitle() { return title; }
    public String getTime() { return time; }
    public String getPrice() { return price; }
    public String getType() { return type; }
    public String getDescription() { return description; }
}
