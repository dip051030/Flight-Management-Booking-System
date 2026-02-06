package bcu.cmp5332.bookingsystem.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer in the Flight Booking System.
 *
 * A customer may authenticate into the system and hold multiple bookings.
 * Deleted customers are soft-deleted and preserved for history.
 */
public class Customer {

    private int id;
    private String name;
    private String phone;
    private String email;
    private String passwordHash;
    private final List<Booking> bookings = new ArrayList<>();
    private boolean deleted = false;

    /**
     * Creates a customer without authentication credentials.
     *
     * @param id customer ID
     * @param name customer name
     * @param phone phone number
     * @param email email address
     */
    public Customer(int id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    /**
     * Creates a customer with authentication credentials.
     *
     * @param id customer ID
     * @param name customer name
     * @param phone phone number
     * @param email email address (used as login)
     * @param passwordHash hashed password
     */
    public Customer(int id, String name, String phone, String email, String passwordHash) {
        this(id, name, phone, email);
        this.passwordHash = passwordHash;
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

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    /**
     * Returns only bookings whose flights are still active.
     *
     * @return list of active bookings
     */
    public List<Booking> getActiveBookings() {
        List<Booking> active = new ArrayList<>();
        for (Booking b : bookings) {
            if (!b.getFlight().isDeleted()) {
                active.add(b);
            }
        }
        return active;
    }

    @Override
    public String toString() {
        return "Customer #" + id + " - " + name;
    }
}
