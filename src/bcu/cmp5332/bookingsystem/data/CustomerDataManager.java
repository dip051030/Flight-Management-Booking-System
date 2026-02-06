package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.*;

/**
 * Handles persistence of Customer data to and from the customers.txt file.
 *
 * <p>
 * Email is used as the login username.
 * Passwords are stored as hashed values.
 * </p>
 *
 * <p>
 * Storage format:
 * </p>
 * <pre>
 * id::name::phone::email::password::deleted
 * </pre>
 */
public class CustomerDataManager implements DataManager {

    private static final String RESOURCE = "./resources/data/customers.txt";

    @Override
    public void loadData(FlightBookingSystem fbs)
            throws IOException, FlightBookingSystemException {

        File file = new File(RESOURCE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("::", -1);

                if (parts.length < 4) {
                    throw new FlightBookingSystemException("Invalid customer entry: " + line);
                }

                int id;
                try {
                    id = Integer.parseInt(parts[0]);
                } catch (NumberFormatException e) {
                    throw new FlightBookingSystemException("Invalid customer ID: " + parts[0]);
                }

                String name = parts[1];
                String phone = parts[2];
                String email = parts[3];
                String password = parts.length > 4 ? parts[4] : "";
                boolean deleted = parts.length > 5 && !parts[5].isEmpty()
                        ? Boolean.parseBoolean(parts[5])
                        : false;

                Customer customer = new Customer(
                        id,
                        name,
                        phone,
                        email,
                        password
                );

                customer.setDeleted(deleted);
                fbs.addCustomer(customer);
            }
        }
    }

    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {

        File file = new File(RESOURCE);
        File tempFile = new File(RESOURCE + ".tmp");

        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {
            for (Customer customer : fbs.getCustomers().values()) {
                writer.printf(
                        "%d::%s::%s::%s::%s::%b%n",
                        customer.getId(),
                        customer.getName(),
                        customer.getPhone(),
                        customer.getEmail(),
                        customer.getPasswordHash(),
                        customer.isDeleted()
                );
            }
        }

        if (file.exists() && !file.delete()) {
            throw new IOException("Failed to delete old customer data");
        }

        if (!tempFile.renameTo(file)) {
            throw new IOException("Failed to save customer data");
        }
    }
}
