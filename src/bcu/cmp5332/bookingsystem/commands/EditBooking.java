package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.data.BookingDataManager;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.IOException;

public class EditBooking implements Command {

    private final int bookingId;
    private final int newFlightId;
    private final BookingDataManager bookingDataManager;

    public EditBooking(int bookingId, int newFlightId, BookingDataManager bookingDataManager) {
        this.bookingId = bookingId;
        this.newFlightId = newFlightId;
        this.bookingDataManager = bookingDataManager;
    }

    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {

        Booking booking = fbs.getBookingById(bookingId);
        if (booking == null) {
            throw new FlightBookingSystemException("Booking not found.");
        }

        Flight oldFlight = booking.getFlight();
        Flight newFlight = fbs.getFlightByID(newFlightId);

        oldFlight.removePassenger(booking.getCustomer());
        newFlight.addPassenger(booking.getCustomer());

        booking.setFlight(newFlight);

        System.out.println(
                "Booking " + bookingId + " successfully updated to flight " + newFlightId + "."
        );

        try {
            bookingDataManager.storeData(fbs);
        } catch (IOException e) {
            System.out.println("Error saving bookings: " + e.getMessage());
        }
    }
}
