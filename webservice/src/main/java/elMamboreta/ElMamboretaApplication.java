package elMamboreta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"elMamboreta", "com.mamboreta.backend"})
@EntityScan(basePackages = "com.mamboreta.backend.entity")
@EnableJpaRepositories(basePackages = "com.mamboreta.backend.repository")
public class ElMamboretaApplication {
    public static void main(String[] args) {
		/* TODO:
		    - Analizar si es necesario ejecutar algo antes de iniciar la aplicación.
		*/
        try {
            SpringApplication.run(ElMamboretaApplication.class, args);
        } catch (Exception e) {
            System.out.println("Error al iniciar la aplicación: " + e);
        }
    }
}
