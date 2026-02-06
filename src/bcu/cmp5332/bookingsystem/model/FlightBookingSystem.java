package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Central model class for the Flight Booking System.
 * Updated to filter deleted entities from views (70-79% requirement).
 */
public class FlightBookingSystem {

    private final LocalDate systemDate = LocalDate.parse("2024-11-11");

    private final Map<Integer, Customer> customers = new TreeMap<>();
    private final Map<Integer, Flight> flights = new TreeMap<>();
    private final List<Booking> bookings = new ArrayList<>();

    public LocalDate getSystemDate() {
        return systemDate;
    }

    // ---------- FLIGHTS ----------
    
    /**
     * Gets all non-deleted flights.
     * Requirement: 70-79% - Filter out deleted flights from list views.
     * 
     * @return list of active flights only
     */
    public List<Flight> getFlights() {
        return flights.values().stream()
                .filter(f -> !f.isDeleted())
                .collect(Collectors.toList());
    }
    
    /**
     * Gets ALL flights including deleted ones (for internal use).
     * 
     * @return list of all flights
     */
    public List<Flight> getAllFlights() {
        return new ArrayList<>(flights.values());
    }

    /**
     * Gets a flight by ID (works for both deleted and active flights).
     * 
     * @param id the flight ID
     * @return the flight
     * @throws FlightBookingSystemException if flight not found
     */
    public Flight getFlightByID(int id) throws FlightBookingSystemException {
        if (!flights.containsKey(id)) {
            throw new FlightBookingSystemException("There is no flight with that ID.");
        }
        return flights.get(id);
    }

    /**
     * Adds a flight to the system.
     * 
     * @param flight the flight to add
     * @throws FlightBookingSystemException if there's an error adding
     */
    public void addFlight(Flight flight) throws FlightBookingSystemException {
        flights.put(flight.getId(), flight);
    }

    // ---------- CUSTOMERS ----------
    
    /**
     * Gets all non-deleted customers.
     * Requirement: 70-79% - Filter out deleted customers from list views.
     * 
     * @return map of active customers only
     */
    public Map<Integer, Customer> getCustomers() {
        return customers.entrySet().stream()
                .filter(entry -> !entry.getValue().isDeleted())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, TreeMap::new));
    }
    
    /**
     * Gets ALL customers including deleted ones (for internal use).
     * 
     * @return map of all customers
     */
    public Map<Integer, Customer> getAllCustomers() {
        return customers;
    }
    
    /**
     * Removes a booking from the system.
     * 
     * @param booking the booking to remove
     */
    public void removeBooking(Booking booking) {
        bookings.remove(booking);
    }

    /**
     * Gets a customer by ID (works for both deleted and active customers).
     * 
     * @param id the customer ID
     * @return the customer
     * @throws FlightBookingSystemException if customer not found
     */
    public Customer getCustomerByID(int id) throws FlightBookingSystemException {
        if (!customers.containsKey(id)) {
            throw new FlightBookingSystemException("There is no customer with that ID.");
        }
        return customers.get(id);
    }

    /**
     * Adds a customer to the system.
     * 
     * @param customer the customer to add
     */
    public void addCustomer(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    // ---------- BOOKINGS ----------
    
    /**
     * Adds a booking to the system.
     * 
     * @param booking the booking to add
     */
    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    /**
     * Gets all bookings.
     * 
     * @return unmodifiable list of all bookings
     */
    public List<Booking> getBookings() {
        return Collections.unmodifiableList(bookings);
    }

    /**
     * Gets a booking by ID.
     * 
     * @param bookingId the booking ID
     * @return the booking
     * @throws FlightBookingSystemException if booking not found
     */
    public Booking getBookingById(int bookingId) throws FlightBookingSystemException {
        for (Booking b : bookings) {
            if (b.getId() == bookingId) {
                return b;
            }
        }
        throw new FlightBookingSystemException("Booking with ID " + bookingId + " not found.");
    }
}
