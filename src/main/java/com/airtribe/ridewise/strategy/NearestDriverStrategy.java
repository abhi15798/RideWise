package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.exception.NoDriverAvailableException;
import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.Location;
import com.airtribe.ridewise.model.Rider;

import java.util.Comparator;
import java.util.List;

public class NearestDriverStrategy implements RideMatchingStrategy{
    @Override
    public Driver findDriver(Rider rider, List<Driver> drivers) {
        Location riderLocation = rider.getLocation();
        if (riderLocation == null) {
            throw new IllegalStateException("Rider location is null");
        }
        return drivers.stream()
                .min(Comparator.comparingDouble(d -> d.getCurrentLocation().distanceTo(riderLocation)))
                .orElseThrow(() -> new NoDriverAvailableException("No drivers available"));
    }
}
