import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PreventSleep {

    private static boolean wasApplicationRunning = false;

    public static void main(String[] args) {
        String applicationName = "scrcpy.exe";  // Specify the application name here
        int interval = 60000;  // Check interval in milliseconds (e.g., 60000ms = 60s)

        try {
            Robot robot = new Robot();
            listRunningApplications();  // List currently running applications

            while (true) {
                boolean isRunning = isApplicationRunning(applicationName);

                if (isRunning && !wasApplicationRunning) {
                    System.out.println(applicationName + " is running. Preventing sleep...");
                    showNotification(applicationName + " detected. Preventing sleep.");
                    wasApplicationRunning = true;
                } else if (!isRunning && wasApplicationRunning) {
                    System.out.println(applicationName + " is not running anymore.");
                    showNotification(applicationName + " is not running anymore. Sleep prevention stopped.");
                    wasApplicationRunning = false;
                }

                if (isRunning) {
                    // Simulate a key press (e.g., VK_SHIFT)
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                }

                Thread.sleep(interval);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isApplicationRunning(String applicationName) {
        try {
            String line;
            Process p = new ProcessBuilder("tasklist").start();
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while ((line = input.readLine()) != null) {
                if (line.contains(applicationName)) {
                    input.close();
                    return true;
                }
            }
            input.close();
        } catch (Exception err) {
            err.printStackTrace();
        }
        return false;
    }

    private static void listRunningApplications() {
        try {
            String line;
            Process p = new ProcessBuilder("tasklist").start();
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            System.out.println("Currently running applications:");
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
            input.close();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    private static void showNotification(String message) {
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png"); 
            TrayIcon trayIcon = new TrayIcon(image, "Prevent Sleep");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("Prevent Sleep Notification");

            try {
                tray.add(trayIcon);
                trayIcon.displayMessage("Prevent Sleep", message, TrayIcon.MessageType.INFO);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("System tray not supported!");
        }
    }
}
