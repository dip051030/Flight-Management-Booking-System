package bcu.cmp5332.bookingsystem.auth;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;

/**
 * Central authentication and authorization service for the
 * Flight Booking System.
 *
 * <p>
 * This class manages login state and enforces role-based
 * access control for administrators and customers.
 * </p>
 *
 * <p>
 * Admin authentication uses fixed credentials.
 * Customer authentication uses email and password.
 * </p>
 */
public final class AuthService {

    /**
     * Supported user roles.
     */
    public enum Role {
        ADMIN,
        CUSTOMER
    }

    private static boolean loggedIn = false;
    private static Role currentRole = null;
    private static Customer loggedInCustomer = null;

    private AuthService() {}

    /**
     * Logs in an administrator using username and password.
     *
     * @param username the admin username
     * @param password the admin password
     * @throws SecurityException if credentials are invalid
     */
    public static void loginAdmin(String username, String password) {
        if (!"admin".equals(username) || !"admin123".equals(password)) {
            throw new SecurityException("Invalid admin credentials");
        }

        loggedIn = true;
        currentRole = Role.ADMIN;
        loggedInCustomer = null;
    }

    /**
     * Authenticates a customer using a raw password.
     *
     * @param customer the customer attempting login
     * @param rawPassword the password entered by the user
     * @throws FlightBookingSystemException if authentication fails
     */
    public static void loginCustomer(Customer customer, String rawPassword)
            throws FlightBookingSystemException {

        if (customer.getPasswordHash() == null || customer.getPasswordHash().isEmpty()) {
            throw new FlightBookingSystemException(
                    "This account was created before authentication was added. Please re-register."
            );
        }

        if (!PasswordUtil.verify(rawPassword, customer.getPasswordHash())) {
            throw new FlightBookingSystemException("Invalid email or password.");
        }

        loggedIn = true;
        currentRole = Role.CUSTOMER;
        loggedInCustomer = customer;
    }

    /**
     * Starts an authenticated session for a newly registered customer.
     *
     * @param customer the customer to authenticate
     */
    public static void startCustomerSession(Customer customer) {
        loggedIn = true;
        currentRole = Role.CUSTOMER;
        loggedInCustomer = customer;
    }

    /**
     * Logs out the currently logged-in user.
     */
    public static void logout() {
        loggedIn = false;
        currentRole = null;
        loggedInCustomer = null;
    }

    /**
     * Ensures that a user is logged in.
     *
     * @throws SecurityException if no user is logged in
     */
    public static void requireLogin() {
        if (!loggedIn) {
            throw new SecurityException("Login required");
        }
    }

    /**
     * Ensures that the logged-in user is an administrator.
     *
     * @throws SecurityException if the user is not an admin
     */
    public static void requireAdmin() {
        requireLogin();
        if (currentRole != Role.ADMIN) {
            throw new SecurityException("Administrator access required");
        }
    }

    /**
     * Ensures that the logged-in user is a customer.
     *
     * @throws SecurityException if the user is not a customer
     */
    public static void requireCustomer() {
        requireLogin();
        if (currentRole != Role.CUSTOMER) {
            throw new SecurityException("Customer access required");
        }
    }

    /**
     * Checks whether an administrator is logged in.
     *
     * @return true if admin is logged in
     */
    public static boolean isAdmin() {
        return loggedIn && currentRole == Role.ADMIN;
    }

    /**
     * Checks whether a customer is logged in.
     *
     * @return true if customer is logged in
     */
    public static boolean isCustomer() {
        return loggedIn && currentRole == Role.CUSTOMER;
    }

    /**
     * Returns the currently logged-in customer.
     *
     * @return the logged-in customer
     * @throws SecurityException if no customer is logged in
     */
    public static Customer getLoggedInCustomer() {
        requireCustomer();
        return loggedInCustomer;
    }

    /**
     * Alias for retrieving the currently logged-in customer.
     *
     * @return the logged-in customer
     */
    public static Customer currentCustomer() {
        return getLoggedInCustomer();
    }
}