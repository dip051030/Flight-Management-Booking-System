package bcu.cmp5332.bookingsystem.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

/**
 * Unit tests for the Flight class.
 * Tests the new capacity and price properties added for requirement 50-59%.
 */
public class FlightTest {

    private Flight flight;
    private Customer customer1;
    private Customer customer2;

    @BeforeEach
    public void setUp() {
        flight = new Flight(1, "BA123", "London", "Paris", 
                          LocalDate.of(2024, 12, 25), 2, 150.50);
        customer1 = new Customer(1, "John Doe", "1234567890", "john@example.com");
        customer2 = new Customer(2, "Jane Smith", "0987654321", "jane@example.com");
    }

    /**
     * Test that capacity property is correctly set via constructor.
     */
    @Test
    public void testCapacitySetViaConstructor() {
        assertEquals(2, flight.getCapacity(), "Capacity should be set to 2");
    }

    /**
     * Test that capacity can be updated via setter.
     */
    @Test
    public void testCapacitySetterGetter() {
        flight.setCapacity(5);
        assertEquals(5, flight.getCapacity(), "Capacity should be updated to 5");
    }

    /**
     * Test that price property is correctly set via constructor.
     */
    @Test
    public void testPriceSetViaConstructor() {
        assertEquals(150.50, flight.getPrice(), 0.01, "Price should be set to 150.50");
    }

    /**
     * Test that price can be updated via setter.
     */
    @Test
    public void testPriceSetterGetter() {
        flight.setPrice(200.00);
        assertEquals(200.00, flight.getPrice(), 0.01, "Price should be updated to 200.00");
    }

    /**
     * Test that addPassengerSafe respects capacity limit.
     */
    @Test
    public void testAddPassengerSafe_RespectsCapacity() {
        // Capacity is 2
        assertTrue(flight.addPassengerSafe(customer1), "Should add first passenger");
        assertTrue(flight.addPassengerSafe(customer2), "Should add second passenger");
        
        Customer customer3 = new Customer(3, "Bob Jones", "5555555555", "bob@example.com");
        assertFalse(flight.addPassengerSafe(customer3), "Should NOT add third passenger (exceeds capacity)");
        
        assertEquals(2, flight.getPassengers().size(), "Should have exactly 2 passengers");
    }

    /**
     * Test that getAvailableSeats returns correct count.
     */
    @Test
    public void testGetAvailableSeats() {
        assertEquals(2, flight.getAvailableSeats(), "Should have 2 available seats initially");
        
        flight.addPassengerSafe(customer1);
        assertEquals(1, flight.getAvailableSeats(), "Should have 1 available seat after adding 1 passenger");
        
        flight.addPassengerSafe(customer2);
        assertEquals(0, flight.getAvailableSeats(), "Should have 0 available seats after adding 2 passengers");
    }

    /**
     * Test that default constructor sets default capacity and price.
     */
    @Test
    public void testDefaultConstructor() {
        Flight defaultFlight = new Flight(2, "EK456", "Dubai", "London", LocalDate.of(2024, 12, 31));
        assertEquals(100, defaultFlight.getCapacity(), "Default capacity should be 100");
        assertEquals(0.0, defaultFlight.getPrice(), 0.01, "Default price should be 0.0");
    }

    /**
     * Test that flight details include capacity and price.
     */
    @Test
    public void testGetDetailsLong_IncludesCapacityAndPrice() {
        String details = flight.getDetailsLong();
        assertTrue(details.contains("Price: $150.50"), "Details should include formatted price");
        assertTrue(details.contains("Capacity: 2"), "Details should include capacity");
    }

    /**
     * Test that deleted flights cannot accept passengers via addPassengerSafe.
     */
    @Test
    public void testAddPassengerSafe_RejectsDeletedFlight() {
        flight.setDeleted(true);
        assertFalse(flight.addPassengerSafe(customer1), "Should NOT add passenger to deleted flight");
        assertEquals(0, flight.getPassengers().size(), "Deleted flight should have 0 passengers");
    }

    /**
     * Test removing a passenger updates available seats correctly.
     */
    @Test
    public void testRemovePassenger_UpdatesAvailableSeats() {
        flight.addPassengerSafe(customer1);
        flight.addPassengerSafe(customer2);
        assertEquals(0, flight.getAvailableSeats(), "Should have 0 available seats");
        
        flight.removePassenger(customer1);
        assertEquals(1, flight.getAvailableSeats(), "Should have 1 available seat after removal");
    }
}
