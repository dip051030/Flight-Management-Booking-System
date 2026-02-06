package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.AddCustomer;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.data.CustomerDataManager;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import javax.swing.*;
import java.awt.*;

public class AddCustomerWindow extends JFrame {

    private MainWindow mw;

    private JTextField nameText = new JTextField();
    private JTextField phoneText = new JTextField();
    private JTextField emailText = new JTextField();
    private JTextField usernameText = new JTextField();
    private JPasswordField passwordText = new JPasswordField();

    private JButton addButton = new JButton("Add");
    private JButton cancelButton = new JButton("Cancel");

    public AddCustomerWindow(MainWindow mw, FlightBookingSystem fbs) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
        }

        setTitle("Add a New Customer");
        setSize(400, 280);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(6, 2));

        topPanel.add(new JLabel("Name : "));
        topPanel.add(nameText);

        topPanel.add(new JLabel("Phone : "));
        topPanel.add(phoneText);

        topPanel.add(new JLabel("Email : "));
        topPanel.add(emailText);

        topPanel.add(new JLabel("Username : "));
        topPanel.add(usernameText);

        topPanel.add(new JLabel("Password : "));
        topPanel.add(passwordText);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 3));
        bottomPanel.add(new JLabel(" "));
        bottomPanel.add(addButton);
        bottomPanel.add(cancelButton);

        addButton.addActionListener(actionEvent -> addCustomer());
        cancelButton.addActionListener(actionEvent -> setVisible(false));

        this.getContentPane().add(topPanel, BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(mw);
        setVisible(true);
    }

    private void addCustomer() {

        try {
            String name = nameText.getText();
            String phone = phoneText.getText();
            String email = emailText.getText();
            String username = usernameText.getText();
            String password = new String(passwordText.getPassword());

            Command addCustomer = new AddCustomer(
                    name,
                    phone,
                    email,
                    username,
                    password,
                    new CustomerDataManager()
            );

            addCustomer.execute(mw.getFlightBookingSystem());
            mw.displayCustomers();
            setVisible(false);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
