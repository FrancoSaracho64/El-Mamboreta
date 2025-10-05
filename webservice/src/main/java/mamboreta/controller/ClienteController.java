package mamboreta.controller;

import com.mamboreta.backend.entity.Cliente;
import com.mamboreta.backend.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    /**
     * Obtiene todos los clientes activos
     */
    @GetMapping
    public ResponseEntity<List<Cliente>> getAllClientes() {
        List<Cliente> clientes = clienteService.findAllActivos();
        return ResponseEntity.ok(clientes);
    }

    /**
     * Obtiene todos los clientes (incluyendo inactivos)
     */
    @GetMapping("/all")
    public ResponseEntity<List<Cliente>> getAllClientesIncludingInactive() {
        List<Cliente> clientes = clienteService.findAll();
        return ResponseEntity.ok(clientes);
    }

    /**
     * Obtiene un cliente por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Long id) {
        Optional<Cliente> cliente = clienteService.findById(id);
        return cliente.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca clientes por nombre o apellido
     */
    @GetMapping("/search")
    public ResponseEntity<List<Cliente>> searchClientes(@RequestParam String nombre) {
        List<Cliente> clientes = clienteService.findByNombreContaining(nombre);
        return ResponseEntity.ok(clientes);
    }

    /**
     * Busca un cliente por email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Cliente> getClienteByEmail(@PathVariable String email) {
        Optional<Cliente> cliente = clienteService.findByEmail(email);
        return cliente.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca un cliente por número de documento
     */
    @GetMapping("/documento/{numeroDocumento}")
    public ResponseEntity<Cliente> getClienteByDocumento(@PathVariable String numeroDocumento) {
        Optional<Cliente> cliente = clienteService.findByNumeroDocumento(numeroDocumento);
        return cliente.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crea un nuevo cliente
     */
    @PostMapping
    public ResponseEntity<Cliente> createCliente(@RequestBody Cliente cliente) {
        try {
            Cliente nuevoCliente = clienteService.save(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualiza un cliente existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable Long id, @RequestBody Cliente cliente) {
        try {
            Cliente clienteActualizado = clienteService.update(id, cliente);
            return ResponseEntity.ok(clienteActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Desactiva un cliente (borrado lógico)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        try {
            clienteService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Verifica si existe un cliente con el email dado
     */
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        boolean exists = clienteService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    /**
     * Verifica si existe un cliente con el número de documento dado
     */
    @GetMapping("/exists/documento/{numeroDocumento}")
    public ResponseEntity<Boolean> existsByDocumento(@PathVariable String numeroDocumento) {
        boolean exists = clienteService.existsByDocumentoNumero(numeroDocumento);
        return ResponseEntity.ok(exists);
    }
} 