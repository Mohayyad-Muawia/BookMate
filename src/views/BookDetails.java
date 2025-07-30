package views;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import components.PrimaryButton;
import components.SecondaryButton;
import components.Theme;
import db.DatabaseConnection;
import models.Book;

public class BookDetails extends JFrame {
    private Book book;
    private BookList bookList;

    private JTextField titleField;
    private JTextField authorField;
    private JTextField categoryField;
    private JTextArea descriptionArea;
    private JTextField statusField;
    private JTextField currentPageField;
    private JTextArea notesArea;
    private JTextField reminderDateField;

    public BookDetails(Book book, BookList bookList) {
        this.book = book;
        this.bookList = bookList;

        setTitle(book.getTitle() + " | BookMate");
        setSize(600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(Theme.BACKGROUND);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Theme.BACKGROUND);
        JLabel titleLabel = new JLabel("Book Details");
        titleLabel.setFont(Theme.MAIN_FONT.deriveFont(Font.BOLD, 28f));
        titleLabel.setForeground(Theme.FOREGROUND);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 15, 15));
        detailsPanel.setBackground(Theme.BACKGROUND);

        titleField = addEditableDetailRow(detailsPanel, "Title:", book.getTitle());
        authorField = addEditableDetailRow(detailsPanel, "Author:", book.getAuthor());
        categoryField = addEditableDetailRow(detailsPanel, "Category:", book.getCategory());
        descriptionArea = addEditableTextAreaRow(detailsPanel, "Description:", book.getDescription());
        statusField = addEditableDetailRow(detailsPanel, "Status:", book.getStatus());

        if (book.getStatus().equals("reading")) {
            currentPageField = addEditableDetailRow(detailsPanel, "Current Page:",
                    String.valueOf(book.getCurrentPage()));
        }

        if (book.getStatus().equals("completed")) {
            notesArea = addEditableTextAreaRow(detailsPanel, "Notes:", book.getNotes());
        }

        reminderDateField = addEditableDetailRow(detailsPanel, "Reminder:", book.getReminderDate());

        mainPanel.add(detailsPanel);
        mainPanel.add(Box.createVerticalStrut(30));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Theme.BACKGROUND);

        JButton saveButton = new PrimaryButton("Save Changes");
        saveButton.addActionListener(e -> saveBook());

        JButton deleteButton = new SecondaryButton("Delete Book");
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this book?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    bookList.removeBook(book.getId());
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting book: " + ex.getMessage());
                }
            }
        });

        buttonPanel.add(deleteButton);
        buttonPanel.add(saveButton);

        mainPanel.add(buttonPanel);
        add(mainPanel);
        setVisible(true);
    }

    private JTextField addEditableDetailRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout(15, 0));
        rowPanel.setBackground(Theme.BACKGROUND);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(Theme.MAIN_FONT.deriveFont(Font.BOLD, 16f));
        labelComp.setForeground(Theme.LABEL_COLOR);
        rowPanel.add(labelComp, BorderLayout.WEST);

        JTextField valueComp = new JTextField(value);
        valueComp.setEditable(true);
        valueComp.setBackground(Theme.INPUT_BG);
        valueComp.setForeground(Theme.INPUT_TEXT);
        valueComp.setBorder(BorderFactory.createLineBorder(Theme.SECONDARY_BUTTON, 1));
        valueComp.setFont(Theme.MAIN_FONT.deriveFont(Font.PLAIN, 16f));

        rowPanel.add(valueComp, BorderLayout.CENTER);
        panel.add(rowPanel);
        return valueComp;
    }

    private JTextArea addEditableTextAreaRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout(15, 0));
        rowPanel.setBackground(Theme.BACKGROUND);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(Theme.MAIN_FONT.deriveFont(Font.BOLD, 16f));
        labelComp.setForeground(Theme.LABEL_COLOR);
        rowPanel.add(labelComp, BorderLayout.WEST);

        JTextArea valueComp = new JTextArea(value);
        valueComp.setEditable(true);
        valueComp.setLineWrap(true);
        valueComp.setWrapStyleWord(true);
        valueComp.setBackground(Theme.INPUT_BG);
        valueComp.setForeground(Theme.INPUT_TEXT);
        valueComp.setBorder(BorderFactory.createLineBorder(Theme.SECONDARY_BUTTON, 1));
        valueComp.setFont(Theme.MAIN_FONT.deriveFont(Font.PLAIN, 16f));

        JScrollPane scrollPane = new JScrollPane(valueComp);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(400, 80));

        rowPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(rowPanel);
        return valueComp;
    }

    private void saveBook() {
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String category = categoryField.getText().trim();
        String description = descriptionArea.getText().trim();
        String status = statusField.getText().trim().toLowerCase();
        String reminder = reminderDateField.getText().trim();

        if (title.isEmpty() || author.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and Author cannot be empty.");
            return;
        }

        Integer currentPage = null;
        if (book.getStatus().equals("reading") && currentPageField != null) {
            try {
                currentPage = Integer.parseInt(currentPageField.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Current page must be a number.");
                return;
            }
        }

        String notes = null;
        if (book.getStatus().equals("completed") && notesArea != null) {
            notes = notesArea.getText().trim();
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "UPDATE books SET title = ?, author = ?, category = ?, description = ?, status = ?, current_page = ?, notes = ?, reminder_date = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.setString(3, category);
            stmt.setString(4, description);
            stmt.setString(5, status);
            if (currentPage != null) {
                stmt.setInt(6, currentPage);
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
            stmt.setString(7, notes != null && !notes.isEmpty() ? notes : null);
            stmt.setString(8, reminder != null && !reminder.isEmpty() ? reminder : null);
            stmt.setInt(9, book.getId());

            int rows = stmt.executeUpdate();
            stmt.close();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "✅ Book updated successfully!");
                if (bookList != null) {
                    bookList.refresh(); // refresh BookList
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "⚠️ Failed to update book.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + e.getMessage());
        }
    }
}
