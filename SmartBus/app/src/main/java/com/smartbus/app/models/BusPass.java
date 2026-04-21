package com.smartbus.app.models;

public class BusPass {
    private final int id;
    private final String name;
    private final String route;
    private final String validity;
    private final String createdAt;

    public BusPass(int id, String name, String route, String validity, String createdAt) {
        this.id = id;
        this.name = name;
        this.route = route;
        this.validity = validity;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRoute() {
        return route;
    }

    public String getValidity() {
        return validity;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
