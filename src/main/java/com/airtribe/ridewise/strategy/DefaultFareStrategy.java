package com.airtribe.ridewise.strategy;

import com.airtribe.ridewise.model.Ride;

public class DefaultFareStrategy implements FareStrategy{
    private final double baseFare;
    private final double perKmRate;

    public DefaultFareStrategy() {
        this(50.0, 12.0);
    }
    public DefaultFareStrategy(double baseFare, double perKmRate) {
        this.baseFare = baseFare;
        this.perKmRate = perKmRate;
    }
    @Override
    public double calculateFare(Ride ride) {
        return baseFare + ride.getDistance()*perKmRate;
    }
}
