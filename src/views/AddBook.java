package views;

import javax.swing.*;
import components.MyTextField;
import components.PrimaryButton;
import components.SecondaryButton;
import components.Theme;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import db.DatabaseConnection;
import models.Book;
import models.User;

public class AddBook extends JFrame {
    private MyTextField titleField, authorField, categoryField, currentPageField, reminderDateField;
    private JTextArea descriptionArea, notesArea;
    private JComboBox<String> statusCombo;
    private JPanel notesPanel, pagePanel;

    private final String[] statuses = { "on hold", "reading", "completed" };
    private User currentUser;

    public AddBook(User user) {
        this.currentUser = user;

        setTitle("BookMate | Add New Book");
        setSize(600, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(Theme.BACKGROUND);

        addForm();
        setVisible(true);
    }

    private void addForm() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // مهم: تحديد الحجم ووسطته أفقياً
        formPanel.setPreferredSize(new Dimension(500, 600));
        formPanel.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // إضافة الحقول
        formPanel.add(createField("Title:", titleField = new MyTextField()));
        formPanel.add(createField("Author:", authorField = new MyTextField()));
        formPanel.add(createField("Category:", categoryField = new MyTextField()));
        formPanel.add(createTextArea("Description:", descriptionArea = new JTextArea(3, 30)));

        notesPanel = createTextArea("Notes / Summary:", notesArea = new JTextArea(3, 30));
        formPanel.add(notesPanel);

        pagePanel = createField("Current Page:", currentPageField = new MyTextField());
        formPanel.add(pagePanel);

        formPanel.add(createField("Reminder Date (YYYY-MM-DD):", reminderDateField = new MyTextField()));

        // Status Combo
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JLabel statusLabel = new JLabel("📌 Status:");
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusCombo = new JComboBox<>(statuses);
        statusCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        statusCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusCombo.addActionListener(e -> updateVisibility());

        statusPanel.add(statusLabel);
        statusPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        statusPanel.add(statusCombo);
        statusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(statusPanel);

        // Buttons
        JPanel btnsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        btnsPanel.setOpaque(false);
        JButton cancelButton = new SecondaryButton("Cancel");
        cancelButton.addActionListener(e -> {
            dispose();
            new Dashboard().initialize(currentUser);
        });
        JButton saveButton = new PrimaryButton("Save Book");
        saveButton.addActionListener(e -> saveBook());

        btnsPanel.add(cancelButton);
        btnsPanel.add(saveButton);

        btnsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(btnsPanel);

        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.X_AXIS));
        centerWrapper.add(Box.createHorizontalGlue());
        centerWrapper.add(formPanel);
        centerWrapper.add(Box.createHorizontalGlue());
        centerWrapper.setOpaque(false);

        add(centerWrapper, BorderLayout.CENTER);
        updateVisibility();
    }

    private Font formFont = new Font("Segoe UI", Font.PLAIN, 16);

    private JPanel createField(String labelText, MyTextField field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(formFont);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setFont(formFont);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(field);
        return panel;
    }

    private JPanel createTextArea(String labelText, JTextArea area) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(formFont);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        area.setFont(formFont);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(scroll);
        return panel;
    }

    private void updateVisibility() {
        String status = (String) statusCombo.getSelectedItem();
        notesPanel.setVisible("completed".equals(status));
        pagePanel.setVisible("reading".equals(status));
        revalidate();
        repaint();
    }

    private void saveBook() {
        Book book = new Book();
        book.setTitle(titleField.getText().trim());
        book.setAuthor(authorField.getText().trim());
        book.setCategory(categoryField.getText().trim());
        book.setDescription(descriptionArea.getText().trim());
        book.setStatus((String) statusCombo.getSelectedItem());
        book.setReminderDate(reminderDateField.getText().trim());
        book.setNotes(notesArea.getText().trim());
        book.setUserId(currentUser.getId());

        if ("reading".equals(book.getStatus())) {
            try {
                book.setCurrentPage(Integer.parseInt(currentPageField.getText().trim()));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Current page must be a number.");
                return;
            }
        } else {
            book.setCurrentPage(null);
        }

        if (book.getTitle().isEmpty() || book.getAuthor().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in at least title and author.");
            return;
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO books (title, author, description, category, status, current_page, notes, reminder_date, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getDescription());
            stmt.setString(4, book.getCategory());
            stmt.setString(5, book.getStatus());
            if (book.getCurrentPage() != null) {
                stmt.setInt(6, book.getCurrentPage());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
            stmt.setString(7, book.getNotes().isEmpty() ? null : book.getNotes());
            stmt.setString(8, book.getReminderDate().isEmpty() ? null : book.getReminderDate());
            stmt.setInt(9, book.getUserId());

            stmt.executeUpdate();
            stmt.close();

            JOptionPane.showMessageDialog(this, "✅ Book added successfully!");
            dispose();
            new Dashboard().initialize(currentUser);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Error saving book: " + e.getMessage());
        }
    }
}
