package mamboreta.controller;

import com.mamboreta.backend.entity.Telefono;
import com.mamboreta.backend.service.TelefonoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/telefonos")
@CrossOrigin(origins = "*")
public class TelefonoController {

    @Autowired
    private TelefonoService telefonoService;

    /**
     * Obtiene todos los teléfonos activos
     */
    @GetMapping
    public ResponseEntity<List<Telefono>> getAllTelefonos() {
        List<Telefono> telefonos = telefonoService.findAllActivos();
        return ResponseEntity.ok(telefonos);
    }

    /**
     * Obtiene todos los teléfonos (incluyendo inactivos)
     */
    @GetMapping("/all")
    public ResponseEntity<List<Telefono>> getAllTelefonosIncludingInactive() {
        List<Telefono> telefonos = telefonoService.findAll();
        return ResponseEntity.ok(telefonos);
    }

    /**
     * Obtiene un teléfono por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Telefono> getTelefonoById(@PathVariable Long id) {
        Optional<Telefono> telefono = telefonoService.findById(id);
        return telefono.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca teléfonos por número
     */
    @GetMapping("/search")
    public ResponseEntity<List<Telefono>> searchTelefonos(@RequestParam String numero) {
        List<Telefono> telefonos = telefonoService.findByNumeroContaining(numero);
        return ResponseEntity.ok(telefonos);
    }

    /**
     * Busca teléfonos por tipo
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Telefono>> getTelefonosByTipo(@PathVariable String tipo) {
        List<Telefono> telefonos = telefonoService.findByTipo(tipo);
        return ResponseEntity.ok(telefonos);
    }

    /**
     * Crea un nuevo teléfono
     */
    @PostMapping
    public ResponseEntity<Telefono> createTelefono(@RequestBody Telefono telefono) {
        try {
            Telefono nuevoTelefono = telefonoService.save(telefono);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTelefono);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualiza un teléfono existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Telefono> updateTelefono(@PathVariable Long id, @RequestBody Telefono telefono) {
        try {
            Telefono telefonoActualizado = telefonoService.update(id, telefono);
            return ResponseEntity.ok(telefonoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Desactiva un teléfono (borrado lógico)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTelefono(@PathVariable Long id) {
        try {
            telefonoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 