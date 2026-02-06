package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.data.BookingDataManager;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.IOException;

public class CancelBooking implements Command {

    private int customerId;
    private int flightId;
    private final BookingDataManager bookingDataManager; // ✅ Add this

    public CancelBooking(int customerId, int flightId, BookingDataManager bookingDataManager) {
        this.customerId = customerId;
        this.flightId = flightId;
        this.bookingDataManager = bookingDataManager; // ✅ Assign
    }

    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {

        Customer customer = fbs.getCustomerByID(customerId);
        Flight flight = fbs.getFlightByID(flightId);

        Booking bookingToRemove = null;

        for (Booking b : customer.getBookings()) {
            if (b.getFlight().getId() == flightId) {
                bookingToRemove = b;
                break;
            }
        }

        if (bookingToRemove == null) {
            throw new FlightBookingSystemException("Booking not found.");
        }

        // ✅ REMOVE FROM CUSTOMER
        customer.getBookings().remove(bookingToRemove);

        // ✅ REMOVE FROM FLIGHT
        flight.removePassenger(customer);

        System.out.println("Booking cancelled successfully.");

        // ✅ Save bookings immediately
        try {
            bookingDataManager.storeData(fbs);
        } catch (IOException e) {
            System.out.println("Error saving bookings: " + e.getMessage());
        }
    }
}
