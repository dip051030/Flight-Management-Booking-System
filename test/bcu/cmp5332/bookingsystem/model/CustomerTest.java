package bcu.cmp5332.bookingsystem.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

/**
 * Unit tests for the Customer class.
 * Tests the new email property added for requirement 50-59%.
 */
public class CustomerTest {

    private Customer customer;
    private Flight flight1;
    private Flight flight2;
    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    public void setUp() {
        customer = new Customer(1, "Alice Johnson", "1234567890", "alice@example.com");
        flight1 = new Flight(1, "BA123", "London", "Paris", LocalDate.of(2024, 12, 25));
        flight2 = new Flight(2, "EK456", "Dubai", "London", LocalDate.of(2024, 12, 31));
    }

    /**
     * Test that email property is correctly set via constructor.
     */
    @Test
    public void testEmailSetViaConstructor() {
        assertEquals("alice@example.com", customer.getEmail(), "Email should be set to alice@example.com");
    }

    /**
     * Test that email can be updated via setter.
     */
    @Test
    public void testEmailSetterGetter() {
        customer.setEmail("newemail@example.com");
        assertEquals("newemail@example.com", customer.getEmail(), "Email should be updated to newemail@example.com");
    }

    /**
     * Test default constructor sets empty email.
     */
    @Test
    public void testDefaultConstructor_SetsEmptyEmail() {
        Customer defaultCustomer = new Customer(2, "Bob Smith", "0987654321");
        assertEquals("", defaultCustomer.getEmail(), "Default email should be empty string");
    }

    /**
     * Test that customer details include email.
     */
    @Test
    public void testGetDetailsLong_IncludesEmail() {
        String details = customer.getDetailsLong();
        assertTrue(details.contains("Email: alice@example.com"), "Details should include email");
    }

    /**
     * Test adding bookings to customer.
     */
    @Test
    public void testAddBooking() {
        booking1 = new Booking(customer, flight1, LocalDate.now());
        customer.addBooking(booking1);
        
        assertEquals(1, customer.getBookings().size(), "Customer should have 1 booking");
        assertTrue(customer.getBookings().contains(booking1), "Customer should contain the booking");
    }

    /**
     * Test getActiveBookings filters out deleted flights.
     */
    @Test
    public void testGetActiveBookings_FiltersDeletedFlights() {
        booking1 = new Booking(customer, flight1, LocalDate.now());
        booking2 = new Booking(customer, flight2, LocalDate.now());
        
        customer.addBooking(booking1);
        customer.addBooking(booking2);
        
        assertEquals(2, customer.getActiveBookings().size(), "Should have 2 active bookings");
        
        // Delete one flight
        flight1.setDeleted(true);
        
        assertEquals(1, customer.getActiveBookings().size(), "Should have 1 active booking after deleting flight1");
        assertFalse(customer.getActiveBookings().contains(booking1), "Should not contain booking for deleted flight");
        assertTrue(customer.getActiveBookings().contains(booking2), "Should contain booking for active flight");
    }

    /**
     * Test that deleted customer can still be marked.
     */
    @Test
    public void testDeletedFlag() {
        assertFalse(customer.isDeleted(), "Customer should not be deleted initially");
        
        customer.setDeleted(true);
        assertTrue(customer.isDeleted(), "Customer should be marked as deleted");
    }

    /**
     * Test booking count in details.
     */
    @Test
    public void testGetDetailsLong_ShowsBookingCount() {
        booking1 = new Booking(customer, flight1, LocalDate.now());
        booking2 = new Booking(customer, flight2, LocalDate.now());
        customer.addBooking(booking1);
        customer.addBooking(booking2);
        
        String details = customer.getDetailsLong();
        assertTrue(details.contains("Number of Bookings: 2"), "Details should show 2 bookings");
    }

    /**
     * Test that email validation is not enforced (allows any string).
     */
    @Test
    public void testEmail_AllowsAnyString() {
        customer.setEmail("not-an-email");
        assertEquals("not-an-email", customer.getEmail(), "Should allow any string as email");
        
        customer.setEmail("");
        assertEquals("", customer.getEmail(), "Should allow empty email");
    }

    /**
     * Test toString returns detailed information.
     */
    @Test
    public void testToString_ReturnsDetails() {
        String toString = customer.toString();
        assertTrue(toString.contains("Alice Johnson"), "toString should contain customer name");
        assertTrue(toString.contains("alice@example.com"), "toString should contain email");
    }
}
