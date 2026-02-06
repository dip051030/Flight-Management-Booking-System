package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.*;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Handles loading and storing Booking data from/to bookings.txt
 */
public class BookingDataManager implements DataManager {

    private static final String RESOURCE = "./resources/data/bookings.txt";

    /**
     * Loads bookings from bookings.txt and links them to Customers and Flights
     */
    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {
        File file = new File(RESOURCE);
        if (!file.exists()) {
            // If no bookings file exists, just skip
            System.out.println("No bookings.txt found, skipping load.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                lineNum++;
                line = line.trim();
                if (line.isEmpty()) continue; // skip blank lines

                // Expected format: customerId|flightId|bookingDate
                String[] parts = line.split("\\|");
                if (parts.length != 3) {
                    System.out.println("Invalid booking line " + lineNum + ": " + line);
                    continue; // skip invalid line
                }

                try {
                    int customerId = Integer.parseInt(parts[0].trim());
                    int flightId = Integer.parseInt(parts[1].trim());
                    LocalDate bookingDate = LocalDate.parse(parts[2].trim());

                    Customer customer = fbs.getCustomerByID(customerId);
                    Flight flight = fbs.getFlightByID(flightId);

                    if (customer == null) {
                        System.out.println("Booking skipped: Customer ID " + customerId + " not found.");
                        continue;
                    }
                    if (flight == null) {
                        System.out.println("Booking skipped: Flight ID " + flightId + " not found.");
                        continue;
                    }

                    Booking booking = new Booking(customer, flight, bookingDate);
                    customer.addBooking(booking);
                    flight.addPassenger(customer);
                    fbs.addBooking(booking);


                } catch (NumberFormatException | DateTimeParseException e) {
                    System.out.println("Error parsing booking line " + lineNum + ": " + line);
                }
            }
        }
    }

    /**
     * Saves all bookings from all customers to bookings.txt safely
     */
    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {
        File file = new File(RESOURCE);
        File tmpFile = new File(RESOURCE + ".tmp");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile))) {
            for (Customer customer : fbs.getCustomers().values()) {
                List<Booking> bookings = customer.getBookings();
                for (Booking booking : bookings) {
                    String line = String.format("%d|%d|%s",
                            customer.getId(),
                            booking.getFlight().getId(),
                            booking.getBookingDate().toString());
                    bw.write(line);
                    bw.newLine();
                }
            }
        }

        // Replace original file with temp file (rollback-safe)
        if (file.exists() && !file.delete()) {
            throw new IOException("Failed to delete original bookings.txt");
        }
        if (!tmpFile.renameTo(file)) {
            throw new IOException("Failed to rename bookings.tmp to bookings.txt");
        }
    }
}
