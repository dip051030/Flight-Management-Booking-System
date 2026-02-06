package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * Manages the loading and storing of Flight data to/from flights.txt file.
 * Format: id::flightNumber::origin::destination::departureDate::capacity::price::deleted
 */
public class FlightDataManager implements DataManager {
    
    private final String RESOURCE = "./resources/data/flights.txt";
    
    /**
     * Loads flight data from the storage file.
     * 
     * @param fbs the flight booking system to load data into
     * @throws IOException if there's an error reading the file
     * @throws FlightBookingSystemException if there's an error parsing flight data
     */
    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException {
        
        File file = new File(RESOURCE);
        
        if (!file.exists()) {
            return;
        }

        try (Scanner sc = new Scanner(file)) {
            int line_idx = 1;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] properties = line.split(SEPARATOR, -1);
                try {
                    int id = Integer.parseInt(properties[0]);
                    String flightNumber = properties[1];
                    String origin = properties[2];
                    String destination = properties[3];
                    LocalDate departureDate = LocalDate.parse(properties[4]);
                    
                    // Handle optional fields (backwards compatible)
                    int capacity = 100;
                    double price = 0.0;
                    boolean deleted = false;
                    
                    if (properties.length > 5 && !properties[5].isEmpty()) {
                        capacity = Integer.parseInt(properties[5]);
                    }
                    if (properties.length > 6 && !properties[6].isEmpty()) {
                        price = Double.parseDouble(properties[6]);
                    }
                    if (properties.length > 7 && !properties[7].isEmpty()) {
                        deleted = Boolean.parseBoolean(properties[7]);
                    }
                    
                    Flight flight = new Flight(id, flightNumber, origin, destination, departureDate, capacity, price);
                    flight.setDeleted(deleted);
                    fbs.addFlight(flight);
                    
                } catch (NumberFormatException ex) {
                    throw new FlightBookingSystemException(
                        "Unable to parse flight id " + properties[0] + 
                        " on line " + line_idx + "\nError: " + ex
                    );
                }
                line_idx++;
            }
        }
    }
    
    /**
     * Stores all flight data to the storage file.
     * 
     * @param fbs the flight booking system containing data to save
     * @throws IOException if there's an error writing to the file
     */
    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException {

        File file = new File(RESOURCE);
        file.getParentFile().mkdirs();

        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            for (Flight flight : fbs.getFlights()) {
                out.print(flight.getId() + SEPARATOR);
                out.print(flight.getFlightNumber() + SEPARATOR);
                out.print(flight.getOrigin() + SEPARATOR);
                out.print(flight.getDestination() + SEPARATOR);
                out.print(flight.getDepartureDate() + SEPARATOR);
                out.print(flight.getCapacity() + SEPARATOR);
                out.print(flight.getPrice() + SEPARATOR);
                out.print(flight.isDeleted());
                out.println();
            }
        }
    }
}
