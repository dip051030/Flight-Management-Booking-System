package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

public class ShowFlight implements Command {

    private int flightId;

    public ShowFlight(int flightId) {
        this.flightId = flightId;
    }

    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        Flight flight = fbs.getFlightByID(flightId);

        if (flight == null) {
            throw new FlightBookingSystemException("Flight not found.");
        }

        System.out.println(flight);
    }
}
