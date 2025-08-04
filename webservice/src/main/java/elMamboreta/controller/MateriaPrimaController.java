package elMamboreta.controller;

import com.mamboreta.backend.entity.MateriaPrima;
import com.mamboreta.backend.service.MateriaPrimaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/materias-primas")
@CrossOrigin(origins = "*")
public class MateriaPrimaController {

    @Autowired
    private MateriaPrimaService materiaPrimaService;

    /**
     * Obtiene todas las materias primas activas
     */
    @GetMapping
    public ResponseEntity<List<MateriaPrima>> getAllMateriasPrimas() {
        List<MateriaPrima> materiasPrimas = materiaPrimaService.findAllActivos();
        return ResponseEntity.ok(materiasPrimas);
    }

    /**
     * Obtiene todas las materias primas (incluyendo inactivas)
     */
    @GetMapping("/all")
    public ResponseEntity<List<MateriaPrima>> getAllMateriasPrimasIncludingInactive() {
        List<MateriaPrima> materiasPrimas = materiaPrimaService.findAll();
        return ResponseEntity.ok(materiasPrimas);
    }

    /**
     * Obtiene una materia prima por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MateriaPrima> getMateriaPrimaById(@PathVariable Long id) {
        Optional<MateriaPrima> materiaPrima = materiaPrimaService.findById(id);
        return materiaPrima.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca materias primas por nombre
     */
    @GetMapping("/search")
    public ResponseEntity<List<MateriaPrima>> searchMateriasPrimas(@RequestParam String nombre) {
        List<MateriaPrima> materiasPrimas = materiaPrimaService.findByNombreContaining(nombre);
        return ResponseEntity.ok(materiasPrimas);
    }

    /**
     * Busca materias primas por rango de precio
     */
    @GetMapping("/precio")
    public ResponseEntity<List<MateriaPrima>> getMateriasPrimasByPrecioRange(
            @RequestParam Double precioMin, 
            @RequestParam Double precioMax) {
        List<MateriaPrima> materiasPrimas = materiaPrimaService.findByPrecioBetween(precioMin, precioMax);
        return ResponseEntity.ok(materiasPrimas);
    }

    /**
     * Busca materias primas con stock bajo
     */
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<MateriaPrima>> getMateriasPrimasConStockBajo(@RequestParam Integer stockMinimo) {
        List<MateriaPrima> materiasPrimas = materiaPrimaService.findByStockLessThan(stockMinimo);
        return ResponseEntity.ok(materiasPrimas);
    }

    /**
     * Busca materias primas sin stock
     */
    @GetMapping("/sin-stock")
    public ResponseEntity<List<MateriaPrima>> getMateriasPrimasSinStock() {
        List<MateriaPrima> materiasPrimas = materiaPrimaService.findMateriasPrimasSinStock();
        return ResponseEntity.ok(materiasPrimas);
    }

    /**
     * Busca materias primas por unidad de medida
     */
    @GetMapping("/unidad-medida/{unidadMedida}")
    public ResponseEntity<List<MateriaPrima>> getMateriasPrimasByUnidadMedida(@PathVariable String unidadMedida) {
        List<MateriaPrima> materiasPrimas = materiaPrimaService.findByUnidadMedida(unidadMedida);
        return ResponseEntity.ok(materiasPrimas);
    }

    /**
     * Crea una nueva materia prima
     */
    @PostMapping
    public ResponseEntity<MateriaPrima> createMateriaPrima(@RequestBody MateriaPrima materiaPrima) {
        try {
            MateriaPrima nuevaMateriaPrima = materiaPrimaService.save(materiaPrima);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMateriaPrima);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Actualiza una materia prima existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<MateriaPrima> updateMateriaPrima(@PathVariable Long id, @RequestBody MateriaPrima materiaPrima) {
        try {
            MateriaPrima materiaPrimaActualizada = materiaPrimaService.update(id, materiaPrima);
            return ResponseEntity.ok(materiaPrimaActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Desactiva una materia prima (borrado lógico)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMateriaPrima(@PathVariable Long id) {
        try {
            materiaPrimaService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Actualiza el stock de una materia prima
     */
    @PutMapping("/{id}/stock")
    public ResponseEntity<MateriaPrima> updateStock(@PathVariable Long id, @RequestParam Integer nuevoStock) {
        try {
            MateriaPrima materiaPrima = materiaPrimaService.updateStock(id, nuevoStock);
            return ResponseEntity.ok(materiaPrima);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Incrementa el stock de una materia prima
     */
    @PutMapping("/{id}/incrementar-stock")
    public ResponseEntity<MateriaPrima> incrementarStock(@PathVariable Long id, @RequestParam Integer cantidad) {
        try {
            MateriaPrima materiaPrima = materiaPrimaService.incrementarStock(id, cantidad);
            return ResponseEntity.ok(materiaPrima);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Decrementa el stock de una materia prima
     */
    @PutMapping("/{id}/decrementar-stock")
    public ResponseEntity<MateriaPrima> decrementarStock(@PathVariable Long id, @RequestParam Integer cantidad) {
        try {
            MateriaPrima materiaPrima = materiaPrimaService.decrementarStock(id, cantidad);
            return ResponseEntity.ok(materiaPrima);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 