package com.airtribe.ridewise.service;

import com.airtribe.ridewise.model.Rider;
import com.airtribe.ridewise.util.IdGenerator;
import com.airtribe.ridewise.util.IdPrefix;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RiderService {
    // Deliberately simple check: something@something.tld
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final Map<String, Rider> riders = new LinkedHashMap<>();
    private final IdGenerator idGenerator = IdGenerator.getInstance();

    public Rider registerRider(String name, String email) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Rider name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Rider email cannot be empty");
        }
        String cleanEmail = email.trim();
        if (!EMAIL_PATTERN.matcher(cleanEmail).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + cleanEmail);
        }
        Rider rider = new Rider(idGenerator.generateId(IdPrefix.RIDER), name.trim(), cleanEmail);
        riders.put(rider.getId(), rider);
        return rider;
    }

    public Rider getRiderById(String riderId) {
        Rider rider = riders.get(riderId);
        if (rider == null) {
            throw new IllegalArgumentException("Rider not found: " + riderId);
        }
        return rider;
    }
}