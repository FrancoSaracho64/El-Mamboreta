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

import java.util.*;

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
            productos.add(new Producto(null, "PESCAMAGIC + LAGO", 700.0, null, true, null, 0));
            productos.add(new Producto(null, "PESCAMAGIC", 700.0, null, true, null, 0));
            productos.add(new Producto(null, "TANGRAM CUADRADO", 1300.0, null, true, null, 0));
            productos.add(new Producto(null, "TANGRAM TABLETA", 1000.0, null, true, null, 0));
            productos.add(new Producto(null, "ENHEBRADOS SIMPLES", 830.0, null, true, null, 0));
            productos.add(new Producto(null, "ENHEBRADO GRABADO", 1080.0, null, true, null, 0));
            productos.add(new Producto(null, "ENHEBRADOS FORMIS", 760.0, null, true, null, 0));
            productos.add(new Producto(null, "TETRIS", 1300.0, null, true, null, 0));
            productos.add(new Producto(null, "ATRAPAMOSCAS", 1260.0, null, true, null, 0));
            productos.add(new Producto(null, "TA-TE-TI MDF", 750.0, null, true, null, 0));
            productos.add(new Producto(null, "TÍTERE DEDO MDF", 680.0, null, true, null, 0));
            productos.add(new Producto(null, "TRABADOS", 850.0, null, true, null, 0));
            productos.add(new Producto(null, "TA-TE-TI FICHA G.E.", 560.0, null, true, null, 0));
            productos.add(new Producto(null, "MEMOTEST", 950.0, null, true, null, 0));

            // Guardamos todos los productos en una sola llamada, que es más eficiente.
            productoRepository.saveAll(productos);

            List<String> nombres = List.of("Lucas", "Martina", "Sofía", "Juan", "Lucía", "Pedro", "Ana", "Franco", "Julieta", "Marcos",
                    "Valentina", "Mateo", "Carla", "Nicolás", "Camila", "Tomás", "Mía", "Joaquín", "Rocío", "Diego");
            List<String> apellidos = List.of("Gómez", "Pérez", "Rodríguez", "Fernández", "López", "Martínez", "García", "Romero", "Sánchez", "Díaz");

            Random random = new Random();
            List<Cliente> clientes = new ArrayList<>();

            for (int i = 1; i <= 50; i++) {
                Cliente cliente = new Cliente();
                String nombre = nombres.get(random.nextInt(nombres.size()));
                String apellido = apellidos.get(random.nextInt(apellidos.size()));
                cliente.setNombre(nombre);
                cliente.setApellido(apellido);
                cliente.setEmail(nombre.toLowerCase() + "." + apellido.toLowerCase() + i + "@correo.com");
                cliente.setDireccion("Calle " + (100 + i) + ", Ciudad " + (random.nextInt(10) + 1));
                cliente.setActivo(true);
                cliente.setObservaciones("Cliente generado automáticamente para pruebas (" + UUID.randomUUID().toString().substring(0, 8) + ")");
                clientes.add(cliente);
            }

            clienteRepository.saveAll(clientes);

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
