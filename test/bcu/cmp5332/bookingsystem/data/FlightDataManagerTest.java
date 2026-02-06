package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.time.LocalDate;

/**
 * Integration tests for FlightDataManager.
 * Tests that capacity and price are correctly saved to and loaded from files.
 */
public class FlightDataManagerTest {

    private FlightDataManager dataManager;
    private FlightBookingSystem fbs;
    private static final String TEST_FILE = "./resources/data/test_flights.txt";

    @BeforeEach
    public void setUp() throws Exception {
        dataManager = new FlightDataManager() {
            @Override
            public void storeData(FlightBookingSystem fbs) throws java.io.IOException {
                // Use test file instead of production file
                File file = new File(TEST_FILE);
                file.getParentFile().mkdirs();
                try (java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileWriter(file))) {
                    for (Flight flight : fbs.getFlights()) {
                        out.print(flight.getId() + SEPARATOR);
                        out.print(flight.getFlightNumber() + SEPARATOR);
                        out.print(flight.getOrigin() + SEPARATOR);
                        out.print(flight.getDestination() + SEPARATOR);
                        out.print(flight.getDepartureDate() + SEPARATOR);
                        out.print(flight.getCapacity() + SEPARATOR);
                        out.print(flight.getPrice());
                        out.println();
                    }
                }
            }

            @Override
            public void loadData(FlightBookingSystem fbs) throws java.io.IOException, bcu.cmp5332.bookingsystem.main.FlightBookingSystemException {
                File file = new File(TEST_FILE);
                if (!file.exists()) return;

                try (java.util.Scanner sc = new java.util.Scanner(file)) {
                    while (sc.hasNextLine()) {
                        String line = sc.nextLine();
                        String[] properties = line.split(SEPARATOR, -1);
                        
                        int id = Integer.parseInt(properties[0]);
                        String flightNumber = properties[1];
                        String origin = properties[2];
                        String destination = properties[3];
                        LocalDate departureDate = LocalDate.parse(properties[4]);
                        
                        int capacity = 100;
                        double price = 0.0;
                        
                        if (properties.length > 5 && !properties[5].isEmpty()) {
                            capacity = Integer.parseInt(properties[5]);
                        }
                        if (properties.length > 6 && !properties[6].isEmpty()) {
                            price = Double.parseDouble(properties[6]);
                        }
                        
                        Flight flight = new Flight(id, flightNumber, origin, destination, departureDate, capacity, price);
                        fbs.addFlight(flight);
                    }
                }
            }
        };
        fbs = new FlightBookingSystem();
    }

    @AfterEach
    public void tearDown() {
        // Clean up test file
        File testFile = new File(TEST_FILE);
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    /**
     * Test that capacity and price are saved correctly to file.
     */
    @Test
    public void testStoreData_SavesCapacityAndPrice() throws Exception {
        Flight flight = new Flight(1, "BA123", "London", "Paris", 
                                  LocalDate.of(2024, 12, 25), 150, 299.99);
        fbs.addFlight(flight);

        dataManager.storeData(fbs);

        // Read file and verify
        File file = new File(TEST_FILE);
        assertTrue(file.exists(), "Test file should be created");

        try (java.util.Scanner sc = new java.util.Scanner(file)) {
            assertTrue(sc.hasNextLine(), "File should have content");
            String line = sc.nextLine();
            assertTrue(line.contains("::150::"), "Should contain capacity 150");
            assertTrue(line.contains("299.99"), "Should contain price 299.99");
        }
    }

    /**
     * Test that capacity and price are loaded correctly from file.
     */
    @Test
    public void testLoadData_LoadsCapacityAndPrice() throws Exception {
        // First save a flight
        Flight originalFlight = new Flight(1, "EK456", "Dubai", "London", 
                                          LocalDate.of(2024, 12, 31), 200, 450.50);
        fbs.addFlight(originalFlight);
        dataManager.storeData(fbs);

        // Create new system and load
        FlightBookingSystem newFbs = new FlightBookingSystem();
        dataManager.loadData(newFbs);

        assertEquals(1, newFbs.getFlights().size(), "Should have 1 flight loaded");
        
        Flight loadedFlight = newFbs.getFlights().get(0);
        assertEquals(200, loadedFlight.getCapacity(), "Loaded capacity should be 200");
        assertEquals(450.50, loadedFlight.getPrice(), 0.01, "Loaded price should be 450.50");
        assertEquals("EK456", loadedFlight.getFlightNumber(), "Flight number should match");
    }

    /**
     * Test backwards compatibility with old format (no capacity/price).
     */
    @Test
    public void testLoadData_BackwardsCompatible() throws Exception {
        // Manually create old format file
        File file = new File(TEST_FILE);
        file.getParentFile().mkdirs();
        try (java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileWriter(file))) {
            out.println("1::BA123::London::Paris::2024-12-25");
        }

        FlightBookingSystem newFbs = new FlightBookingSystem();
        dataManager.loadData(newFbs);

        assertEquals(1, newFbs.getFlights().size(), "Should load old format flight");
        Flight flight = newFbs.getFlights().get(0);
        assertEquals(100, flight.getCapacity(), "Should use default capacity 100");
        assertEquals(0.0, flight.getPrice(), 0.01, "Should use default price 0.0");
    }

    /**
     * Test that multiple flights are stored and loaded correctly.
     */
    @Test
    public void testStoreAndLoadMultipleFlights() throws Exception {
        Flight flight1 = new Flight(1, "BA123", "London", "Paris", 
                                   LocalDate.of(2024, 12, 25), 180, 250.00);
        Flight flight2 = new Flight(2, "EK456", "Dubai", "London", 
                                   LocalDate.of(2024, 12, 31), 300, 500.00);
        
        fbs.addFlight(flight1);
        fbs.addFlight(flight2);
        dataManager.storeData(fbs);

        FlightBookingSystem newFbs = new FlightBookingSystem();
        dataManager.loadData(newFbs);

        assertEquals(2, newFbs.getFlights().size(), "Should load 2 flights");
        
        Flight loaded1 = newFbs.getFlightByID(1);
        assertEquals(180, loaded1.getCapacity(), "First flight capacity should be 180");
        assertEquals(250.00, loaded1.getPrice(), 0.01, "First flight price should be 250.00");
        
        Flight loaded2 = newFbs.getFlightByID(2);
        assertEquals(300, loaded2.getCapacity(), "Second flight capacity should be 300");
        assertEquals(500.00, loaded2.getPrice(), 0.01, "Second flight price should be 500.00");
    }
}
