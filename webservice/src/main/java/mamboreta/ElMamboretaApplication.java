package mamboreta;

import com.mamboreta.backend.display.TrayIconManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.swing.*;

@SpringBootApplication
@ComponentScan(basePackages = {"mamboreta", "com.mamboreta.backend"})
@EntityScan(basePackages = "com.mamboreta.backend.entity")
@EnableJpaRepositories(basePackages = "com.mamboreta.backend.repository")
public class ElMamboretaApplication {
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");

        try {
            ConfigurableApplicationContext context = SpringApplication.run(ElMamboretaApplication.class, args);
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());

            // Obtener versión desde application.properties
            Environment env = context.getEnvironment();
            String version = env.getProperty("app.version", "Desconocida");

            javax.swing.SwingUtilities.invokeLater(() -> {
                TrayIconManager trayIconManager = new TrayIconManager(context, version);
                trayIconManager.showTrayIcon();
            });

        } catch (Exception e) {
            System.out.println("Error al iniciar la aplicación: " + e);
        }
    }
}
