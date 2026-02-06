package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Window to display the list of passengers for a selected flight.
 * Requirement: 60-69% band - Display popup showing passengers for a particular flight.
 */
public class ViewPassengersWindow extends JFrame {

    private Flight flight;
    private JTable passengersTable;

    /**
     * Creates a window showing all passengers for the given flight.
     * 
     * @param parent the parent window
     * @param flight the flight to show passengers for
     */
    public ViewPassengersWindow(JFrame parent, Flight flight) {
        this.flight = flight;
        initialize(parent);
    }

    private void initialize(JFrame parent) {
        setTitle("Passengers for Flight " + flight.getFlightNumber());
        setSize(600, 400);
        setLocationRelativeTo(parent);

        // Create table with passenger data
        String[] columnNames = {"Customer ID", "Name", "Phone", "Email"};
        List<Customer> passengers = flight.getPassengers();
        
        Object[][] data = new Object[passengers.size()][4];
        int row = 0;
        for (Customer customer : passengers) {
            data[row][0] = customer.getId();
            data[row][1] = customer.getName();
            data[row][2] = customer.getPhone();
            data[row][3] = customer.getEmail();
            row++;
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        passengersTable = new JTable(model);
        passengersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(passengersTable);

        // Info panel at top
        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 10, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        infoPanel.add(new JLabel("Flight Number:"));
        infoPanel.add(new JLabel(flight.getFlightNumber()));
        
        infoPanel.add(new JLabel("Route:"));
        infoPanel.add(new JLabel(flight.getOrigin() + " → " + flight.getDestination()));
        
        infoPanel.add(new JLabel("Departure Date:"));
        infoPanel.add(new JLabel(flight.getDepartureDate().toString()));
        
        infoPanel.add(new JLabel("Capacity:"));
        infoPanel.add(new JLabel(passengers.size() + " / " + flight.getCapacity()));
        
        infoPanel.add(new JLabel("Available Seats:"));
        infoPanel.add(new JLabel(String.valueOf(flight.getAvailableSeats())));

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);

        // Layout
        setLayout(new BorderLayout());
        add(infoPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}
