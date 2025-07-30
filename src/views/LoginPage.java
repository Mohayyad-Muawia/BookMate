package views;

import javax.swing.*;

import components.MyPassField;
import components.MyTextField;
import components.PrimaryButton;
import components.SecondaryButton;
import components.Theme;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import db.DatabaseConnection;
import models.User;

public class LoginPage extends JFrame {
    private MyTextField usernameField;
    private MyPassField passwordField;

    public LoginPage() {
        setTitle("BookMate | Login");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(300, 320);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

        // make the form components
        usernameField = new MyTextField();
        passwordField = new MyPassField();
        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");

        JButton loginButton = new PrimaryButton("Login");
        JButton registerButton = new SecondaryButton("Register New");
        JPanel buttonPanel = new JPanel();
        JPanel formPanel = new JPanel();

        // Style the components:-
        // FONT
        usernameField.setFont(Theme.MAIN_FONT);
        passwordField.setFont(Theme.MAIN_FONT);
        loginButton.setFont(Theme.MAIN_FONT);
        registerButton.setFont(Theme.MAIN_FONT);

        // COLORS
        usernameField.setBackground(Theme.INPUT_BG);
        usernameField.setForeground(Theme.FOREGROUND);
        passwordField.setBackground(Theme.INPUT_BG);
        passwordField.setForeground(Theme.FOREGROUND);

        // SIZE
        usernameField.setMaximumSize(new Dimension(200, 40));
        passwordField.setMaximumSize(new Dimension(200, 40));

        // Panels

        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Theme.BACKGROUND);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(registerButton);

        // Center everything
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(220, 100));

        formPanel.add(userLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(passLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(buttonPanel);

        // Action Listener
        loginButton.addActionListener(e -> loginUser());
        registerButton.addActionListener(e -> {
            dispose();
            new Register();
        });

        add(formPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void loginUser() {
        User user = null;
        String username = usernameField.getText().trim().toLowerCase();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // create the user
                user = new User(
                        rs.getInt("id"),
                        rs.getString("fullname"),
                        rs.getString("username"),
                        rs.getString("password"));

                dispose();
                // open the next screen
                Dashboard dash = new Dashboard();
                dash.initialize(user);
                showTodayReminders(user);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void showTodayReminders(User user) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());

            String sql = "SELECT title FROM books WHERE user_id = ? AND reminder_date = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, user.getId());
            stmt.setString(2, today);
            ResultSet rs = stmt.executeQuery();

            StringBuilder reminders = new StringBuilder();
            while (rs.next()) {
                reminders.append(rs.getString("title")).append("\n");
            }

            rs.close();
            stmt.close();

            if (reminders.length() > 0) {
                JOptionPane.showMessageDialog(this,
                        "Books with reminders for today:\n\n" + reminders.toString(),
                        "Today's Reminders",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error checking reminders: " + e.getMessage());
        }
    }

}
