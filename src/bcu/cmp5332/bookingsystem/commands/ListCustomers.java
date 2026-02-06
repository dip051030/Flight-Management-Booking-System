package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

public class ListCustomers implements Command {

    @Override
    public void execute(FlightBookingSystem flightBookingSystem)
            throws FlightBookingSystemException {

        if (flightBookingSystem.getCustomers().isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        for (Customer customer : flightBookingSystem.getCustomers().values()) {
            System.out.println(
                "Customer #" + customer.getId() + " - "
                + customer.getName() + " - "
                + customer.getPhone()
            );
        }

        System.out.println(
            flightBookingSystem.getCustomers().size() + " customer(s)"
        );
    }
}
