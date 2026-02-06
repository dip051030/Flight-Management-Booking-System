package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.auth.AuthService;
import bcu.cmp5332.bookingsystem.commands.DeleteCustomer;
import bcu.cmp5332.bookingsystem.commands.DeleteFlight;
import bcu.cmp5332.bookingsystem.data.*;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

/**
 * Main graphical user interface for the Flight Booking System.
 *
 * <p>This window provides role-based access control:
 * <ul>
 *   <li><b>Admin</b> users can manage flights, customers, and bookings.</li>
 *   <li><b>Customer</b> users can browse flights and create bookings.</li>
 * </ul>
 *
 * <p>The interface dynamically adapts based on the authenticated user's role
 * using {@link AuthService}. All sensitive actions are additionally protected
 * at the command level.</p>
 */
public class MainWindow extends JFrame implements ActionListener {

    private JMenuBar menuBar;
    private JMenu adminMenu, flightsMenu, bookingsMenu, customersMenu;

    private JMenuItem adminExit;
    private JMenuItem flightsView, flightsAdd, flightsViewPassengers, flightsDelete;
    private JMenuItem bookingsView, bookingsIssue, bookingsCancel, bookingsEdit;
    private JMenuItem custView, custAdd, custViewBookings, custDelete;

    private FlightBookingSystem fbs;
    private BookingDataManager bookingDataManager;
    private JTable currentTable;

    private JPanel centerPanel;
    private JLabel statusLabel;

    /**
     * Constructs the main window using a default {@link BookingDataManager}.
     *
     * @param fbs the flight booking system instance
     */
    public MainWindow(FlightBookingSystem fbs) {
        this(fbs, new BookingDataManager());
    }

    /**
     * Constructs the main window with explicit dependencies.
     *
     * @param fbs the flight booking system instance
     * @param bookingDataManager the booking data manager
     */
    public MainWindow(FlightBookingSystem fbs, BookingDataManager bookingDataManager) {
        this.fbs = fbs;
        this.bookingDataManager = bookingDataManager;
        initialize();
    }

    /**
     * Initializes the GUI layout, menus, and event handlers.
     * Applies role-based UI restrictions before displaying the window.
     */
    private void initialize() {
        setTitle("Flight Booking System");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        buildMenuBar();
        buildHeader();
        buildCenterPanel();
        buildStatusBar();

        applyRoleRestrictions();
        setVisible(true);
    }

    /**
     * Builds the application menu bar.
     */
    private void buildMenuBar() {
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        adminMenu = new JMenu("Admin");
        flightsMenu = new JMenu("Flights");
        bookingsMenu = new JMenu("Bookings");
        customersMenu = new JMenu("Customers");

        menuBar.add(adminMenu);
        menuBar.add(flightsMenu);
        menuBar.add(bookingsMenu);
        menuBar.add(customersMenu);

        adminExit = new JMenuItem("Exit");
        adminExit.addActionListener(this);
        adminMenu.add(adminExit);

        flightsView = new JMenuItem("View All");
        flightsAdd = new JMenuItem("Add");
        flightsViewPassengers = new JMenuItem("View Passengers");
        flightsDelete = new JMenuItem("Delete");

        flightsMenu.add(flightsView);
        flightsMenu.add(flightsAdd);
        flightsMenu.addSeparator();
        flightsMenu.add(flightsViewPassengers);
        flightsMenu.add(flightsDelete);

        bookingsView = new JMenuItem("View");
        bookingsIssue = new JMenuItem("Issue");
        bookingsCancel = new JMenuItem("Cancel");
        bookingsEdit = new JMenuItem("Edit");

        bookingsMenu.add(bookingsView);
        bookingsMenu.add(bookingsIssue);
        bookingsMenu.add(bookingsCancel);
        bookingsMenu.add(bookingsEdit);

        custView = new JMenuItem("View All");
        custAdd = new JMenuItem("Add");
        custViewBookings = new JMenuItem("View Bookings");
        custDelete = new JMenuItem("Delete");

        customersMenu.add(custView);
        customersMenu.add(custAdd);
        customersMenu.addSeparator();
        customersMenu.add(custViewBookings);
        customersMenu.add(custDelete);

        for (JMenuItem item : new JMenuItem[]{
                flightsView, flightsAdd, flightsViewPassengers, flightsDelete,
                bookingsView, bookingsIssue, bookingsCancel, bookingsEdit,
                custView, custAdd, custViewBookings, custDelete
        }) {
            item.addActionListener(this);
        }
    }


