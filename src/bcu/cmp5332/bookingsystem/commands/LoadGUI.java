package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.gui.LoginWindow;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Font;

public class LoadGUI implements Command {

    private static boolean themeApplied = false;

    private static void applyTheme() {
        if (themeApplied) {
            return;
        }
        themeApplied = true;

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // Fall back to default look and feel.
        }

        UIManager.put("defaultFont", new Font("SansSerif", Font.PLAIN, 13));
    }

    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        SwingUtilities.invokeLater(() -> {
            applyTheme();
            new LoginWindow(fbs);
        });
    }
}
