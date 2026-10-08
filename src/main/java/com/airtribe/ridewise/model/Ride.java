package com.airtribe.ridewise.model;

public class Ride {
    private final String id;
    private  Driver driver;
    private final Rider rider;
    private RideStatus  rideStatus;
    private final Location pickupLocation;
    private final Location dropLocation;
    private final double distance;
    private FareReceipt fareReceipt;

    public Ride(String id, Rider rider ,Location pickupLocation, Location dropLocation) {
        this.id = id;
        this.rider = rider;
        this.pickupLocation = pickupLocation;
        this.dropLocation =dropLocation;
        this.distance = pickupLocation.distanceTo(dropLocation);
        this.rideStatus= RideStatus.REQUESTED;
    }

    public void assignDriver(Driver driver) {
        transitionTo(RideStatus.ASSIGNED);
        this.driver = driver;
    }

    public void completeRide(FareReceipt fareReceipt) {
        transitionTo(RideStatus.COMPLETED);
        this.fareReceipt = fareReceipt;
    }

    public void cancelRide() {
        transitionTo(RideStatus.CANCELLED);
    }

    private void transitionTo(RideStatus next) {
        if (!rideStatus.canTransitionTo(next)) {
            throw new IllegalStateException(
                    "Ride " + id + ": cannot go from " + rideStatus + " to " + next);
        }
        this.rideStatus = next;
    }

    public String getId() {
        return id;
    }

    public RideStatus getRideStatus() {
        return rideStatus;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public Location getDropLocation() {
        return dropLocation;
    }

    public double getDistance() {
        return distance;
    }

    public FareReceipt getFareReceipt() {
        return fareReceipt;
    }
    // Demeter-friendly: callers never reach through Ride to Rider/Driver
    public String getRiderId() { return rider.getId(); }
    public String getRiderName() { return rider.getName(); }
    public boolean hasDriver() { return driver != null; }
    public String getDriverId() { return driver.getId(); }
    public String getDriverName() { return driver.getName(); }

    @Override
    public String toString() {
        return "Ride{" +
                "id='" + id + '\'' +
                ", driver=" + driver +
                ", rider=" + rider +
                ", rideStatus=" + rideStatus +
                ", pickupLocation=" + pickupLocation +
                ", dropLocation=" + dropLocation +
                ", distance=" + distance +
                ", fareReceipt=" + fareReceipt +
                '}';
    }
}