package mamboreta.controller;

import com.mamboreta.backend.entity.Documento;
import com.mamboreta.backend.service.DocumentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/documentos")
@CrossOrigin(origins = "*")
public class DocumentoController {

    @Autowired
    private DocumentoService documentoService;

    /**
     * Obtiene todos los documentos
     */
    @GetMapping
    public ResponseEntity<List<Documento>> getAllDocumentos() {
        List<Documento> documentos = documentoService.findAll();
        return ResponseEntity.ok(documentos);
    }

    /**
     * Obtiene un documento por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Documento> getDocumentoById(@PathVariable Long id) {
        Optional<Documento> documento = documentoService.findById(id);
        return documento.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca documentos por número
     */
    @GetMapping("/numero/{numero}")
    public ResponseEntity<Documento> getDocumentoByNumero(@PathVariable String numero) {
        Optional<Documento> documento = documentoService.findByNumero(numero);
        return documento.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca documentos por tipo
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Documento>> getDocumentosByTipo(@PathVariable String tipo) {
        List<Documento> documentos = documentoService.findByTipo(tipo);
        return ResponseEntity.ok(documentos);
    }

    /**
     * Crea un nuevo documento
     */
    @PostMapping
    public ResponseEntity<Documento> createDocumento(@RequestBody Documento documento) {
        try {
            Documento nuevoDocumento = documentoService.save(documento);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDocumento);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualiza un documento existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Documento> updateDocumento(@PathVariable Long id, @RequestBody Documento documento) {
        try {
            Documento documentoActualizado = documentoService.update(id, documento);
            return ResponseEntity.ok(documentoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Elimina un documento
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocumento(@PathVariable Long id) {
        try {
            documentoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 