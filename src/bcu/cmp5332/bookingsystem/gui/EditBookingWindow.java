package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.data.BookingDataManager;
import bcu.cmp5332.bookingsystem.model.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class EditBookingWindow extends JFrame {

    private JComboBox<String> comboCustomerBooking;
    private JComboBox<String> comboFlight;
    private JButton btnSave;

    private FlightBookingSystem fbs;
    private MainWindow mainWindow;
    private BookingDataManager bookingDataManager;

    public EditBookingWindow(MainWindow mainWindow, FlightBookingSystem fbs, BookingDataManager bookingDataManager) {
        this.mainWindow = mainWindow;
        this.fbs = fbs;
        this.bookingDataManager = bookingDataManager;

        setTitle("Edit Booking");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 10, 10));

        // Select existing booking
        add(new JLabel("Select Booking (Customer - Flight):"));
        comboCustomerBooking = new JComboBox<>();
        for (Booking b : fbs.getBookings()) {
            comboCustomerBooking.addItem(b.getCustomer().getId() + ":" + b.getCustomer().getName()
                    + " -> " + b.getFlight().getFlightNumber());
        }
        add(comboCustomerBooking);

        // Select new flight
        add(new JLabel("Select New Flight:"));
        comboFlight = new JComboBox<>();
        for (Flight f : fbs.getFlights()) {
            comboFlight.addItem(f.getId() + ": " + f.getFlightNumber() + " (" + f.getOrigin() + " -> " + f.getDestination() + ")");
        }
        add(comboFlight);

        btnSave = new JButton("Save Changes");
        add(new JLabel());
        add(btnSave);

        btnSave.addActionListener(e -> saveEdit());

        setVisible(true);
    }

    private void saveEdit() {
        try {
            if (comboCustomerBooking.getSelectedIndex() == -1 || comboFlight.getSelectedIndex() == -1) {
                JOptionPane.showMessageDialog(this, "Select both booking and new flight.");
                return;
            }

            // Selected booking
            String bookingStr = (String) comboCustomerBooking.getSelectedItem();
            int custId = Integer.parseInt(bookingStr.split(":")[0].trim());
            Booking selectedBooking = null;
            for (Booking b : fbs.getBookings()) {
                if (b.getCustomer().getId() == custId) {
                    selectedBooking = b;
                    break;
                }
            }

            if (selectedBooking == null) {
                JOptionPane.showMessageDialog(this, "Booking not found.");
                return;
            }

            // Remove old flight passenger
            selectedBooking.getFlight().removePassenger(selectedBooking.getCustomer());

            // New flight
            String flightStr = (String) comboFlight.getSelectedItem();
            int flightId = Integer.parseInt(flightStr.split(":")[0].trim());
            Flight newFlight = fbs.getFlightByID(flightId);

            // Update booking
            selectedBooking.setFlight(newFlight);
            newFlight.addPassenger(selectedBooking.getCustomer());

            // Save changes
            bookingDataManager.storeData(fbs);

            JOptionPane.showMessageDialog(this, "Booking updated successfully!");
            mainWindow.displayBookings();
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
