package views;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import components.MyTextField;
import components.PrimaryButton;
import components.Theme;
import db.DatabaseConnection;
import models.Book;
import models.User;

public class BookList extends JFrame {
    private String placeholder = "Search books by title or author...";
    private User currentUser;
    private JLabel totalLabel, finishedLabel, readingLabel, onholdLabel;
    private MyTextField searchField;
    private JPanel cardsPanel;
    private List<Book> allBooks = new ArrayList<>();

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

        // --- Search Bar ---
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        searchPanel.setBackground(Theme.BACKGROUND);

        searchField = new MyTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setPreferredSize(new Dimension(300, 35));
        searchField.setToolTipText("Search by title or author...");
        searchField.setBackground(Theme.INPUT_BG);
        searchField.setForeground(Theme.INPUT_TEXT);

        // Placeholder text
        searchField.setText(placeholder);
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals(placeholder)) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK); // أو Theme.INPUT_TEXT لو عندك ثيم
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText(placeholder);
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        // Fillter the books
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filterBooks();
            }

            public void removeUpdate(DocumentEvent e) {
                filterBooks();
            }

            public void changedUpdate(DocumentEvent e) {
                filterBooks();
            }
        });

        searchPanel.add(searchField, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.NORTH);

        // --- Cards Panel ---
        cardsPanel = new JPanel();
        cardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        cardsPanel.setBackground(Theme.BACKGROUND);

        JScrollPane scroll = new JScrollPane(cardsPanel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT * FROM books WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentUser.getId());
            ResultSet rs = stmt.executeQuery();

            allBooks.clear();
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

                allBooks.add(book);
            }

            rs.close();
            stmt.close();

            filterBooks();
        } catch (Exception e) {
            e.printStackTrace();
        }

        add(scroll, BorderLayout.CENTER);

        // --- Sidebar ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        sidebar.setBackground(Theme.INPUT_BG);

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

        statsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        statsPanel.add(totalLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        statsPanel.add(finishedLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        statsPanel.add(readingLabel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        statsPanel.add(onholdLabel);

        JButton backButton = new PrimaryButton("⬅   Back to Dashboard");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setFont(new Font(Theme.MAIN_FONT.getName(), Font.PLAIN, 15));
        backButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        backButton.addActionListener(e -> {
            dispose();
            new Dashboard().initialize(user);
        });

        sidebar.add(statsPanel);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(backButton);

        add(sidebar, BorderLayout.WEST);

        // Load Stats
        loadBooks(user.getId());

        setVisible(true);
    }

    private void filterBooks() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty() || query.equals(placeholder)) {
            query = "";
        }

        cardsPanel.removeAll();

        for (Book book : allBooks) {
            if (book.getTitle().toLowerCase().contains(query) ||
                    book.getAuthor().toLowerCase().contains(query)) {
                cardsPanel.add(new components.BookCard(book));
            }
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private void loadBooks(int userId) {
        int total = 0;
        int finished = 0;
        int inProgress = 0;
        int notStarted = 0;

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "SELECT status FROM books WHERE user_id = ?";
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
            }

            rs.close();
            stmt.close();

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
                refresh();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to remove book.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error removing book: " + e.getMessage());
        }
    }
}