    /**
     * Prompts the user for a flight ID and displays its passengers.
     *
     * @throws FlightBookingSystemException if the flight does not exist
     */
    private void viewPassengersForFlight() throws FlightBookingSystemException {
        String input = JOptionPane.showInputDialog(this, "Enter Flight ID:");
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try {
            int flightId = Integer.parseInt(input.trim());
            Flight flight = fbs.getFlightByID(flightId);
            new ViewPassengersWindow(this, flight);
        } catch (NumberFormatException e) {
            throw new FlightBookingSystemException("Invalid Flight ID format.");
        }
    }


    /**
     * Prompts the administrator for a customer ID and displays their bookings.
     *
     * @throws FlightBookingSystemException if access is denied or customer not found
     */
    private void viewBookingsForCustomer() throws FlightBookingSystemException {
        AuthService.requireAdmin();

        String input = JOptionPane.showInputDialog(this, "Enter Customer ID:");
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try {
            int customerId = Integer.parseInt(input.trim());
            Customer customer = fbs.getCustomerByID(customerId);
            new ViewCustomerBookingsWindow(this, customer);
        } catch (NumberFormatException e) {
            throw new FlightBookingSystemException("Invalid Customer ID format.");
        }
    }

    /**
     * Performs a soft delete of a flight (admin only).
     *
     * @throws FlightBookingSystemException if access is denied or flight not found
     */
    private void deleteFlight() throws FlightBookingSystemException {
        AuthService.requireAdmin();

        String input = JOptionPane.showInputDialog(this, "Enter Flight ID to delete:");
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try {
            int flightId = Integer.parseInt(input.trim());
            new DeleteFlight(flightId, new FlightDataManager()).execute(fbs);
            displayFlights();
        } catch (NumberFormatException e) {
            throw new FlightBookingSystemException("Invalid Flight ID format.");
        }
    }



    /**
     * Performs a soft delete of a customer.
     *
     * <p>This action is restricted to administrators. The customer is marked
     * as deleted but their historical data is preserved.</p>
     *
     * @throws FlightBookingSystemException if access is denied or input is invalid
     */
    private void deleteCustomer() throws FlightBookingSystemException {
        AuthService.requireAdmin();

        String input = JOptionPane.showInputDialog(this, "Enter Customer ID to delete:");
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try {
            int customerId = Integer.parseInt(input.trim());
            new DeleteCustomer(customerId, new CustomerDataManager()).execute(fbs);
            displayCustomers();
        } catch (NumberFormatException e) {
            throw new FlightBookingSystemException("Invalid Customer ID format.");
        }
    }

    /**
     * Cancels an existing booking.
     *
     * <p>This operation is restricted to administrators. The booking is removed
     * from the system and the associated seat is released.</p>
     *
     * @throws FlightBookingSystemException if access is denied or booking not found
     */
    private void cancelBooking() throws FlightBookingSystemException {
        AuthService.requireAdmin();

        String custInput = JOptionPane.showInputDialog(this, "Enter Customer ID:");
        String flightInput = JOptionPane.showInputDialog(this, "Enter Flight ID:");

        if (custInput == null || flightInput == null ||
                custInput.trim().isEmpty() || flightInput.trim().isEmpty()) {
            return;
        }

        try {
            int customerId = Integer.parseInt(custInput.trim());
            int flightId = Integer.parseInt(flightInput.trim());

            Booking target = null;
            for (Booking b : fbs.getBookings()) {
                if (b.getCustomer().getId() == customerId &&
                        b.getFlight().getId() == flightId) {
                    target = b;
                    break;
                }
            }

            if (target == null) {
                throw new FlightBookingSystemException("Booking not found.");
            }

            fbs.removeBooking(target);
            bookingDataManager.storeData(fbs);
            displayBookings();

        } catch (NumberFormatException e) {
            throw new FlightBookingSystemException("Invalid ID format.");
        } catch (IOException e) {
            throw new FlightBookingSystemException("Failed to save booking changes.");
        }
    }


    /**
     * Builds the top header panel.
     */
    private void buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel title = new JLabel("Flight Booking System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel role = new JLabel(
                AuthService.isAdmin() ? "Role: Admin" : "Role: Customer"
        );
        role.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        header.add(title, BorderLayout.WEST);
        header.add(role, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
    }

