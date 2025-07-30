package components;

import javax.swing.BorderFactory;
import javax.swing.JPasswordField;

public class MyPassField extends JPasswordField {

    public MyPassField() {
        this(20); // Default to 20 columns
        setEchoChar('*'); // Set the echo character for password masking
    }

    public MyPassField(int columns) {
        super(columns);
        setEchoChar('*'); // Set the echo character for password masking
        setBackground(Theme.INPUT_BG);
        setForeground(Theme.INPUT_TEXT);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

}
