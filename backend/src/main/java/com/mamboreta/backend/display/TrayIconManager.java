package com.mamboreta.backend.display;

import com.formdev.flatlaf.FlatLightLaf;
import org.springframework.context.ConfigurableApplicationContext;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

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

        JFrame frame = new JFrame("El Mamboretá");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(360, 420);
        frame.setLayout(new BorderLayout(10, 10));

        // --- Logo ---
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

        // --- Info Panel (versión + IP) ---
        String ipAddress = "IP no disponible";
        try {
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            System.err.println("No se pudo obtener la IP local: " + e.getMessage());
        }

        JLabel versionLabel = new JLabel("Versión " + appVersion, SwingConstants.CENTER);
        versionLabel.setFont(versionLabel.getFont().deriveFont(Font.BOLD, 14f));

        JLabel ipLabel = new JLabel("IP local: " + ipAddress, SwingConstants.CENTER);
        ipLabel.setFont(ipLabel.getFont().deriveFont(Font.PLAIN, 13f));
        ipLabel.setForeground(new Color(90, 90, 90));

        // --- Botón detener ---
        JButton stopButton = new JButton("- Detener servidor -");
        stopButton.setBackground(new Color(245, 199, 215));
        stopButton.setForeground(new Color(120, 0, 50));
        stopButton.setFocusPainted(false);
        stopButton.setFont(stopButton.getFont().deriveFont(Font.BOLD, 13f));
        stopButton.setBorder(BorderFactory.createLineBorder(new Color(210, 150, 180)));
        stopButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        stopButton.addActionListener(e -> stopApplication());

        // --- Panel inferior estilizado ---
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        bottomPanel.setBackground(Color.WHITE);

        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        stopButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottomPanel.add(versionLabel);
        bottomPanel.add(Box.createVerticalStrut(4));
        bottomPanel.add(ipLabel);
        bottomPanel.add(Box.createVerticalStrut(12));
        bottomPanel.add(stopButton);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        // --- Final window setup ---
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
