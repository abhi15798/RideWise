package com.airtribe.ridewise;

import com.airtribe.ridewise.exception.NoDriverAvailableException;
import com.airtribe.ridewise.model.Driver;
import com.airtribe.ridewise.model.FareReceipt;
import com.airtribe.ridewise.model.Location;
import com.airtribe.ridewise.model.Ride;
import com.airtribe.ridewise.model.Rider;
import com.airtribe.ridewise.model.VehicleType;
import com.airtribe.ridewise.service.DriverService;
import com.airtribe.ridewise.service.RideService;
import com.airtribe.ridewise.service.RiderService;
import com.airtribe.ridewise.strategy.DefaultFareStrategy;
import com.airtribe.ridewise.strategy.FareStrategy;
import com.airtribe.ridewise.strategy.LeastActiveDriverStrategy;
import com.airtribe.ridewise.strategy.NearestDriverStrategy;
import com.airtribe.ridewise.strategy.PeakHourFareStrategy;
import com.airtribe.ridewise.strategy.RideMatchingStrategy;
import java.util.List;
import java.util.Scanner;

/** Composition root: the only place that knows concrete classes and wires them together. */
public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);

    private final RiderService riderService;
    private final DriverService driverService;
    private final RideService rideService;

    private Main(RiderService riderService, DriverService driverService, RideService rideService) {
        this.riderService = riderService;
        this.driverService = driverService;
        this.rideService = rideService;
    }

    public static void main(String[] args) {
        RiderService riderService = new RiderService();
        DriverService driverService = new DriverService();

        RideMatchingStrategy matchingStrategy = chooseMatchingStrategy();
        FareStrategy fareStrategy = chooseFareStrategy();

        RideService rideService = new RideService(riderService, driverService, matchingStrategy, fareStrategy);
        new Main(riderService, driverService, rideService).run();
    }

    // ---------- startup: pick strategies ----------

    private static RideMatchingStrategy chooseMatchingStrategy() {
        System.out.println("Select ride matching strategy:");
        System.out.println("1. Nearest driver");
        System.out.println("2. Least active driver");
        while (true) {
            int choice = readInt("Choice: ");
            if (choice == 1) return new NearestDriverStrategy();
            if (choice == 2) return new LeastActiveDriverStrategy();
            System.out.println("Please enter 1 or 2.");
        }
    }

    private static FareStrategy chooseFareStrategy() {
        System.out.println("Select fare strategy:");
        System.out.println("1. Default fare");
        System.out.println("2. Peak hour fare");
        while (true) {
            int choice = readInt("Choice: ");
            if (choice == 1) return new DefaultFareStrategy();
            if (choice == 2) return new PeakHourFareStrategy(new DefaultFareStrategy());
            System.out.println("Please enter 1 or 2.");
        }
    }

    // ---------- menu loop ----------

    private void run() {
        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choice: ");
            try {
                switch (choice) {
                    case 1: addRider(); break;
                    case 2: addDriver(); break;
                    case 3: viewAvailableDrivers(); break;
                    case 4: requestRide(); break;
                    case 5: completeRide(); break;
                    case 6: viewRides(); break;
                    case 7: cancelRide(); break;
                    case 8:
                        System.out.println("Thank you for Using RideWise!");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Enter a number from 1 to 8.");
                }
            } catch (NoDriverAvailableException e) {
                System.out.println("Sorry: " + e.getMessage());
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("===== RideWise =====");
        System.out.println("1. Add Rider");
        System.out.println("2. Add Driver");
        System.out.println("3. View Available Drivers");
        System.out.println("4. Request Ride");
        System.out.println("5. Complete Ride");
        System.out.println("6. View Rides");
        System.out.println("7. Cancel Ride");
        System.out.println("8. Exit");
    }

    // ---------- menu actions (services only) ----------

    private void addRider() {
        String name = readString("Rider name: ");
        String email = readString("Rider email: ");
        Rider rider = riderService.registerRider(name, email);
        System.out.println("Rider registered: " + rider.getId());
    }

    private void addDriver() {
        String name = readString("Driver name: ");
        Location location = readLocation("Driver current location");
        VehicleType vehicleType = readVehicleType();
        Driver driver = driverService.registerDriver(name, location, vehicleType);
        System.out.println("Driver registered: " + driver.getId());
    }

    private void viewAvailableDrivers() {
        List<Driver> drivers = driverService.listAvailableDrivers();
        if (drivers.isEmpty()) {
            System.out.println("No available drivers.");
            return;
        }
        for( Driver driver : drivers) {
            System.out.println(driver);
        }
    }

    private void requestRide() {
        String riderId = readString("Rider id: ");
        Location pickup = readLocation("Pickup location");
        Location drop = readLocation("Drop location");
        Ride ride = rideService.requestRide(riderId, pickup, drop);
        System.out.println("Ride booked: " + ride);
    }

    private void completeRide() {
        String rideId = readString("Ride id: ");
        FareReceipt receipt = rideService.completeRide(rideId);
        System.out.println("Ride completed. " + receipt);
    }

    private void cancelRide() {
        String rideId = readString("Ride id: ");
        rideService.cancelRide(rideId);
        System.out.println("Ride cancelled: " + rideId);
    }

    private void viewRides() {
        List<Ride> rides = rideService.listRides();
        if (rides.isEmpty()) {
            System.out.println("No rides yet.");
            return;
        }
        rides.forEach(System.out::println);
    }

    // ---------- input helpers ----------

    private static String readString(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readString(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, try again.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(readString(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, try again.");
            }
        }
    }

    private static Location readLocation(String label) {
        while (true) {
            double lat = readDouble(label + " - latitude: ");
            double lon = readDouble(label + " - longitude: ");
            try {
                return new Location(lat, lon);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage() + ". Try again.");
            }
        }
    }

    private static VehicleType readVehicleType() {
        while (true) {
            String input = readString("Vehicle type (BIKE / AUTO / CAR): ").toUpperCase();
            try {
                return VehicleType.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid vehicle type, try again.");
            }
        }
    }
}