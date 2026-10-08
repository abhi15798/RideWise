package com.airtribe.ridewise.service;

import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.Location;
import com.airtribe.ridewise.model.VehicleType;
import com.airtribe.ridewise.util.IdGenerator;
import com.airtribe.ridewise.util.IdPrefix;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DriverService {
    private final Map<String, Driver> drivers = new LinkedHashMap<>();
    private final IdGenerator idGenerator = IdGenerator.getInstance();

    public Driver registerDriver(String name, Location location, VehicleType vehicleType) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Driver name cannot be empty");
        }
        Driver driver = new Driver(idGenerator.generateId(IdPrefix.DRIVER), name.trim(), location, vehicleType);
        drivers.put(driver.getId(), driver);
        return driver;
    }

    public Driver getDriverById(String id) {
        Driver driver = drivers.get(id);
        if (driver == null) {
            throw new IllegalArgumentException("Driver not found: " + id);
        }
        return driver;
    }

    public void updateAvailability(String driverId, boolean available) {
        getDriverById(driverId).setAvailable(available);
    }

    public void recordCompletedRide(String driverId) {
        getDriverById(driverId).incrementCompletedRides();
    }

    public List<Driver> listAvailableDrivers() {
        return drivers.values().stream()
                .filter(Driver::isAvailable)
                .collect(Collectors.toList());
    }
}