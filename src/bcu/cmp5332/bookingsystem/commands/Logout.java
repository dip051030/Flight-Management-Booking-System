package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.auth.AuthService;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command to terminate the current authentication session.
 */
public class Logout implements Command {

    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        AuthService.logout();
        System.out.println("Logged out.");
    }
}
