package com.mamboreta.backend.display;

import com.formdev.flatlaf.FlatLightLaf;
import org.springframework.context.ConfigurableApplicationContext;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class TrayIconManager {
    private final ConfigurableApplicationContext context;
    private final String appVersion;
    private TrayIcon trayIcon;

    public TrayIconManager(ConfigurableApplicationContext context, String appVersion) {
        this.context = context;
        this.appVersion = appVersion;
    }

    public void showTrayIcon() {
        System.setProperty("java.awt.headless", "false");

        if (GraphicsEnvironment.isHeadless() || !SystemTray.isSupported()) {
            System.out.println("SystemTray no disponible, mostrando ventana directamente...");
            SwingUtilities.invokeLater(this::showAboutWindow);
            return;
        }

        try {
            InputStream iconStream = getClass().getResourceAsStream("/mambo_logo.jpg");
            if (iconStream == null) {
                System.err.println("No se encontró mambo_logo.jpg en resources.");
                return;
            }

            Image image = ImageIO.read(iconStream);
            PopupMenu popup = new PopupMenu();

            MenuItem openItem = new MenuItem("Acerca de...");
            openItem.addActionListener(e -> SwingUtilities.invokeLater(this::showAboutWindow));

            MenuItem exitItem = new MenuItem("Detener aplicación");
            exitItem.addActionListener(e -> stopApplication());

            popup.add(openItem);
            popup.addSeparator();
            popup.add(exitItem);

            trayIcon = new TrayIcon(image, "El Mamboreta", popup);
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(e -> SwingUtilities.invokeLater(this::showAboutWindow));

            SystemTray.getSystemTray().add(trayIcon);

            // Notificación emergente al iniciar
            trayIcon.displayMessage(
                    "El Mamboreta",
                    "Aplicación iniciada (versión " + appVersion + ")",
                    TrayIcon.MessageType.INFO
            );

        } catch (IOException | AWTException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(this::showAboutWindow);
        }
    }

    private void showAboutWindow() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ignored) {
        }

        JFrame frame = new JFrame("El Mamboreta");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(360, 420);
        frame.setLayout(new BorderLayout(10, 10));

        try (InputStream logoStream = getClass().getResourceAsStream("/mambo_logo.jpg")) {
            if (logoStream != null) {
                ImageIcon logo = new ImageIcon(ImageIO.read(logoStream));
                JLabel logoLabel = new JLabel(logo);
                logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                frame.add(logoLabel, BorderLayout.CENTER);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JLabel versionLabel = new JLabel("Versión " + appVersion, SwingConstants.CENTER);
        versionLabel.setFont(versionLabel.getFont().deriveFont(Font.BOLD, 14f));

        JButton stopButton = new JButton("- Detener servidor -");
        stopButton.addActionListener(e -> stopApplication());

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        bottomPanel.add(versionLabel);
        bottomPanel.add(stopButton);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void stopApplication() {
        try {
            SystemTray.getSystemTray().remove(trayIcon);
        } catch (Exception ignored) {
        }
        context.close();
        System.exit(0);
    }
}
