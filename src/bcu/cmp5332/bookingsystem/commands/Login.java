package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.auth.AuthService;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command responsible for authenticating users into the system.
 *
 * <p>This command supports two roles:
 * <ul>
 *   <li><b>Admin</b> – authenticated using fixed credentials.</li>
 *   <li><b>Customer</b> – authenticated against stored customer records.</li>
 * </ul>
 *
 * <p>On successful authentication, the authenticated user and role are
 * stored in {@link AuthService} for subsequent authorization checks.</p>
 */
public class Login implements Command {

    private final String username;
    private final String password;

    /**
     * Constructs a login command with the provided credentials.
     *
     * @param username the username entered by the user
     * @param password the password entered by the user
     */
    public Login(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Executes the login process.
     *
     * <p>If the username is {@code admin}, admin authentication is attempted.
     * Otherwise, the system searches for a matching customer username.</p>
     *
     * @param fbs the flight booking system containing customer data
     * @throws FlightBookingSystemException if authentication fails
     */
    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {

        // ---- ADMIN LOGIN ----
        if ("admin".equals(username)) {
            AuthService.loginAdmin(username, password);
            System.out.println("Admin logged in successfully.");
            return;
        }

        // ---- CUSTOMER LOGIN ----
        for (Customer customer : fbs.getCustomers().values()) {
            if (username.equalsIgnoreCase(customer.getUsername())) {
                AuthService.loginCustomer(customer, password);
                System.out.println("Customer logged in successfully.");
                return;
            }
        }

        throw new FlightBookingSystemException("Invalid username or password.");
    }
}