    /**
     * Builds the main content panel.
     */
    private void buildCenterPanel() {
        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel placeholder = new JLabel(
                "Select an option from the menu to begin",
                JLabel.CENTER
        );
        placeholder.setFont(new Font("Segoe UI", Font.ITALIC, 14));

        centerPanel.add(placeholder, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Builds the status bar.
     */
    private void buildStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        statusLabel = new JLabel(
                AuthService.isAdmin()
                        ? "Logged in as Admin"
                        : "Logged in as Customer"
        );
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        statusBar.add(statusLabel, BorderLayout.WEST);
        add(statusBar, BorderLayout.SOUTH);
    }

    /**
     * Applies role-based restrictions to the GUI.
     * Customers are prevented from accessing administrative actions.
     */
    private void applyRoleRestrictions() {
        if (AuthService.isCustomer()) {
            adminMenu.setVisible(false);
            flightsAdd.setEnabled(false);
            flightsDelete.setEnabled(false);
            custAdd.setEnabled(false);
            custDelete.setEnabled(false);
            bookingsEdit.setEnabled(false);
            bookingsCancel.setEnabled(false);
        }
    }

    /**
     * Handles menu action events and dispatches them to the appropriate handlers.
     *
     * @param ae the action event
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        Object src = ae.getSource();

        try {
            if (src == adminExit) {
                FlightBookingSystemData.store(fbs);
                bookingDataManager.storeData(fbs);
                System.exit(0);
            }

            if (src == flightsView) displayFlights();
            if (src == flightsAdd) new AddFlightWindow(this, fbs);
            if (src == flightsViewPassengers) viewPassengersForFlight();
            if (src == flightsDelete) deleteFlight();

            if (src == custView) displayCustomers();
            if (src == custAdd) new AddCustomerWindow(this, fbs);
            if (src == custViewBookings) viewBookingsForCustomer();
            if (src == custDelete) deleteCustomer();

            if (src == bookingsView) displayBookings();
            if (src == bookingsIssue) new AddBookingWindow(this, fbs, bookingDataManager);
            if (src == bookingsCancel) cancelBooking();
            if (src == bookingsEdit) new EditBookingWindow(this, fbs, bookingDataManager);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    /**
     * Reloads all system data from persistent storage to keep the GUI synchronized.
     */
    private void refreshDataFromFiles() {
        try {
            fbs = FlightBookingSystemData.load();
        } catch (FlightBookingSystemException | IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to refresh data.");
        }
    }

    /**
     * Displays all flights in a tabular view.
     */
    public void displayFlights() {
        refreshDataFromFiles();
        List<Flight> flights = fbs.getFlights();

        String[] cols = {"ID", "Number", "Origin", "Destination", "Date", "Price", "Capacity", "Booked"};
        Object[][] data = new Object[flights.size()][8];

        for (int i = 0; i < flights.size(); i++) {
            Flight f = flights.get(i);
            data[i] = new Object[]{
                    f.getId(), f.getFlightNumber(), f.getOrigin(),
                    f.getDestination(), f.getDepartureDate(),
                    String.format("$%.2f", f.getPrice()),
                    f.getCapacity(), f.getPassengers().size()
            };
        }

        showTable(data, cols);
    }

    /**
     * Displays all customers along with their booking counts.
     */
    public void displayCustomers() {
        refreshDataFromFiles();

        String[] cols = {"ID", "Name", "Phone", "Email", "Bookings"};
        Object[][] data = new Object[fbs.getCustomers().size()][5];

        int i = 0;
        for (Customer c : fbs.getCustomers().values()) {
            data[i++] = new Object[]{
                    c.getId(), c.getName(), c.getPhone(),
                    c.getEmail(), c.getBookings().size()
            };
        }

        showTable(data, cols);
    }

    /**
     * Displays all bookings in a tabular view.
     */
    public void displayBookings() {
        refreshDataFromFiles();
        List<Booking> bookings = fbs.getBookings();

        String[] cols = {"Booking ID", "Customer", "Flight", "Date", "Price"};
        Object[][] data = new Object[bookings.size()][5];

        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            data[i] = new Object[]{
                    b.getId(),
                    b.getCustomer().getName(),
                    b.getFlight().getFlightNumber(),
                    b.getBookingDate(),
                    String.format("$%.2f", b.getFlight().getPrice())
            };
        }

        showTable(data, cols);
    }

    /**
     * Renders a table in the center panel.
     *
     * @param data table data
     * @param cols column headers
     */
    private void showTable(Object[][] data, String[] cols) {
        centerPanel.removeAll();
        currentTable = new JTable(data, cols);
        centerPanel.add(new JScrollPane(currentTable), BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    /**
     * Provides access to the current FlightBookingSystem instance.
     *
     * @return the FlightBookingSystem used by the GUI
     */
    public FlightBookingSystem getFlightBookingSystem() {
        return fbs;
    }
}
