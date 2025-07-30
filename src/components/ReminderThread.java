package components;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ReminderThread extends Thread {
    private String reminderTime;
    private String message;

    public ReminderThread(String reminderTime, String message) {
        this.reminderTime = reminderTime;
        this.message = message;
    }

    @Override
    public void run() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date reminderDate = sdf.parse(reminderTime);
            long delay = reminderDate.getTime() - System.currentTimeMillis();

            if (delay > 0) {
                Thread.sleep(delay);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, " Reminder: " + message);
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
