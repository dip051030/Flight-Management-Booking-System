package bcu.cmp5332.bookingsystem.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a flight in the booking system.
 * Stores flight details, passenger list, capacity, and pricing information.
 */
public class Flight {

    private int id;
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDate departureDate;

    private final Set<Customer> passengers;

    private int capacity = 100; // default flight capacity
    private double price = 0.0; // flight price in currency units
    private boolean deleted = false; // soft delete flag

    // --- Constructors ---
    
    /**
     * Creates a new Flight with basic details (capacity and price set to defaults).
     * 
     * @param id the unique flight identifier
     * @param flightNumber the flight number (e.g., "BA123")
     * @param origin the departure location
     * @param destination the arrival location
     * @param departureDate the date of departure
     */
    public Flight(int id, String flightNumber, String origin, String destination, LocalDate departureDate) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.passengers = new HashSet<>();
    }
    
    /**
     * Creates a new Flight with full details including capacity and price.
     * 
     * @param id the unique flight identifier
     * @param flightNumber the flight number (e.g., "BA123")
     * @param origin the departure location
     * @param destination the arrival location
     * @param departureDate the date of departure
     * @param capacity the maximum number of passengers
     * @param price the price per seat
     */
    public Flight(int id, String flightNumber, String origin, String destination, 
                  LocalDate departureDate, int capacity, double price) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.capacity = capacity;
        this.price = price;
        this.passengers = new HashSet<>();
    }

    // --- Getters / Setters ---
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public LocalDate getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }

    /**
     * Gets the maximum passenger capacity for this flight.
     * 
     * @return the maximum number of passengers
     */
    public int getCapacity() { return capacity; }
    
    /**
     * Sets the maximum passenger capacity for this flight.
     * 
     * @param capacity the maximum number of passengers
     */
    public void setCapacity(int capacity) { this.capacity = capacity; }

    /**
     * Gets the price per seat for this flight.
     * 
     * @return the price in currency units
     */
    public double getPrice() { return price; }
    
    /**
     * Sets the price per seat for this flight.
     * 
     * @param price the price in currency units
     */
    public void setPrice(double price) { this.price = price; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    // --- Passenger Handling ---
    
    /**
     * Gets the list of passengers booked on this flight.
     * 
     * @return an immutable list of passengers
     */
    public List<Customer> getPassengers() {
        return new ArrayList<>(passengers);
    }

    /**
     * Safely adds a passenger to the flight, checking capacity and deleted status.
     * 
     * @param passenger the customer to add as a passenger
     * @return true if the passenger was added, false if flight is full or deleted
     */
    public boolean addPassengerSafe(Customer passenger) {
        if (deleted) return false;
        if (passengers.size() >= capacity) return false;
        return passengers.add(passenger);
    }

    /**
     * Adds a passenger to the flight without capacity checking.
     * Use this only when loading from storage.
     * 
     * @param passenger the customer to add as a passenger
     */
    public void addPassenger(Customer passenger) {
        passengers.add(passenger);
    }
    
    /**
     * Removes a passenger from the flight.
     * 
     * @param passenger the customer to remove
     */
    public void removePassenger(Customer passenger) {
        passengers.remove(passenger);
    }
    
    /**
     * Gets the number of available seats on this flight.
     * 
     * @return the number of seats still available
     */
    public int getAvailableSeats() {
        return capacity - passengers.size();
    }

    // --- Display Helpers ---
    
    /**
     * Gets a short description of the flight.
     * 
     * @return a formatted string with basic flight information
     */
    public String getDetailsShort() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "Flight #" + id + " - " + flightNumber + " - " +
               origin + " to " + destination +
               " on " + departureDate.format(dtf);
    }

    /**
     * Gets a detailed description of the flight.
     * 
     * @return a formatted string with comprehensive flight information
     */
    public String getDetailsLong() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "Flight ID: " + id + "\n" +
               "Flight Number: " + flightNumber + "\n" +
               "From: " + origin + "\n" +
               "To: " + destination + "\n" +
               "Departure Date: " + departureDate.format(dtf) + "\n" +
               "Price: $" + String.format("%.2f", price) + "\n" +
               "Capacity: " + capacity + "\n" +
               "Number of Passengers: " + passengers.size() + "\n" +
               "Available Seats: " + getAvailableSeats();
    }

    /**
     * Gets extended details including deleted status.
     * 
     * @return a formatted string with all flight information
     */
    public String getDetailsLongExtra() {
        return getDetailsLong() +
               "\nDeleted: " + deleted;
    }

    @Override
    public String toString() {
        return getDetailsLong();
    }
}
