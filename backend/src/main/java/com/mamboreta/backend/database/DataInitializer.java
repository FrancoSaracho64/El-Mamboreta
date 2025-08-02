package com.mamboreta.backend.database;

import com.mamboreta.backend.entity.Producto;
import com.mamboreta.backend.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase de configuración se encarga de inicializar la base de datos
 * con algunos registros de ejemplo una vez que la aplicación ha arrancado.
 */
@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    /**
     * Define un Bean de tipo CommandLineRunner que se ejecutará al inicio de la aplicación.
     *
     * @param repository El repositorio de la entidad Product, inyectado automáticamente por Spring.
     * @return Una instancia de CommandLineRunner.
     */
    @Bean
    public CommandLineRunner initDatabase(ProductRepository repository) {
        // La expresión lambda dentro del `run` se ejecuta cuando la aplicación está lista.
        return args -> {
            log.info("Inicializando la base de datos con registros de ejemplo...");

            // Creamos los productos usando el constructor sin argumentos y los setters,
            // lo que es compatible con tu clase Producto anotada con Lombok.
            List<Producto> productos = new ArrayList<>();
            Producto producto1 = new Producto();
            producto1.setNombre("Miel de abeja");
            producto1.setPrecio(15.50);
            productos.add(producto1);

            Producto producto2 = new Producto();
            producto2.setNombre("Pan integral");
            producto2.setPrecio(5.25);
            productos.add(producto2);

            Producto producto3 = new Producto();
            producto3.setNombre("Huevos de campo");
            producto3.setPrecio(8.00);
            productos.add(producto3);

            // Guardamos todos los productos en una sola llamada, que es más eficiente.
            repository.saveAll(productos);

            log.info("Datos insertados. Listando todos los productos:");
            repository.findAll().forEach(product -> log.info(product.toString()));
        };
    }
}
