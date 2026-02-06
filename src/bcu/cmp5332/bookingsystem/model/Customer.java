package bcu.cmp5332.bookingsystem.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer in the Flight Booking System.
 *
 * Email is used as the login username.
 */
public class Customer {

    private int id;
    private String name;
    private String phone;
    private String email;
    private String password;

    private final List<Booking> bookings = new ArrayList<>();
    private boolean deleted = false;

    public Customer(int id, String name, String phone, String email, String password) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    /**
     * Returns the customer's email.
     *
     * @return email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the username used for login.
     *
     * Email acts as the username.
     *
     * @return username (email)
     */
    public String getUsername() {
        return email;
    }

    /**
     * Returns the customer's password.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Returns all active bookings for this customer.
     *
     * A booking is considered active if the associated flight
     * has not been deleted.
     *
     * @return list of active bookings
     */
    public List<Booking> getActiveBookings() {
        List<Booking> active = new ArrayList<>();
        for (Booking booking : bookings) {
            if (!booking.getFlight().isDeleted()) {
                active.add(booking);
            }
        }
        return active;
    }

}
