# RideWise: Object Relationships

## 1. Relationship table

| From | To | Type | Notes |
|------|----|------|-------|
| `Rider` | `Ride` | Association | A ride refers to its rider; the rider outlives the ride. A rider may have many rides over time. |
| `Driver` | `Ride` | Association | A ride refers to its assigned driver (null until assigned); the driver outlives the ride. |
| `Ride` | `FareReceipt` | Composition | A receipt exists only as part of a completed ride and has no life of its own. |
| `Rider`, `Driver` | `Location` | Composition (value object) | Each holds its own immutable `Location`; instances are never shared between a rider and a driver. |
| `Ride` | `Location` | Composition (value object) | Holds pickup and drop locations. |
| `RideService` | `RideMatchingStrategy`, `FareStrategy` | Composition via constructor injection | The brief calls this composition. Strictly, since the instances are created in `Main` and passed in, it is a has-a held through interfaces (aggregation). The effect that matters is the same: behaviour is replaceable without touching `RideService`. |
| `RideService` | `RiderService`, `DriverService` | Association (has-a) | Constructor dependencies, used only through public methods. |
| `PeakHourFareStrategy` | `FareStrategy` | Composition (decorator-style) | Wraps another fare strategy and multiplies its result. |
| Strategy classes | `RideMatchingStrategy` / `FareStrategy` | Realization | Implement the interfaces. |
| Services | `IdGenerator` | Dependency | Fetched through `getInstance()`. |

## 2. Structure

```
Main (composition root)
 |-- creates --> RiderService
 |-- creates --> DriverService
 |-- creates --> NearestDriverStrategy | LeastActiveDriverStrategy   (chosen at startup)
 |-- creates --> DefaultFareStrategy | PeakHourFareStrategy          (chosen at startup)
 '-- creates --> RideService(riderService, driverService, matchingStrategy, fareStrategy)

RideService --> RiderService
            --> DriverService
            --> RideMatchingStrategy  (interface)
            --> FareStrategy          (interface)
            --> owns Map<id, Ride>

Ride --> Rider        (association)
     --> Driver       (association, set on assignment)
     --> Location x2  (pickup, drop)
     --> FareReceipt  (composition, set on completion)

PeakHourFareStrategy --> FareStrategy (the strategy it wraps)
```

## 3. Ride lifecycle

```
REQUESTED --assign--> ASSIGNED --complete--> COMPLETED   (terminal)
    |                    |
    '-----cancel---------'--cancel--> CANCELLED          (terminal)
```

Rules live in `RideStatus.canTransitionTo` and are enforced in `Ride.transitionTo`. An illegal move throws `IllegalStateException` such as `Ride RID-0001: cannot go from COMPLETED to CANCELLED`. In the current flow a ride is created and assigned in the same call, so `REQUESTED` is passed through and no ride is stored when matching fails.

## 4. Flows

**Request ride** (`RideService.requestRide(riderId, pickup, drop)`)
1. Reject null pickup or drop.
2. `riderService.getRiderById(riderId)`, then `rider.setLocation(pickup)`.
3. `driverService.listAvailableDrivers()`.
4. `matchingStrategy.findDriver(rider, candidates)`; throws `NoDriverAvailableException` if none.
5. Create `Ride` with a new `RIDE` id, call `ride.assignDriver(driver)`.
6. `driverService.updateAvailability(driverId, false)`; store the ride.

**Complete ride** (`completeRide(rideId)`)
1. Look up the ride.
2. `fareStrategy.calculateFare(ride)` (no state change yet), build a `FareReceipt`.
3. `ride.completeRide(receipt)`; this validates the transition.
4. `driverService.recordCompletedRide(driverId)` and `updateAvailability(driverId, true)`.

**Cancel ride** (`cancelRide(rideId)`)
1. Look up the ride; `ride.cancelRide()` validates the transition.
2. If the ride has a driver, `driverService.updateAvailability(driverId, true)`.

## 5. Law of Demeter in these flows

`RideService` reaches a ride's driver only through `ride.getDriverId()` and `ride.hasDriver()`, never through `ride.getDriver()`. Strategies call methods on their direct arguments only (`rider.getLocation()`, `driver.getCurrentLocation()`, `driver.getCompletedRides()`).
