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
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

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
        adminMenu.add(adminExit);
        adminExit.addActionListener(this);

        flightsView = new JMenuItem("View All");
        flightsAdd = new JMenuItem("Add");
        flightsViewPassengers = new JMenuItem("View Passengers");
        flightsDelete = new JMenuItem("Delete");

        flightsMenu.add(flightsView);
        flightsMenu.add(flightsAdd);
        flightsMenu.addSeparator();
        flightsMenu.add(flightsViewPassengers);
        flightsMenu.add(flightsDelete);

        flightsView.addActionListener(this);
        flightsAdd.addActionListener(this);
        flightsViewPassengers.addActionListener(this);
        flightsDelete.addActionListener(this);

        bookingsView = new JMenuItem("View");
        bookingsIssue = new JMenuItem("Issue");
        bookingsCancel = new JMenuItem("Cancel");
        bookingsEdit = new JMenuItem("Edit");

        bookingsMenu.add(bookingsView);
        bookingsMenu.add(bookingsIssue);
        bookingsMenu.add(bookingsCancel);
        bookingsMenu.add(bookingsEdit);

        bookingsView.addActionListener(this);
        bookingsIssue.addActionListener(this);
        bookingsCancel.addActionListener(this);
        bookingsEdit.addActionListener(this);

        custView = new JMenuItem("View All");
        custAdd = new JMenuItem("Add");
        custViewBookings = new JMenuItem("View Bookings");
        custDelete = new JMenuItem("Delete");

        customersMenu.add(custView);
        customersMenu.add(custAdd);
        customersMenu.addSeparator();
        customersMenu.add(custViewBookings);
        customersMenu.add(custDelete);

        custView.addActionListener(this);
        custAdd.addActionListener(this);
        custViewBookings.addActionListener(this);
        custDelete.addActionListener(this);

        JLabel welcome = new JLabel(
                "Welcome to Flight Booking System - Double-click rows for details",
                JLabel.CENTER
        );

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(welcome, BorderLayout.CENTER);

        applyRoleRestrictions();

        setVisible(true);
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

        showTable(data, cols, "flights");
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

        showTable(data, cols, "customers");
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

        showTable(data, cols, "bookings");
    }

    /**
     * Renders a table and attaches double-click handlers.
     *
     * @param data table data
     * @param cols column headers
     * @param type table type identifier
     */
    private void showTable(Object[][] data, String[] cols, String type) {
        getContentPane().removeAll();
        currentTable = new JTable(data, cols);

        currentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleDoubleClick(type, currentTable.getSelectedRow());
                }
            }
        });

        getContentPane().add(new JScrollPane(currentTable), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /**
     * Handles double-click actions on table rows.
     *
     * @param type the table type
     * @param row the selected row
     */
    private void handleDoubleClick(String type, int row) {
        try {
            if ("customers".equals(type) && AuthService.isCustomer()) {
                throw new FlightBookingSystemException("Access denied.");
            }

            if ("flights".equals(type)) {
                int id = (int) currentTable.getValueAt(row, 0);
                new ViewPassengersWindow(this, fbs.getFlightByID(id));
            }

            if ("customers".equals(type)) {
                int id = (int) currentTable.getValueAt(row, 0);
                new ViewCustomerBookingsWindow(this, fbs.getCustomerByID(id));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    /**
     * Prompts for a flight ID and displays its passengers.
     */
    private void viewPassengersForFlight() throws Exception {
        int id = Integer.parseInt(JOptionPane.showInputDialog(this, "Flight ID:"));
        new ViewPassengersWindow(this, fbs.getFlightByID(id));
    }

    /**
     * Prompts for a customer ID and displays their bookings (admin only).
     */
    private void viewBookingsForCustomer() throws Exception {
        AuthService.requireAdmin();
        int id = Integer.parseInt(JOptionPane.showInputDialog(this, "Customer ID:"));
        new ViewCustomerBookingsWindow(this, fbs.getCustomerByID(id));
    }

    /**
     * Deletes a flight using soft delete (admin only).
     */
    private void deleteFlight() throws Exception {
        AuthService.requireAdmin();
        int id = Integer.parseInt(JOptionPane.showInputDialog(this, "Flight ID:"));
        new DeleteFlight(id, new FlightDataManager()).execute(fbs);
        displayFlights();
    }

    /**
     * Deletes a customer using soft delete (admin only).
     */
    private void deleteCustomer() throws Exception {
        AuthService.requireAdmin();
        int id = Integer.parseInt(JOptionPane.showInputDialog(this, "Customer ID:"));
        new DeleteCustomer(id, new CustomerDataManager()).execute(fbs);
        displayCustomers();
    }

    /**
     * Cancels an existing booking (admin only).
     */
    private void cancelBooking() throws Exception {
        AuthService.requireAdmin();

        int custId = Integer.parseInt(JOptionPane.showInputDialog(this, "Customer ID:"));
        int flightId = Integer.parseInt(JOptionPane.showInputDialog(this, "Flight ID:"));

        Booking target = null;
        for (Booking b : fbs.getBookings()) {
            if (b.getCustomer().getId() == custId && b.getFlight().getId() == flightId) {
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