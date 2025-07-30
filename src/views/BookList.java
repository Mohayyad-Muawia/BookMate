package views;

import javax.swing.*;

import components.PrimaryButton;
import components.Theme;

import java.awt.*;
import java.sql.*;
import db.DatabaseConnection;
import models.Book;
import models.User;

public class BookList extends JFrame {
    private User currentUser;
    private JLabel totalLabel, finishedLabel, readingLabel, onholdLabel;

    public BookList() {
        setTitle("BookMate | My Books");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setBackground(Theme.BACKGROUND);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

    }

    public void initialize(User user) {
        this.currentUser = user;

        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        cardsPanel.setBackground(Theme.BACKGROUND);

        JScrollPane scroll = new JScrollPane(cardsPanel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        cardsPanel.setPreferredSize(null);
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM books WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentUser.getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setCategory(rs.getString("category"));
                book.setStatus(rs.getString("status"));
                book.setDescription(rs.getString("description"));
                book.setCurrentPage(rs.getInt("current_page"));
                book.setNotes(rs.getString("notes"));
                book.setReminderDate(rs.getString("reminder_date"));
                book.setUserId(rs.getInt("user_id"));

                cardsPanel.add(new components.BookCard(book));
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        add(scroll, BorderLayout.CENTER);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        sidebar.setBackground(Theme.INPUT_BG);

        // Stats label
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setOpaque(false);
        statsPanel.setBackground(Theme.GREEN);
        statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        Font labelFont = new Font("Segoe UI", Font.BOLD, 18);

        totalLabel = new JLabel();
        totalLabel.setForeground(Theme.INPUT_TEXT);
        totalLabel.setFont(labelFont);
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        finishedLabel = new JLabel();
        finishedLabel.setForeground(Theme.GREEN);
        finishedLabel.setFont(labelFont);
        finishedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        readingLabel = new JLabel();
        readingLabel.setForeground(Theme.ACCENT);
        readingLabel.setFont(labelFont);
        readingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        onholdLabel = new JLabel();
        onholdLabel.setForeground(Theme.LABEL_COLOR);
        onholdLabel.setFont(labelFont);
        onholdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add Labels to Panel
        statsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        statsPanel.add(totalLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        statsPanel.add(finishedLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        statsPanel.add(readingLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        statsPanel.add(onholdLabel);

        // Back button
        JButton backButton = new PrimaryButton("⬅   Back to Dashboard");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setFont(new Font(Theme.MAIN_FONT.getName(), Font.PLAIN, 15));
        backButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        backButton.addActionListener(e -> {
            dispose();
            new Dashboard().initialize(user);
        });

        // Add to sidebar
        sidebar.add(statsPanel);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(backButton);

        // Load table and stats
        loadBooks(user.getId());

        // Add panels to frame
        add(sidebar, BorderLayout.WEST);

        setVisible(true);
    }

    private void loadBooks(int userId) {
        int total = 0;
        int finished = 0;
        int inProgress = 0;
        int notStarted = 0;

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT id, title, author, status, current_page, reminder_date FROM books WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                total++;
                String status = rs.getString("status");
                if ("completed".equals(status))
                    finished++;
                else if ("reading".equals(status))
                    inProgress++;
                else
                    notStarted++;

                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        status,
                        rs.getInt("current_page"),
                        rs.getString("reminder_date")
                };
            }

            rs.close();
            stmt.close();

            // Show stats
            totalLabel.setText("Total Books: " + total);
            finishedLabel.setText("Completed: " + finished);
            readingLabel.setText("Reading: " + inProgress);
            onholdLabel.setText("On Hold: " + notStarted);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage());
        }
    }

    public void refresh() {
        getContentPane().removeAll();
        revalidate();
        repaint();
        initialize(currentUser);
    }

    public void removeBook(int bookId) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "DELETE FROM books WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bookId);
            int rows = stmt.executeUpdate();
            stmt.close();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "✅ Book removed successfully!");
                refresh(); // refresh BookList
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to remove book.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error removing book: " + e.getMessage());
        }
    }
}