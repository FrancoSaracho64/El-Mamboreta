package com.mamboreta.backend.database;

import com.mamboreta.backend.entity.Cliente;
import com.mamboreta.backend.entity.Producto;
import com.mamboreta.backend.entity.Usuario;
import com.mamboreta.backend.repository.ClienteRepository;
import com.mamboreta.backend.repository.ProductoRepository;
import com.mamboreta.backend.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
     * @param productoRepository El repositorio de la entidad Product, inyectado automáticamente por Spring.
     * @return Una instancia de CommandLineRunner.
     */
    @Bean
    public CommandLineRunner initDatabase(ProductoRepository productoRepository, ClienteRepository clienteRepository, UsuarioRepository usuarioRepository) {
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

            Producto producto4 = new Producto();
            producto4.setNombre("Tangram");
            producto4.setPrecio(1054.00);
            productos.add(producto4);

            Producto producto5 = new Producto();
            producto5.setNombre("Enhebrado");
            producto5.setPrecio(640.00);
            productos.add(producto5);

            Producto producto6 = new Producto();
            producto6.setNombre("PescaMagic");
            producto6.setPrecio(500.00);
            productos.add(producto6);

            Producto producto7 = new Producto();
            producto7.setNombre("TaTeTi");
            producto7.setPrecio(2500.00);
            producto7.setStock(150);
            productos.add(producto7);

            // Guardamos todos los productos en una sola llamada, que es más eficiente.
            productoRepository.saveAll(productos);

            Cliente cliente = new Cliente();
            cliente.setNombre("Franco");
            cliente.setApellido("Saracho");
            cliente.setEmail("fmsaracho64@gmail.com");
            cliente.setDireccion("Salta 2060");
            cliente.setObservaciones("Desarrollador de esta app B)");

            clienteRepository.save(cliente);

            // Alta de usuarios iniciales
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("admin"));
            admin.setRoles(Set.of("ADMIN"));
            admin.setActivo(true);
            usuarioRepository.save(admin);

            Usuario empleado = new Usuario();
            empleado.setUsername("empleado");
            empleado.setPassword(encoder.encode("empleado"));
            empleado.setRoles(Set.of("EMPLEADO"));
            empleado.setActivo(true);
            usuarioRepository.save(empleado);
        };
    }
}
