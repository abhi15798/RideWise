package com.airtribe.ridewise.model;

public enum RideStatus {
    REQUESTED,
    ASSIGNED,
    COMPLETED,
    CANCELLED;
    /** Lifecycle rules live here, in one place. COMPLETED and CANCELLED are terminal. */
    public boolean canTransitionTo(RideStatus next) {
        switch (this) {
            case REQUESTED:
                return next == ASSIGNED || next == CANCELLED;
            case ASSIGNED:
                return next == COMPLETED || next == CANCELLED;
            default:
                return false;
        }
    }
}
