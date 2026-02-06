package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.data.BookingDataManager;
import bcu.cmp5332.bookingsystem.model.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

/**
 * Window for adding a new booking with capacity checking.
 * Updated to enforce flight capacity limits (70-79% requirement).
 */
public class AddBookingWindow extends JFrame {

    private JComboBox<String> comboCustomer;
    private JComboBox<String> comboFlight;
    private JButton btnAdd;

    private FlightBookingSystem fbs;
    private MainWindow mainWindow;
    private BookingDataManager bookingDataManager;

    public AddBookingWindow(MainWindow mainWindow, FlightBookingSystem fbs, BookingDataManager bookingDataManager) {
        this.mainWindow = mainWindow;
        this.fbs = fbs;
        this.bookingDataManager = bookingDataManager;

        setTitle("Add Booking");
        setSize(500, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 10, 10));

        // Customer selection
        add(new JLabel("Select Customer:"));
        comboCustomer = new JComboBox<>();
        for (Customer c : fbs.getCustomers().values()) {
            comboCustomer.addItem(c.getId() + ": " + c.getName());
        }
        add(comboCustomer);

        // Flight selection with capacity info
        add(new JLabel("Select Flight:"));
        comboFlight = new JComboBox<>();
        for (Flight f : fbs.getFlights()) {
            String availability = f.getAvailableSeats() > 0 ? 
                " [" + f.getAvailableSeats() + " seats available]" : " [FULL]";
            comboFlight.addItem(f.getId() + ": " + f.getFlightNumber() + 
                " (" + f.getOrigin() + " → " + f.getDestination() + ")" + availability);
        }
        add(comboFlight);

        // Add button
        btnAdd = new JButton("Book Flight");
        add(new JLabel());
        add(btnAdd);

        btnAdd.addActionListener(e -> addBooking());

        setVisible(true);
    }

    private void addBooking() {
        try {
            if (comboCustomer.getSelectedIndex() == -1 || comboFlight.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(this, "Please select both a customer and a flight.");
                return;
            }

            // Get selected customer
            String custStr = (String) comboCustomer.getSelectedItem();
            int custId = Integer.parseInt(custStr.split(":")[0].trim());
            Customer c = fbs.getCustomerByID(custId);

            // Get selected flight
            String flightStr = (String) comboFlight.getSelectedItem();
            int flightId = Integer.parseInt(flightStr.split(":")[0].trim());
            Flight f = fbs.getFlightByID(flightId);

            // CHECK CAPACITY using safe add
            boolean canAdd = f.addPassengerSafe(c);
            
            if (!canAdd) {
                JOptionPane.showMessageDialog(this, 
                    "Cannot make booking: Flight " + f.getFlightNumber() + 
                    " is at full capacity (" + f.getCapacity() + " seats).",
                    "Flight Full",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Create booking
            Booking b = new Booking(c, f, LocalDate.now());
            c.getBookings().add(b);
            // Note: passenger already added via addPassengerSafe above
            fbs.addBooking(b);

            // Save immediately
            bookingDataManager.storeData(fbs);

            JOptionPane.showMessageDialog(this, 
                "Booking added successfully!\nSeats remaining: " + f.getAvailableSeats());

            // Refresh GUI
            mainWindow.displayBookings();
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
