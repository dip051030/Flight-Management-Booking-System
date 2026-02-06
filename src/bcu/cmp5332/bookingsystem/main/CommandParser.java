package bcu.cmp5332.bookingsystem.main;

import bcu.cmp5332.bookingsystem.commands.AddBooking;
import bcu.cmp5332.bookingsystem.commands.AddCustomer;
import bcu.cmp5332.bookingsystem.commands.AddFlight;
import bcu.cmp5332.bookingsystem.commands.CancelBooking;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.commands.Help;
import bcu.cmp5332.bookingsystem.commands.ListCustomers;
import bcu.cmp5332.bookingsystem.commands.ListFlights;
import bcu.cmp5332.bookingsystem.commands.LoadGUI;
import bcu.cmp5332.bookingsystem.commands.Login;
import bcu.cmp5332.bookingsystem.commands.Logout;
import bcu.cmp5332.bookingsystem.commands.ShowCustomer;
import bcu.cmp5332.bookingsystem.commands.ShowFlight;

import bcu.cmp5332.bookingsystem.data.BookingDataManager;
import bcu.cmp5332.bookingsystem.data.CustomerDataManager;
import bcu.cmp5332.bookingsystem.data.FlightDataManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Parses user input strings and converts them into executable Command objects.
 *
 * <p>This class acts as a command factory in the Command Pattern architecture.
 * It interprets raw user input from the command-line interface and constructs
 * the corresponding {@link Command} implementation.</p>
 *
 * <p>The parser is responsible only for command creation. Validation,
 * authorization, and execution logic are delegated to the Command objects
 * themselves.</p>
 */
public class CommandParser {

    private static FlightDataManager flightDataManager;
    private static CustomerDataManager customerDataManager;
    private static BookingDataManager bookingDataManager;

    /**
     * Injects the required DataManager instances into the parser.
     *
     * <p>This method must be called once during application startup
     * before parsing any commands.</p>
     *
     * @param fdm flight data manager
     * @param cdm customer data manager
     * @param bdm booking data manager
     */
    public static void setDataManagers(
            FlightDataManager fdm,
            CustomerDataManager cdm,
            BookingDataManager bdm
    ) {
        flightDataManager = fdm;
        customerDataManager = cdm;
        bookingDataManager = bdm;
    }

    /**
     * Parses a single line of user input and returns the corresponding Command.
     *
     * @param line raw command-line input
     * @return a concrete Command instance
     * @throws IOException if an input error occurs
     * @throws FlightBookingSystemException if the command is invalid
     */
    public static Command parse(String line)
            throws IOException, FlightBookingSystemException {

        try {
            String[] parts = line.split(" ");
            String cmd = parts[0];

            if (cmd.equals("login") && parts.length == 3) {
                return new Login(parts[1], parts[2]);
            }

            if (cmd.equals("logout")) {
                return new Logout();
            }

            if (cmd.equals("addflight")) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

                System.out.print("Flight Number: ");
                String flightNumber = reader.readLine();

                System.out.print("Origin: ");
                String origin = reader.readLine();

                System.out.print("Destination: ");
                String destination = reader.readLine();

                LocalDate departureDate = parseDateWithAttempts(reader);

                System.out.print("Capacity (default 100): ");
                String capacityStr = reader.readLine();
                int capacity = capacityStr.isEmpty() ? 100 : Integer.parseInt(capacityStr);

                System.out.print("Price: ");
                String priceStr = reader.readLine();
                double price = priceStr.isEmpty() ? 0.0 : Double.parseDouble(priceStr);

                return new AddFlight(
                        flightNumber,
                        origin,
                        destination,
                        departureDate,
                        capacity,
                        price,
                        flightDataManager
                );
            }

            if (cmd.equals("addcustomer")) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

                System.out.print("Customer Name: ");
                String name = reader.readLine();

                System.out.print("Phone: ");
                String phone = reader.readLine();

                System.out.print("Email: ");
                String email = reader.readLine();

                System.out.print("Username: ");
                String username = reader.readLine();

                System.out.print("Password: ");
                String password = reader.readLine();

                return new AddCustomer(
                        name,
                        phone,
                        email,
                        username,
                        password,
                        customerDataManager
                );

            }

            if (cmd.equals("listflights")) {
                return new ListFlights();
            }

            if (cmd.equals("listcustomers")) {
                return new ListCustomers();
            }

            if (cmd.equals("help")) {
                return new Help();
            }

            if (cmd.equals("showflight") && parts.length == 2) {
                int id = Integer.parseInt(parts[1]);
                return new ShowFlight(id);
            }

            if (cmd.equals("showcustomer") && parts.length == 2) {
                int id = Integer.parseInt(parts[1]);
                return new ShowCustomer(id);
            }

            if (cmd.equals("loadgui")) {
                return new LoadGUI();
            }

            if (cmd.equals("addbooking") && parts.length == 3) {
                int customerId = Integer.parseInt(parts[1]);
                int flightId = Integer.parseInt(parts[2]);
                return new AddBooking(customerId, flightId, bookingDataManager);
            }

            if (cmd.equals("cancelbooking") && parts.length == 3) {
                int customerId = Integer.parseInt(parts[1]);
                int flightId = Integer.parseInt(parts[2]);
                return new CancelBooking(customerId, flightId, bookingDataManager);
            }

            if (cmd.equals("editbooking") && parts.length == 3) {
                int bookingId = Integer.parseInt(parts[1]);
                int newFlightId = Integer.parseInt(parts[2]);
                return new bcu.cmp5332.bookingsystem.commands.EditBooking(
                        bookingId,
                        newFlightId,
                        bookingDataManager
                );
            }

        } catch (NumberFormatException e) {
            throw new FlightBookingSystemException("Invalid number format.");
        }

        throw new FlightBookingSystemException("Invalid command.");
    }

    /**
     * Prompts the user to enter a valid date with a limited number of attempts.
     *
     * @param br buffered reader for input
     * @return parsed LocalDate
     * @throws IOException if an input error occurs
     * @throws FlightBookingSystemException if all attempts fail
     */
    private static LocalDate parseDateWithAttempts(BufferedReader br)
            throws IOException, FlightBookingSystemException {

        int attempts = 3;
        while (attempts-- > 0) {
            System.out.print("Departure Date (YYYY-MM-DD): ");
            try {
                return LocalDate.parse(br.readLine());
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Attempts left: " + attempts);
            }
        }

        throw new FlightBookingSystemException("Invalid departure date.");
    }
}
