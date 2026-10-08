# RideWise: SOLID and Design Reflection

## SOLID

**SRP: one reason to change per class.**
- `RiderService` manages riders; `DriverService` manages drivers and their availability; `RideService` orchestrates a ride; `Main` handles console I/O only.
- Fare maths lives in `FareStrategy` implementations, matching logic in `RideMatchingStrategy` implementations, geometry in `Location`, lifecycle rules in `RideStatus`/`Ride`.

**OCP: extend without modifying.**
- A new pricing rule (say, per-vehicle rates) is a new `FareStrategy`. A new matching rule (say, highest rated) is a new `RideMatchingStrategy`. `RideService` is untouched; only the wiring in `Main` changes.
- `PeakHourFareStrategy` takes its multiplier as a constructor argument, so a new rate needs no edit to the class.

**LSP: strategies are interchangeable.**
- Every `RideMatchingStrategy` follows the same contract: it receives available drivers and throws `NoDriverAvailableException` on an empty list. `RideService` never needs to know which one it holds.
- Every `FareStrategy` returns a fare from a `Ride` with no hidden preconditions beyond a valid ride.

**ISP: small interfaces.**
- Each strategy interface has exactly one method, so no implementer carries methods it does not use.

**DIP: depend on abstractions.**
- `RideService` depends on `RideMatchingStrategy` and `FareStrategy` (interfaces) and receives them through its constructor. Only `Main` knows the concrete strategy classes.
- Honest limit: `RideService` depends on the concrete `RiderService` and `DriverService`, and services call `IdGenerator.getInstance()` directly. Interfaces for them would be over-engineering at this size, but it is a known gap.

## Other principles

- **Composition over inheritance.** There is no inheritance hierarchy among domain classes. `PeakHourFareStrategy` wraps another `FareStrategy` instead of extending `DefaultFareStrategy`.
- **DRY.** Availability filtering happens once, in `DriverService.listAvailableDrivers()`. Lifecycle validation happens once, in `Ride.transitionTo`, with the rules in `RideStatus`. Id formatting happens once, in `IdGenerator`.
- **KISS.** Plain classes, in-memory maps, one console loop.
- **YAGNI.** No persistence, no interfaces for services, no State pattern, no `Clock` abstraction.
- **Law of Demeter.** `RideService` calls `ride.getDriverId()` and `ride.hasDriver()` instead of `ride.getDriver().getId()`. `Ride` does not expose its `Rider` or `Driver` objects. Strategies read one hop only (`rider.getLocation()`, `driver.getCurrentLocation()`).

## Patterns considered

- **State pattern: rejected.** There are four statuses and three transitions, and behaviour does not differ per state; only the permission to transition does. A transition table in `RideStatus.canTransitionTo` is enough. If per-state behaviour appears (for example `IN_PROGRESS` with its own billing rules), refactor to State then.
- **Strategy: used** for matching and fares.
- **Decorator-style composition: used** for peak pricing over a base fare.

## Trade-offs and known limitations

1. **`IdGenerator` is a singleton.** It is global state and makes ids hard to predict in tests. Accepted for the MVP; the fix is to inject it through constructors.
2. **Temporal coupling on rider location.** The brief fixes `findDriver(Rider, List<Driver>)`, so the rider's location must be set before matching. `RideService.requestRide` does this; the contract is written on the interface. A signature taking a `Location` would remove the coupling but departs from the brief. `NearestDriverStrategy` reads that location and relies on this ordering.
3. **Location update on failed matching.** If no driver is available, the rider's last known location still changes to the new pickup.
4. **Tie-breaking.** `LeastActiveDriverStrategy` returns the first driver in list order on a tie, which means the earliest registered.
5. **Peak window is not time-aware.** `PeakHourFareStrategy` multiplies every fare it computes; whoever wires the app decides when to use it. A `Clock`-based check is a small addition.
6. **`VehicleType` is unused by pricing.** It is stored and displayed, and kept as an extension point.
7. **Unchecked `NoDriverAvailableException`.** Chosen so the interface signature matches the brief; the catch sits in `Main`.
8. **In-memory, single-threaded.** Services use plain `LinkedHashMap`s and are not thread-safe.
9. **No duplicate-email check.** Two riders may share an email.
