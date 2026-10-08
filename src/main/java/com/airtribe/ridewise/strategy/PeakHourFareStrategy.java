package com.airtribe.ridewise.strategy;
import com.airtribe.ridewise.model.Ride;
import java.util.Objects;

/**
 * Applies a surge multiplier on top of another FareStrategy (composition, not inheritance).
 * Deciding WHEN peak applies is the caller's job: choose this strategy during peak time.
 */
public class PeakHourFareStrategy implements FareStrategy {
    private static final double DEFAULT_MULTIPLIER = 1.5;

    private final FareStrategy baseFareStrategy;
    private final double multiplier;

    public PeakHourFareStrategy(FareStrategy baseFareStrategy) {
        this(baseFareStrategy, DEFAULT_MULTIPLIER);
    }

    public PeakHourFareStrategy(FareStrategy baseFareStrategy, double multiplier) {
        this.baseFareStrategy = Objects.requireNonNull(baseFareStrategy, "baseFareStrategy cannot be null");
        if (multiplier <= 0) {
            throw new IllegalArgumentException("Multiplier must be positive");
        }
        this.multiplier = multiplier;
    }

    @Override
    public double calculateFare(Ride ride) {
        return baseFareStrategy.calculateFare(ride) * multiplier;
    }
}