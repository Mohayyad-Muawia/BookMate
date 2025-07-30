package components;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import java.awt.Dimension;

public class MyTextField extends JTextField {

    public MyTextField() {
        this(20); // Default to 20 columns
    }

    public MyTextField(int columns) {
        super(columns);
        setBackground(Theme.INPUT_BG);
        setForeground(Theme.INPUT_TEXT);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }
}
