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

public class Register extends JFrame {

    private MyTextField fullnameField;
    private MyTextField usernameField;
    private MyPassField passwordField;

    public Register() {
        setTitle("BookMate | Register");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(300, 380);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

        fullnameField = new MyTextField();
        usernameField = new MyTextField();
        passwordField = new MyPassField();

        JLabel fullLabel = new JLabel("Full Name:");
        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");

        JButton registerButton = new PrimaryButton("Register");
        JButton loginButton = new SecondaryButton("Login Instead");
        JPanel buttonPanel = new JPanel();
        JPanel formPanel = new JPanel();

        // Styling
        fullnameField.setFont(Theme.MAIN_FONT);
        usernameField.setFont(Theme.MAIN_FONT);
        passwordField.setFont(Theme.MAIN_FONT);
        registerButton.setFont(Theme.MAIN_FONT);

        fullnameField.setBackground(Theme.INPUT_BG);
        fullnameField.setForeground(Theme.FOREGROUND);
        usernameField.setBackground(Theme.INPUT_BG);
        usernameField.setForeground(Theme.FOREGROUND);
        passwordField.setBackground(Theme.INPUT_BG);
        passwordField.setForeground(Theme.FOREGROUND);

        fullnameField.setMaximumSize(new Dimension(200, 40));
        usernameField.setMaximumSize(new Dimension(200, 40));
        passwordField.setMaximumSize(new Dimension(200, 40));

        // Panels

        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Theme.BACKGROUND);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        buttonPanel.add(registerButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(loginButton);

        fullLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        fullnameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(220, 100));

        // Add components
        formPanel.add(fullLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(fullnameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(userLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(passLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        formPanel.add(buttonPanel);

        // Event handler
        registerButton.addActionListener(e -> registerUser());
        loginButton.addActionListener(e -> {
            dispose();
            new LoginPage();
        });

        add(formPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void registerUser() {
        String fullname = fullnameField.getText().trim();
        String username = usernameField.getText().trim().toLowerCase();
        String password = new String(passwordField.getPassword()).trim();

        if (fullname.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();

            // Check if username already exists
            String checkSql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Username already exists. Choose a different one.");
                rs.close();
                checkStmt.close();
                return;
            }
            rs.close();
            checkStmt.close();

            // Insert new user
            String sql = "INSERT INTO users (fullname, username, password) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, fullname);
            stmt.setString(2, username);
            stmt.setString(3, password);

            int rows = stmt.executeUpdate();
            stmt.close();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "✅ Account created successfully!");
                dispose();
                new LoginPage();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to create account.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + e.getMessage());
        }
    }
}
