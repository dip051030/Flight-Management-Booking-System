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
 * <p>Email is used as the login username.</p>
 */
public class RegisterWindow extends JFrame {

    private final FlightBookingSystem fbs;

    private JTextField nameText = new JTextField();
    private JTextField phoneText = new JTextField();
    private JTextField emailText = new JTextField();
    private JPasswordField passwordText = new JPasswordField();

    private JButton registerButton = new JButton("Create Account");
    private JButton cancelButton = new JButton("Cancel");

    /**
     * Constructs the registration window.
     *
     * @param fbs the flight booking system instance
     */
    public RegisterWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;
        initialize();
    }

    /**
     * Initializes the registration window layout and components.
     */
    private void initialize() {
        setTitle("Customer Registration");
        setSize(450, 320);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        setVisible(true);
    }

    /**
     * Builds the header panel.
     *
     * @return the header panel
     */
    private JPanel buildHeader() {
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JLabel title = new JLabel("Create Customer Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JLabel subtitle = new JLabel("Register to book flights and manage your bookings");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        header.add(title);
        header.add(subtitle);

        return header;
    }

    /**
     * Builds the registration form panel.
     *
     * @return the form panel
     */
    private JPanel buildForm() {
        JPanel form = new JPanel(new GridLayout(4, 2, 12, 12));
        form.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        form.add(new JLabel("Full Name:"));
        form.add(nameText);

        form.add(new JLabel("Phone:"));
        form.add(phoneText);

        form.add(new JLabel("Email:"));
        form.add(emailText);

        form.add(new JLabel("Password:"));
        form.add(passwordText);

        return form;
    }

    /**
     * Builds the footer panel containing action buttons.
     *
     * @return the footer panel
     */
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.add(cancelButton);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.add(registerButton);

        footer.add(left, BorderLayout.WEST);
        footer.add(right, BorderLayout.EAST);

        registerButton.addActionListener(e -> register());
        cancelButton.addActionListener(e -> dispose());

        return footer;
    }

    /**
     * Registers a new customer and starts a customer session.
     */
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
