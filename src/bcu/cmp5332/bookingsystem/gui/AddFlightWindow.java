package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.AddFlight;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.data.FlightDataManager;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AddFlightWindow extends JFrame {

    private MainWindow mw;
    private JTextField flightNoText = new JTextField();
    private JTextField originText = new JTextField();
    private JTextField destText = new JTextField();
    private JTextField dateText = new JTextField();
    private JTextField capacityText = new JTextField("100");
    private JTextField priceText = new JTextField("0.00");

    private JButton addButton = new JButton("Add");
    private JButton cancelButton = new JButton("Cancel");

    public AddFlightWindow(MainWindow mw, FlightBookingSystem fbs) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {

        }

        setTitle("Add a New Flight");

        setSize(350, 300);
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(7, 2));
        topPanel.add(new JLabel("Flight No : "));
        topPanel.add(flightNoText);
        topPanel.add(new JLabel("Origin : "));
        topPanel.add(originText);
        topPanel.add(new JLabel("Destination : "));
        topPanel.add(destText);
        topPanel.add(new JLabel("Departure Date (YYYY-MM-DD) : "));
        topPanel.add(dateText);
        topPanel.add(new JLabel("Capacity : "));
        topPanel.add(capacityText);
        topPanel.add(new JLabel("Price : "));
        topPanel.add(priceText);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 3));
        bottomPanel.add(new JLabel("     "));
        bottomPanel.add(addButton);
        bottomPanel.add(cancelButton);

        addButton.addActionListener(actionEvent -> addFlight());
        cancelButton.addActionListener(actionEvent -> setVisible(false));

        this.getContentPane().add(topPanel, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(mw);

        setVisible(true);

    }

    private void addFlight() {

        try {
            String flightNumber = flightNoText.getText();
            String origin = originText.getText();
            String destination = destText.getText();
            LocalDate departureDate = LocalDate.parse(dateText.getText());
            int capacity = Integer.parseInt(capacityText.getText());
            double price = Double.parseDouble(priceText.getText());

            // Create and execute the command
            Command addFlight = new AddFlight(flightNumber, origin, destination, departureDate, capacity, price, new FlightDataManager());
            addFlight.execute(mw.getFlightBookingSystem());

            // Update the display
            mw.displayFlights();

            // Hide (close) the AddFlightWindow
            this.setVisible(false);
        } catch (DateTimeParseException dtpe) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Invalid number format for capacity or price", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
