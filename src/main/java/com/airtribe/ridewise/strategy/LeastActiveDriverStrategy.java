package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.exception.NoDriverAvailableException;
import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.Rider;

import java.util.Comparator;
import java.util.List;

public class LeastActiveDriverStrategy implements RideMatchingStrategy{
    @Override
    public Driver findDriver(Rider rider, List<Driver> drivers) {
        return drivers.stream()
                .min(Comparator.comparingInt(Driver::getCompletedRides))
                .orElseThrow(() -> new NoDriverAvailableException("No drivers available"));
    }
}
