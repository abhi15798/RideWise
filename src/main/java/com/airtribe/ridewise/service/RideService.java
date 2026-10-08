package com.airtribe.ridewise.service;

import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.FareReceipt;
import com.airtribe.ridewise.model.Location;
import com.airtribe.ridewise.model.Ride;
import com.airtribe.ridewise.model.Rider;
import com.airtribe.ridewise.strategy.FareStrategy;
import com.airtribe.ridewise.strategy.RideMatchingStrategy;
import com.airtribe.ridewise.util.IdGenerator;
import com.airtribe.ridewise.util.IdPrefix;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RideService {
    private final Map<String, Ride> rides = new LinkedHashMap<>();
    private final IdGenerator idGenerator = IdGenerator.getInstance();

    private final RiderService riderService;
    private final DriverService driverService;
    private final RideMatchingStrategy matchingStrategy; // depends on interfaces (DIP)
    private final FareStrategy fareStrategy;

    public RideService(RiderService riderService,
                       DriverService driverService,
                       RideMatchingStrategy matchingStrategy,
                       FareStrategy fareStrategy) {
        this.riderService = Objects.requireNonNull(riderService);
        this.driverService = Objects.requireNonNull(driverService);
        this.matchingStrategy = Objects.requireNonNull(matchingStrategy);
        this.fareStrategy = Objects.requireNonNull(fareStrategy);
    }

    public Ride requestRide(String riderId, Location pickup, Location drop) {
        if (pickup == null || drop == null) {
            throw new IllegalArgumentException("Pickup and drop locations are required");
        }
        Rider rider = riderService.getRiderById(riderId);
        rider.setLocation(pickup); // must happen before matching: strategies read rider's location

        List<Driver> candidates = driverService.listAvailableDrivers();
        Driver driver = matchingStrategy.findDriver(rider, candidates); // throws if none available

        Ride ride = new Ride(idGenerator.generateId(IdPrefix.RIDE), rider, pickup, drop);
        ride.assignDriver(driver);
        driverService.updateAvailability(driver.getId(), false);

        rides.put(ride.getId(), ride);
        return ride;
    }

    public FareReceipt completeRide(String rideId) {
        Ride ride = getRideById(rideId);

        double fare = fareStrategy.calculateFare(ride); // pure calculation, no state change yet
        FareReceipt receipt = new FareReceipt(ride.getId(), fare, LocalDateTime.now());
        ride.completeRide(receipt); // validates the transition; throws if ride isn't ASSIGNED

        driverService.recordCompletedRide(ride.getDriverId());
        driverService.updateAvailability(ride.getDriverId(), true);
        return receipt;
    }

    public void cancelRide(String rideId) {
        Ride ride = getRideById(rideId);
        ride.cancelRide(); // validates the transition

        if (ride.hasDriver()) {
            driverService.updateAvailability(ride.getDriverId(), true);
        }
    }

    public Ride getRideById(String rideId) {
        Ride ride = rides.get(rideId);
        if (ride == null) {
            throw new IllegalArgumentException("Ride not found: " + rideId);
        }
        return ride;
    }

    public List<Ride> listRides() {
        return new ArrayList<>(rides.values());
    }
}