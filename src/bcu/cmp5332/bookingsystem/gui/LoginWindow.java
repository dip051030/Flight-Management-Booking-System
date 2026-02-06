package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.commands.Login;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {

    private final FlightBookingSystem fbs;

    private JTextField usernameText = new JTextField();
    private JPasswordField passwordText = new JPasswordField();

    private JButton loginButton = new JButton("Login");
    private JButton registerButton = new JButton("Register");
    private JButton exitButton = new JButton("Exit");

    public LoginWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;
        initialize();
    }

    private void initialize() {
        setTitle("Login - Flight Booking System");
        setSize(360, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(new JLabel("Username:"));
        panel.add(usernameText);

        panel.add(new JLabel("Password:"));
        panel.add(passwordText);

        panel.add(loginButton);
        panel.add(registerButton);

        panel.add(exitButton);

        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> new RegisterWindow(fbs));
        exitButton.addActionListener(e -> System.exit(0));

        add(panel);
        setVisible(true);
    }

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
}
