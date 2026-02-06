package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Window to display all bookings for a selected customer.
 * Requirement: 60-69% band - Display popup showing booking details for a customer.
 */
public class ViewCustomerBookingsWindow extends JFrame {

    private Customer customer;
    private JTable bookingsTable;

    /**
     * Creates a window showing all bookings for the given customer.
     * 
     * @param parent the parent window
     * @param customer the customer to show bookings for
     */
    public ViewCustomerBookingsWindow(JFrame parent, Customer customer) {
        this.customer = customer;
        initialize(parent);
    }

    private void initialize(JFrame parent) {
        setTitle("Bookings for " + customer.getName());
        setSize(700, 400);
        setLocationRelativeTo(parent);

        // Create table with booking data
        String[] columnNames = {"Booking ID", "Flight Number", "Route", "Date", "Price", "Status"};
        List<Booking> bookings = customer.getBookings();
        
        Object[][] data = new Object[bookings.size()][6];
        int row = 0;
        for (Booking booking : bookings) {
            Flight flight = booking.getFlight();
            data[row][0] = booking.getId();
            data[row][1] = flight.getFlightNumber();
            data[row][2] = flight.getOrigin() + " → " + flight.getDestination();
            data[row][3] = booking.getBookingDate().toString();
            data[row][4] = String.format("$%.2f", flight.getPrice());
            data[row][5] = flight.isDeleted() ? "CANCELLED" : "ACTIVE";
            row++;
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        bookingsTable = new JTable(model);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(bookingsTable);

        // Info panel at top
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        infoPanel.add(new JLabel("Customer ID:"));
        infoPanel.add(new JLabel(String.valueOf(customer.getId())));
        
        infoPanel.add(new JLabel("Name:"));
        infoPanel.add(new JLabel(customer.getName()));
        
        infoPanel.add(new JLabel("Phone:"));
        infoPanel.add(new JLabel(customer.getPhone()));
        
        infoPanel.add(new JLabel("Email:"));
        infoPanel.add(new JLabel(customer.getEmail()));

        // Statistics panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        int totalBookings = bookings.size();
        int activeBookings = customer.getActiveBookings().size();
        int cancelledBookings = totalBookings - activeBookings;
        
        statsPanel.add(new JLabel("Total Bookings: " + totalBookings + "   "));
        statsPanel.add(new JLabel("Active: " + activeBookings + "   "));
        statsPanel.add(new JLabel("Cancelled: " + cancelledBookings));

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);

        // Layout
        setLayout(new BorderLayout());
        add(infoPanel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Fix layout issue - properly arrange components
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(statsPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        getContentPane().removeAll();
        add(infoPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}
