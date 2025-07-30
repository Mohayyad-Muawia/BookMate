package components;

import java.awt.*;
import javax.swing.*;

public class PrimaryButton extends JButton {

    public PrimaryButton(String text) {
        super(text);

        // Padding inside the button
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Colors
        setBackground(Theme.ACCENT);
        setForeground(Theme.ACCENT_TEXT);

        // Font
        setFont(Theme.MAIN_FONT);

        // Cursor and focus
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Optional size
        setMaximumSize(new Dimension(200, 45)); // restrict width if needed
    }
}
