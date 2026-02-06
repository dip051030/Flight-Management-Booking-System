package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.auth.AuthService;
import bcu.cmp5332.bookingsystem.auth.PasswordUtil;
import bcu.cmp5332.bookingsystem.data.CustomerDataManager;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import javax.swing.*;
import java.awt.*;

/**
 * GUI window that allows a new customer to register.
 *
 * Email is used as the login username.
 */
public class RegisterWindow extends JFrame {

    private final FlightBookingSystem fbs;

    private JTextField nameText = new JTextField();
    private JTextField phoneText = new JTextField();
    private JTextField emailText = new JTextField();
    private JPasswordField passwordText = new JPasswordField();

    private JButton registerButton = new JButton("Register");
    private JButton cancelButton = new JButton("Cancel");

    public RegisterWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;
        initialize();
    }

    private void initialize() {
        setTitle("Customer Registration");
        setSize(400, 260);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(new JLabel("Name:"));
        panel.add(nameText);

        panel.add(new JLabel("Phone:"));
        panel.add(phoneText);

        panel.add(new JLabel("Email:"));
        panel.add(emailText);

        panel.add(new JLabel("Password:"));
        panel.add(passwordText);

        panel.add(registerButton);
        panel.add(cancelButton);

        registerButton.addActionListener(e -> register());
        cancelButton.addActionListener(e -> dispose());

        add(panel);
        setVisible(true);
    }

    private void register() {
        try {
            String name = nameText.getText().trim();
            String phone = phoneText.getText().trim();
            String email = emailText.getText().trim();
            String password = new String(passwordText.getPassword());

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                throw new FlightBookingSystemException("All required fields must be filled.");
            }

            for (Customer c : fbs.getCustomers().values()) {
                if (email.equalsIgnoreCase(c.getEmail())) {
                    throw new FlightBookingSystemException("Email already registered.");
                }
            }

            int maxId = 0;
            for (Customer c : fbs.getCustomers().values()) {
                maxId = Math.max(maxId, c.getId());
            }

            Customer customer = new Customer(
                    ++maxId,
                    name,
                    phone,
                    email,
                    PasswordUtil.hash(password)
            );

            fbs.addCustomer(customer);
            new CustomerDataManager().storeData(fbs);

            AuthService.startCustomerSession(customer);

            dispose();
            new MainWindow(fbs);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Registration Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
