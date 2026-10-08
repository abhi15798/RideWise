# RideWise: Class Model

Base package: `com.airtribe.ridewise`

```
ridewise/
  Main.java
  model/      Rider, Driver, Ride, FareReceipt, Location, RideStatus, VehicleType
  strategy/   RideMatchingStrategy, NearestDriverStrategy, LeastActiveDriverStrategy,
              FareStrategy, DefaultFareStrategy, PeakHourFareStrategy
  service/    RiderService, DriverService, RideService
  exception/  NoDriverAvailableException
  util/       IdGenerator, IdPrefix
```

## model/

| Class | Fields | Notes |
|-------|--------|-------|
| `Location` (final) | `latitude`, `longitude` | Immutable. Validates ranges. `distanceTo(Location)` returns km (Haversine). |
| `Rider` | `id`, `name`, `email`, `location` | `location` is null until the first ride request; set through `setLocation`. |
| `Driver` | `id`, `name`, `currentLocation`, `vehicleType`, `isAvailable`, `completedRides` | Starts available with 0 completed rides. `completedRides` feeds `LeastActiveDriverStrategy`. |
| `Ride` | `id`, `rider`, `driver`, `pickupLocation`, `dropLocation`, `distance`, `rideStatus`, `fareReceipt` | `distance` derived in the constructor. Status changes only through `assignDriver`, `completeRide`, `cancelRide`, which all go through one private `transitionTo`. Exposes `getRiderId/Name`, `getDriverId/Name`, `hasDriver` so callers never chain through `rider` or `driver`. |
| `FareReceipt` (final) | `rideId`, `amount`, `generatedAt` | Immutable. |

Enums:
- `RideStatus`: `REQUESTED`, `ASSIGNED`, `COMPLETED`, `CANCELLED`. Holds the lifecycle rules in `canTransitionTo(next)`.
- `VehicleType`: `BIKE`, `AUTO`, `CAR`.

## strategy/

| Type | Kind | Behaviour |
|------|------|-----------|
| `RideMatchingStrategy` | interface | `Driver findDriver(Rider rider, List<Driver> drivers)` |
| `NearestDriverStrategy` | class | Picks the driver with the smallest `distanceTo` the rider's location. |
| `LeastActiveDriverStrategy` | class | Picks the driver with the fewest `completedRides`; ties go to the first in the list. |
| `FareStrategy` | interface | `double calculateFare(Ride ride)` |
| `DefaultFareStrategy` | class | `baseFare + distance * perKmRate` (defaults 50 and 12). |
| `PeakHourFareStrategy` | class | Wraps another `FareStrategy` and multiplies its result (default 1.5). |

Contract for every `RideMatchingStrategy`: callers pass available drivers only, and an empty list throws `NoDriverAvailableException`.

## service/

| Service | Public methods |
|---------|----------------|
| `RiderService` | `registerRider(name, email)`, `getRiderById(id)` |
| `DriverService` | `registerDriver(name, location, vehicleType)`, `getDriverById(id)`, `updateAvailability(id, available)`, `recordCompletedRide(id)`, `listAvailableDrivers()` |
| `RideService` | `requestRide(riderId, pickup, drop)`, `completeRide(rideId)`, `cancelRide(rideId)`, `getRideById(id)`, `listRides()` |

`RideService` constructor: `(RiderService, DriverService, RideMatchingStrategy, FareStrategy)`. Both strategies are injected as interfaces.

## exception/ and util/

- `NoDriverAvailableException` extends `RuntimeException`. It is unchecked because the brief's `findDriver` signature has no `throws` clause.
- `IdGenerator`: singleton; `generateId(IdPrefix)` returns `<code>-<4-digit counter>`, with a separate counter per prefix.
- `IdPrefix`: `RIDER("RDR")`, `DRIVER("DRI")`, `RIDE("RID")`.

## Differences from the brief

| Item | Brief | Implementation | Reason |
|------|-------|----------------|--------|
| `Location` | not listed | added to `model/` | Needed for distance; shared by rider and driver |
| `Rider.email` | not listed | added | Requested field |
| `Ride` | id, rider, driver, distance, status | plus pickup, drop, receipt | Distance needs two points; receipt composition |
| `Driver.completedRides` | not listed | added | `LeastActiveDriverStrategy` needs it |
| `IdPrefix` | not listed | added to `util/` | Avoids string-typed id prefixes |
| Menu | 7 options | 8 options | Cancel Ride added; Exit is 8 |
