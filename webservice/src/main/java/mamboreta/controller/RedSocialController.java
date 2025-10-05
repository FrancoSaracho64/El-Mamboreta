package mamboreta.controller;

import com.mamboreta.backend.entity.RedSocial;
import com.mamboreta.backend.service.RedSocialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/redes-sociales")
@CrossOrigin(origins = "*")
public class RedSocialController {

    @Autowired
    private RedSocialService redSocialService;

    /**
     * Obtiene todas las redes sociales activas
     */
    @GetMapping
    public ResponseEntity<List<RedSocial>> getAllRedesSociales() {
        List<RedSocial> redesSociales = redSocialService.findAllActivos();
        return ResponseEntity.ok(redesSociales);
    }

    /**
     * Obtiene todas las redes sociales (incluyendo inactivas)
     */
    @GetMapping("/all")
    public ResponseEntity<List<RedSocial>> getAllRedesSocialesIncludingInactive() {
        List<RedSocial> redesSociales = redSocialService.findAll();
        return ResponseEntity.ok(redesSociales);
    }

    /**
     * Obtiene una red social por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<RedSocial> getRedSocialById(@PathVariable Long id) {
        Optional<RedSocial> redSocial = redSocialService.findById(id);
        return redSocial.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca redes sociales por nombre de red
     */
    @GetMapping("/red/{red}")
    public ResponseEntity<List<RedSocial>> getRedesSocialesByRed(@PathVariable String red) {
        List<RedSocial> redesSociales = redSocialService.findByRed(red);
        return ResponseEntity.ok(redesSociales);
    }

    /**
     * Busca redes sociales por usuario
     */
    @GetMapping("/search")
    public ResponseEntity<List<RedSocial>> searchRedesSociales(@RequestParam String usuario) {
        List<RedSocial> redesSociales = redSocialService.findByUsuarioContaining(usuario);
        return ResponseEntity.ok(redesSociales);
    }

    /**
     * Busca redes sociales por tipo de red y usuario
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<RedSocial>> buscarRedesSociales(
            @RequestParam String red, 
            @RequestParam String usuario) {
        List<RedSocial> redesSociales = redSocialService.findByRedAndUsuarioContaining(red, usuario);
        return ResponseEntity.ok(redesSociales);
    }

    /**
     * Crea una nueva red social
     */
    @PostMapping
    public ResponseEntity<RedSocial> createRedSocial(@RequestBody RedSocial redSocial) {
        try {
            RedSocial nuevaRedSocial = redSocialService.save(redSocial);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaRedSocial);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualiza una red social existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<RedSocial> updateRedSocial(@PathVariable Long id, @RequestBody RedSocial redSocial) {
        try {
            RedSocial redSocialActualizada = redSocialService.update(id, redSocial);
            return ResponseEntity.ok(redSocialActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Desactiva una red social (borrado lógico)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRedSocial(@PathVariable Long id) {
        try {
            redSocialService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 