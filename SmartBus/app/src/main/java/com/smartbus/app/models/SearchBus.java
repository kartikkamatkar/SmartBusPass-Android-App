package com.smartbus.app.models;

public class SearchBus {
    private String name;
    private String time;
    private String route;

    public SearchBus(String name, String time, String route) {
        this.name = name;
        this.time = time;
        this.route = route;
    }

    public String getName() { return name; }
    public String getTime() { return time; }
    public String getRoute() { return route; }
}
