package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.data.FlightDataManager;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.IOException;

/**
 * Command to soft-delete a flight from the system.
 * Requirement: 70-79% - Remove (hide) flights using soft delete.
 */
public class DeleteFlight implements Command {

    private final int flightId;
    private final FlightDataManager flightDataManager;

    /**
     * Creates a new DeleteFlight command.
     * 
     * @param flightId the ID of the flight to delete
     * @param flightDataManager the data manager for persistence
     */
    public DeleteFlight(int flightId, FlightDataManager flightDataManager) {
        this.flightId = flightId;
        this.flightDataManager = flightDataManager;
    }

    /**
     * Executes the soft delete by marking the flight as deleted.
     * 
     * @param fbs the flight booking system
     * @throws FlightBookingSystemException if flight not found or other error
     */
    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        Flight flight = fbs.getFlightByID(flightId);
        
        if (flight.isDeleted()) {
            throw new FlightBookingSystemException("Flight is already deleted.");
        }
        
        // Soft delete - just mark as deleted
        flight.setDeleted(true);
        
        System.out.println("Flight #" + flightId + " has been deleted (hidden from system).");
        
        // Save changes immediately
        try {
            flightDataManager.storeData(fbs);
        } catch (IOException e) {
            // Rollback the delete
            flight.setDeleted(false);
            throw new FlightBookingSystemException("Error saving flight deletion: " + e.getMessage());
        }
    }
}
