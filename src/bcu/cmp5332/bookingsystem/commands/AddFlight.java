package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.data.FlightDataManager;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Command to add a new flight to the system.
 */
public class AddFlight implements Command {

    private final String flightNumber;
    private final String origin;
    private final String destination;
    private final LocalDate departureDate;
    private final int capacity;
    private final double price;
    private final FlightDataManager flightDataManager;

    /**
     * Creates a new AddFlight command with full flight details.
     * 
     * @param flightNumber the flight number
     * @param origin the departure location
     * @param destination the arrival location
     * @param departureDate the date of departure
     * @param capacity the maximum number of passengers
     * @param price the price per seat
     * @param flightDataManager the data manager for persistence
     */
    public AddFlight(String flightNumber, String origin, String destination, 
                     LocalDate departureDate, int capacity, double price,
                     FlightDataManager flightDataManager) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.capacity = capacity;
        this.price = price;
        this.flightDataManager = flightDataManager;
    }

    /**
     * Executes the command to add the flight to the system.
     * 
     * @param flightBookingSystem the system to add the flight to
     * @throws FlightBookingSystemException if there's an error adding the flight
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        int maxId = 0;
        if (flightBookingSystem.getFlights().size() > 0) {
            int lastIndex = flightBookingSystem.getFlights().size() - 1;
            maxId = flightBookingSystem.getFlights().get(lastIndex).getId();
        }

        Flight flight = new Flight(++maxId, flightNumber, origin, destination, departureDate, capacity, price);
        flightBookingSystem.addFlight(flight);
        System.out.println("Flight #" + flight.getId() + " added.");

        // Save immediately after adding
        try {
            flightDataManager.storeData(flightBookingSystem);
        } catch (IOException e) {
            System.out.println("Error saving flights: " + e.getMessage());
        }
    }
}
