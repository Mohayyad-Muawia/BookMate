package components;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import models.Book;
import views.BookDetails;
import views.BookList;

public class BookCard extends JPanel {
    public BookCard(Book book) {

        Color statusColor = getStatusColor(book.getStatus());

        // Card Layout
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 250));
        setBackground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Border
        Border bottomBorder = BorderFactory.createMatteBorder(0, 0, 4, 0, statusColor);
        setBorder(bottomBorder);

        // Making the Labels
        JLabel title = new JLabel(capitalizeText(book.getTitle()));
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(statusColor);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel author = new JLabel("by: " + capitalizeText(book.getAuthor()));
        author.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        author.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel status = new JLabel(book.getStatus());
        status.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Text Panel to hold the labels
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Theme.INPUT_BG);

        textPanel.add(Box.createVerticalGlue());
        textPanel.add(title);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(author);
        textPanel.add(Box.createVerticalGlue());

        add(textPanel, BorderLayout.CENTER);

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new BookDetails(book, (BookList) SwingUtilities.getWindowAncestor(BookCard.this));
            }
        });
    }

    String capitalizeText(String text) {
        if (text == null || text.isEmpty())
            return text;
        String capitalized = text.substring(0, 1).toUpperCase() + text.substring(1);
        return capitalized;
    }

    Color getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "reading":
                return Theme.ACCENT;
            case "completed":
                return Theme.GREEN; // Green
            case "on hold":
                return Theme.LABEL_COLOR;
            default:
                return Theme.FOREGROUND; // Default color
        }
    }
}
