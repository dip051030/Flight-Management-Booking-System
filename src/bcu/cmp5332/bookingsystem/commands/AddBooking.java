package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.auth.AuthService;
import bcu.cmp5332.bookingsystem.data.BookingDataManager;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Command to create a booking for a flight.
 *
 * <p>Access rules:</p>
 * <ul>
 *   <li>Administrators may create bookings for any customer.</li>
 *   <li>Customers may create bookings only for themselves.</li>
 * </ul>
 *
 * <p>Capacity constraints are enforced using the safe passenger
 * addition mechanism provided by the Flight entity.</p>
 */
public class AddBooking implements Command {

    private final int customerId;
    private final int flightId;
    private final BookingDataManager bookingDataManager;

    /**
     * Constructs an AddBooking command.
     *
     * @param customerId target customer ID (ignored for customer role)
     * @param flightId target flight ID
     * @param bookingDataManager data manager for persistence
     */
    public AddBooking(int customerId, int flightId, BookingDataManager bookingDataManager) {
        this.customerId = customerId;
        this.flightId = flightId;
        this.bookingDataManager = bookingDataManager;
    }

    /**
     * Executes the booking creation process.
     *
     * @param fbs the FlightBookingSystem instance
     * @throws FlightBookingSystemException if validation or persistence fails
     */
    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {

        AuthService.requireLogin();

        Customer customer;
        if (AuthService.isAdmin()) {
            customer = fbs.getCustomerByID(customerId);
        } else {
            customer = AuthService.currentCustomer();
        }

        Flight flight = fbs.getFlightByID(flightId);

        if (flight.isDeleted()) {
            throw new FlightBookingSystemException("Cannot book a deleted flight.");
        }

        if (customer.isDeleted()) {
            throw new FlightBookingSystemException("Cannot make booking for a deleted customer.");
        }

        boolean added = flight.addPassengerSafe(customer);
        if (!added) {
            throw new FlightBookingSystemException(
                    "Flight " + flight.getFlightNumber() +
                            " is at full capacity (" + flight.getCapacity() + " seats)."
            );
        }

        Booking booking = new Booking(customer, flight, LocalDate.now());
        customer.addBooking(booking);
        fbs.addBooking(booking);

        System.out.println("Booking successful! Booking ID: " + booking.getId());
        System.out.println("Seats remaining: " + flight.getAvailableSeats());

        try {
            bookingDataManager.storeData(fbs);
        } catch (IOException e) {
            customer.getBookings().remove(booking);
            flight.removePassenger(customer);
            fbs.removeBooking(booking);
            throw new FlightBookingSystemException("Error saving booking: " + e.getMessage());
        }
    }
}
