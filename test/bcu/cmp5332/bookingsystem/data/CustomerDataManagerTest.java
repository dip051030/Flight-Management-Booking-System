package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

/**
 * Integration tests for CustomerDataManager.
 * Tests that email is correctly saved to and loaded from files.
 */
public class CustomerDataManagerTest {

    private CustomerDataManager dataManager;
    private FlightBookingSystem fbs;
    private static final String TEST_FILE = "./resources/data/test_customers.txt";

    @BeforeEach
    public void setUp() throws Exception {
        dataManager = new CustomerDataManager() {
            @Override
            public void storeData(FlightBookingSystem fbs) throws java.io.IOException {
                File file = new File(TEST_FILE);
                File tempFile = new File(TEST_FILE + ".tmp");

                file.getParentFile().mkdirs();

                try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(tempFile))) {
                    for (Customer customer : fbs.getCustomers().values()) {
                        writer.printf("%d::%s::%s::%s%n",
                                customer.getId(),
                                customer.getName(),
                                customer.getPhone(),
                                customer.getEmail());
                    }
                }

                if (file.exists() && !file.delete()) {
                    throw new java.io.IOException("Failed to delete old customer data");
                }

                if (!tempFile.renameTo(file)) {
                    throw new java.io.IOException("Failed to save customer data");
                }
            }

            @Override
            public void loadData(FlightBookingSystem fbs) throws java.io.IOException, bcu.cmp5332.bookingsystem.main.FlightBookingSystemException {
                File file = new File(TEST_FILE);
                if (!file.exists()) return;

                try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty()) continue;

                        String[] parts = line.split("::", -1);
                        if (parts.length < 3) continue;

                        int id = Integer.parseInt(parts[0]);
                        String name = parts[1];
                        String phone = parts[2];
                        String email = (parts.length > 3) ? parts[3] : "";

                        Customer customer = new Customer(id, name, phone, email);
                        fbs.addCustomer(customer);
                    }
                }
            }
        };
        fbs = new FlightBookingSystem();
    }

    @AfterEach
    public void tearDown() {
        File testFile = new File(TEST_FILE);
        if (testFile.exists()) {
            testFile.delete();
        }
        File tmpFile = new File(TEST_FILE + ".tmp");
        if (tmpFile.exists()) {
            tmpFile.delete();
        }
    }

    /**
     * Test that email is saved correctly to file.
     */
    @Test
    public void testStoreData_SavesEmail() throws Exception {
        Customer customer = new Customer(1, "Alice Johnson", "1234567890", "alice@example.com");
        fbs.addCustomer(customer);

        dataManager.storeData(fbs);

        File file = new File(TEST_FILE);
        assertTrue(file.exists(), "Test file should be created");

        try (java.util.Scanner sc = new java.util.Scanner(file)) {
            assertTrue(sc.hasNextLine(), "File should have content");
            String line = sc.nextLine();
            assertTrue(line.contains("alice@example.com"), "Should contain email alice@example.com");
        }
    }

    /**
     * Test that email is loaded correctly from file.
     */
    @Test
    public void testLoadData_LoadsEmail() throws Exception {
        Customer originalCustomer = new Customer(1, "Bob Smith", "0987654321", "bob@example.com");
        fbs.addCustomer(originalCustomer);
        dataManager.storeData(fbs);

        FlightBookingSystem newFbs = new FlightBookingSystem();
        dataManager.loadData(newFbs);

        assertEquals(1, newFbs.getCustomers().size(), "Should have 1 customer loaded");
        
        Customer loadedCustomer = newFbs.getCustomerByID(1);
        assertEquals("bob@example.com", loadedCustomer.getEmail(), "Loaded email should be bob@example.com");
        assertEquals("Bob Smith", loadedCustomer.getName(), "Customer name should match");
    }

    /**
     * Test backwards compatibility with old format (no email).
     */
    @Test
    public void testLoadData_BackwardsCompatible() throws Exception {
        File file = new File(TEST_FILE);
        file.getParentFile().mkdirs();
        try (java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileWriter(file))) {
            out.println("1::John Doe::1234567890::");
        }

        FlightBookingSystem newFbs = new FlightBookingSystem();
        dataManager.loadData(newFbs);

        assertEquals(1, newFbs.getCustomers().size(), "Should load old format customer");
        Customer customer = newFbs.getCustomerByID(1);
        assertEquals("", customer.getEmail(), "Should use empty string for missing email");
    }

    /**
     * Test that multiple customers are stored and loaded correctly.
     */
    @Test
    public void testStoreAndLoadMultipleCustomers() throws Exception {
        Customer customer1 = new Customer(1, "Alice Johnson", "111", "alice@example.com");
        Customer customer2 = new Customer(2, "Bob Smith", "222", "bob@example.com");
        Customer customer3 = new Customer(3, "Charlie Brown", "333", "charlie@example.com");
        
        fbs.addCustomer(customer1);
        fbs.addCustomer(customer2);
        fbs.addCustomer(customer3);
        dataManager.storeData(fbs);

        FlightBookingSystem newFbs = new FlightBookingSystem();
        dataManager.loadData(newFbs);

        assertEquals(3, newFbs.getCustomers().size(), "Should load 3 customers");
        
        assertEquals("alice@example.com", newFbs.getCustomerByID(1).getEmail());
        assertEquals("bob@example.com", newFbs.getCustomerByID(2).getEmail());
        assertEquals("charlie@example.com", newFbs.getCustomerByID(3).getEmail());
    }

    /**
     * Test that empty email is handled correctly.
     */
    @Test
    public void testStoreAndLoad_EmptyEmail() throws Exception {
        Customer customer = new Customer(1, "Test User", "999", "");
        fbs.addCustomer(customer);
        dataManager.storeData(fbs);

        FlightBookingSystem newFbs = new FlightBookingSystem();
        dataManager.loadData(newFbs);

        Customer loadedCustomer = newFbs.getCustomerByID(1);
        assertEquals("", loadedCustomer.getEmail(), "Empty email should be preserved");
    }
}
