package com.airtribe.ridewise.model;

public class Driver {
    private final String id;
    private final String name;
    private Location currentLocation;
    private  final VehicleType vehicleType;
    private boolean isAvailable;
    private int completedRides;

    public Driver(String id, String name, Location currentLocation, VehicleType vehicleType) {
        this.id = id;
        this.name = name;
        this.currentLocation = currentLocation;
        this.vehicleType = vehicleType;
        this.isAvailable = true;
        this.completedRides = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public int getCompletedRides() {
        return completedRides;
    }

    public void incrementCompletedRides() {
        this.completedRides++;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", currentLocation=" + currentLocation +
                ", vehicleType=" + vehicleType +
                ", isAvailable=" + isAvailable +
                ", completedRides=" + completedRides +
                '}';
    }
}
