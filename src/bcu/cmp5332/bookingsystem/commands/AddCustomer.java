package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.auth.AuthService;
import bcu.cmp5332.bookingsystem.auth.PasswordUtil;
import bcu.cmp5332.bookingsystem.data.CustomerDataManager;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.IOException;

/**
 * Command to add a new customer to the Flight Booking System.
 *
 * <p>This operation is restricted to administrators. Each customer
 * is created with authentication credentials consisting of a unique
 * username and a hashed password.</p>
 *
 * <p>Usernames must be unique across all customers.</p>
 */
public class AddCustomer implements Command {

    private final String name;
    private final String phone;
    private final String email;
    private final String username;
    private final String password;
    private final CustomerDataManager customerDataManager;

    /**
     * Constructs a new AddCustomer command.
     *
     * @param name customer's name
     * @param phone customer's phone number
     * @param email customer's email address
     * @param username unique username for authentication
     * @param password raw password (hashed before storage)
     * @param customerDataManager data manager for persistence
     */
    public AddCustomer(
            String name,
            String phone,
            String email,
            String username,
            String password,
            CustomerDataManager customerDataManager
    ) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.password = password;
        this.customerDataManager = customerDataManager;
    }

    /**
     * Executes the command to create and persist a new customer.
     *
     * @param fbs the FlightBookingSystem instance
     * @throws FlightBookingSystemException if validation or persistence fails
     */
    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {

        AuthService.requireAdmin();

        int maxId = 0;
        for (Customer c : fbs.getCustomers().values()) {
            if (c.getId() > maxId)
            if (username.equalsIgnoreCase(c.getEmail())) {
                throw new FlightBookingSystemException("Username already exists.");
            }
        }

        Customer customer = new Customer(
                ++maxId,
                name,
                phone,
                email,
                PasswordUtil.hash(password)
        );

        fbs.addCustomer(customer);
        System.out.println("Customer #" + customer.getId() + " added.");

        try {
            customerDataManager.storeData(fbs);
        } catch (IOException e) {
            throw new FlightBookingSystemException("Error saving customers: " + e.getMessage());
        }
    }
}
