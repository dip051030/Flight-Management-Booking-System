package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.commands.Login;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import javax.swing.*;
import java.awt.*;

/**
 * Login window for the Flight Booking System.
 *
 * <p>This window allows both administrators and customers to authenticate
 * using their credentials. Successful authentication redirects the user
 * to the main application window.</p>
 *
 * <p>Authentication logic is delegated to the {@link Login} command.</p>
 */
public class LoginWindow extends JFrame {

    private final FlightBookingSystem fbs;

    private JTextField usernameText = new JTextField();
    private JPasswordField passwordText = new JPasswordField();

    private JButton loginButton = new JButton("Login");
    private JButton registerButton = new JButton("Register");
    private JButton exitButton = new JButton("Exit");

    /**
     * Constructs the login window.
     *
     * @param fbs the flight booking system instance
     */
    public LoginWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;
        initialize();
    }

    /**
     * Initializes the login window layout and components.
     */
    private void initialize() {
        setTitle("Flight Booking System");
        setSize(440, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        JLabel title = new JLabel("Flight Booking System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel subtitle = new JLabel("Please log in to continue");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);

        return header;
    }

    /**
     * Builds the login form panel.
     *
     * @return the form panel
     */
    private JPanel buildForm() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));

        usernameText.setColumns(20);
        passwordText.setColumns(20);

        form.add(createFieldRow("Username:", usernameText));
        form.add(Box.createVerticalStrut(12));
        form.add(createFieldRow("Password:", passwordText));

        return form;
    }

    /**
     * Builds the footer panel with action buttons.
     *
     * @return the footer panel
     */
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(BorderFactory.createEmptyBorder(12, 24, 18, 24));

        registerButton.setPreferredSize(new Dimension(130, 32));
        loginButton.setPreferredSize(new Dimension(110, 32));
        exitButton.setPreferredSize(new Dimension(90, 32));

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftButtons.add(registerButton);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButtons.add(loginButton);
        rightButtons.add(exitButton);

        footer.add(leftButtons, BorderLayout.WEST);
        footer.add(rightButtons, BorderLayout.EAST);

        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> new RegisterWindow(fbs));
        exitButton.addActionListener(e -> System.exit(0));

        return footer;
    }

    /**
     * Executes the login command using the entered credentials.
     */
    private void login() {
        try {
            String username = usernameText.getText().trim();
            String password = new String(passwordText.getPassword());

            Command loginCommand = new Login(username, password);
            loginCommand.execute(fbs);

            dispose();
            new MainWindow(fbs);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Creates a labeled row for the form.
     *
     * @param labelText the text for the label
     * @param field     the input field
     * @return the panel containing the label and field
     */
    private JPanel createFieldRow(String labelText, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(90, 24));

        row.add(label, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);

        return row;
    }
}
