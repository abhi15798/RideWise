# RideWise: Requirements

RideWise is a console-based ride-sharing (Uber/Ola style) system. The goal is not a full app but a clean low-level design: Strategy pattern through interfaces, SOLID, composition over inheritance, low coupling and the Law of Demeter. It is plain Java with no framework; `Main` is the composition root and wires dependencies by hand.

## 1. Functional requirements

| # | Requirement | Where it is implemented |
|---|-------------|-------------------------|
| FR1 | Register riders | `RiderService.registerRider(name, email)` |
| FR2 | Register drivers | `DriverService.registerDriver(name, location, vehicleType)` |
| FR3 | Show available drivers | `DriverService.listAvailableDrivers()` |
| FR4 | Request a ride | `RideService.requestRide(riderId, pickup, drop)` |
| FR5 | Match ride to driver using a strategy | `RideMatchingStrategy` (`NearestDriverStrategy`, `LeastActiveDriverStrategy`) |
| FR6 | Calculate fare using a pricing strategy | `FareStrategy` (`DefaultFareStrategy`, `PeakHourFareStrategy`) |
| FR7 | Track ride status | `RideStatus`: REQUESTED, ASSIGNED, COMPLETED, CANCELLED, enforced inside `Ride` |
| FR8 | Complete a ride | `RideService.completeRide(rideId)` returns a `FareReceipt` |
| FR9 | Cancel a ride | `RideService.cancelRide(rideId)` (added, see decisions) |

## 2. Non-functional requirements

| Requirement | How it is met |
|-------------|---------------|
| Easily extendable pricing | New class implementing `FareStrategy`; no change to `RideService` |
| Easily change matching logic | New class implementing `RideMatchingStrategy`; chosen in `Main` |
| Low coupling between services | `RideService` talks to `RiderService`/`DriverService` through their public methods only, and to strategies through interfaces |
| Maintainable, readable code | Small single-purpose classes, one package per layer |

## 3. Console menu

| Option | Action |
|--------|--------|
| 1 | Add Rider (name, email) |
| 2 | Add Driver (name, latitude, longitude, vehicle type) |
| 3 | View Available Drivers |
| 4 | Request Ride (rider id, pickup lat/long, drop lat/long) |
| 5 | Complete Ride |
| 6 | View Rides |
| 7 | Cancel Ride |
| 8 | Exit |

At startup the user picks the matching strategy and the fare strategy. Every option calls the service layer only, and invalid input is caught: bad numbers are re-prompted, and service errors are caught in one place in `Main`.

## 4. Design decisions and assumptions

These fill gaps or deviate from the original brief. Each is intentional.

1. **Locations.** A `Location` value class (latitude, longitude, immutable) is shared by `Rider` and `Driver`. Distance uses the Haversine formula in kilometres.
2. **Rider location is per ride.** A rider has no location at registration. Pickup and drop are passed to `requestRide`; the pickup is stored on the rider as last known location before matching.
3. **Distance is derived.** `Ride` computes `distance` from pickup and drop, so a caller cannot pass an inconsistent value.
4. **Cancel is in scope.** The brief lists CANCELLED but no operation reaches it, so `cancelRide` was added. Because of it the menu has 8 options: Cancel Ride is 7 and Exit moved to 8.
5. **Rider email.** `email` is an extra field not in the brief. It is validated for presence and a simple format, and is stored only.
6. **`VehicleType`.** Stored on `Driver` and shown in listings. No strategy uses it yet; it is the natural extension point for per-vehicle pricing.
7. **Peak pricing.** `PeakHourFareStrategy` applies a surge multiplier (default 1.5) on top of another `FareStrategy`. Deciding when peak applies is the caller's job (choose it at startup); it does not read the clock.
8. **Fare defaults.** Base fare 50, plus 12 per km.
9. **IDs.** String IDs such as `RDR-0001`, `DRI-0001`, `RID-0001` from a singleton `IdGenerator` with an `IdPrefix` enum.
10. **Fare receipt.** One receipt per ride, identified by `rideId`; it has no id of its own.
11. **State pattern not used.** Four statuses and three transitions do not justify it (YAGNI). See `SOLID_Reflection.md`.
12. **Storage.** In memory only (`LinkedHashMap`), insertion order kept.

## 5. Out of scope

Persistence, authentication, real maps or routing, payments, concurrency beyond the thread-safe ID generator, driver movement during a trip, and ratings.
