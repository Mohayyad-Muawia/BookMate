package views;

import javax.swing.*;

import components.PrimaryButton;
import components.Theme;
import models.User;

import java.awt.*;

public class Dashboard extends JFrame {
    private User user;

    public Dashboard() {
        setTitle("BookMate | Dashboard");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setBackground(Theme.BACKGROUND);
    }

    public void initialize(User user) {
        this.user = user;

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getFullname(), SwingConstants.CENTER);
        welcomeLabel.setFont(Theme.MAIN_FONT);
        welcomeLabel.setForeground(Theme.FOREGROUND);

        // Buttons
        JButton addBookBtn = new PrimaryButton("➕ Add New Book");
        JButton viewBooksBtn = new PrimaryButton("📚 View My Books");
        JButton logoutBtn = new PrimaryButton("🚪 Logout");

        addBookBtn.setFont(Theme.MAIN_FONT);
        viewBooksBtn.setFont(Theme.MAIN_FONT);
        logoutBtn.setFont(Theme.MAIN_FONT);

        // Actions
        addBookBtn.addActionListener(e -> {
            dispose();
            new AddBook(user);
        });

        viewBooksBtn.addActionListener(e -> {
            dispose();
            new BookList().initialize(user);
        });

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginPage();
        });

        // Layout
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Theme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        panel.add(welcomeLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(addBookBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(viewBooksBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(logoutBtn);

        add(panel);
        setVisible(true);
    }
}
