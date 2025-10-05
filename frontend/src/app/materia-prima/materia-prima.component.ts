import { Component, OnInit } from '@angular/core';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";

interface MateriaPrima {
  id?: number;
  nombre: string;
  descripcion?: string;
  stock: number;
  unidadMedida?: string;
  activo: boolean;
}

@Component({
  selector: 'app-materia-prima',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule],
  templateUrl: './materia-prima.component.html',
  styleUrls: ['./materia-prima.component.css']
})
export class MateriaPrimaComponent implements OnInit {

  /** 📦 Listado y vista filtrada */
  listaMateriaPrima: MateriaPrima[] = [];
  materiaPrimaFiltrada: MateriaPrima[] = [];

  /** 🧱 Entidad en edición / creación */
  materiaPrimaActual: MateriaPrima = this.nuevaMateriaPrima();

  /** ⚙️ Control de formulario y búsqueda */
  mostrarForm = false;
  materiaPrimaEditando: MateriaPrima | null = null;
  terminoBusqueda = '';

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.cargarMateriaPrima();
    console.log("se mando a cargar")
  }

  /** 🔁 Inicializa una materia prima vacía */
  private nuevaMateriaPrima(): MateriaPrima {
    return {
      nombre: '',
      descripcion: '',
      stock: 0,
      unidadMedida: '',
      activo: true
    };
  }

  /** 📥 Carga la lista de materias primas */
  cargarMateriaPrima(): void {
    this.http.get<MateriaPrima[]>(`${environment.apiUrl}/materias-primas`).subscribe({
      next: (data) => {
        console.log("vino la data");
        console.log(data);
        this.listaMateriaPrima = data;
        this.materiaPrimaFiltrada = [...this.listaMateriaPrima];
      },
      error: (error) => {
        console.error('Error al cargar materia prima:', error);
        // 🔧 Datos mock para desarrollo local
        this.listaMateriaPrima = [
          { id: 1, nombre: 'Harina', descripcion: 'Harina de trigo', stock: 50, unidadMedida: 'KG', activo: true },
          { id: 2, nombre: 'Azúcar', descripcion: 'Azúcar refinada', stock: 3, unidadMedida: 'KG', activo: true }
        ];
        this.materiaPrimaFiltrada = [...this.listaMateriaPrima];
      }
    });
  }

  /** 🧾 Muestra el formulario vacío */
  mostrarFormulario(): void {
    this.mostrarForm = true;
    this.materiaPrimaEditando = null;
    this.materiaPrimaActual = this.nuevaMateriaPrima();
  }

  /** ✏️ Edita una materia prima existente */
  editarMateriaPrima(materia: MateriaPrima): void {
    this.materiaPrimaEditando = materia;
    this.materiaPrimaActual = { ...materia };
    this.mostrarForm = true;
  }

  /** 💾 Guarda una nueva materia prima o actualiza una existente */
  guardarMateriaPrima(): void {
    if (this.materiaPrimaEditando) {
      // 🔄 Actualizar existente
      this.http.put<MateriaPrima>(
        `${environment.apiUrl}/materias-primas/${this.materiaPrimaEditando.id}`,
        this.materiaPrimaActual
      ).subscribe({
        next: (materiaActualizada) => {
          const index = this.listaMateriaPrima.findIndex(m => m.id === materiaActualizada.id);
          if (index !== -1) this.listaMateriaPrima[index] = materiaActualizada;
          this.filtrarMateriaPrima();
          this.cancelarEdicion();
        },
        error: (error) => {
          console.error('Error al actualizar materia prima:', error);
          // Simular actualización local
          const index = this.listaMateriaPrima.findIndex(m => m.id === this.materiaPrimaEditando?.id);
          if (index !== -1) {
            this.listaMateriaPrima[index] = { ...this.materiaPrimaActual, id: this.materiaPrimaEditando?.id };
          }
          this.filtrarMateriaPrima();
          this.cancelarEdicion();
        }
      });
    } else {
      // ➕ Crear nueva
      this.http.post<MateriaPrima>(
        `${environment.apiUrl}/materias-primas`,
        this.materiaPrimaActual
      ).subscribe({
        next: (nuevaMateria) => {
          this.listaMateriaPrima.push(nuevaMateria);
          this.filtrarMateriaPrima();
          this.cancelarEdicion();
        },
        error: (error) => {
          console.error('Error al crear materia prima:', error);
          // Simular creación local
          const nuevoId = Math.max(...this.listaMateriaPrima.map(m => m.id || 0)) + 1;
          const nuevaMateria = { ...this.materiaPrimaActual, id: nuevoId };
          this.listaMateriaPrima.push(nuevaMateria);
          this.filtrarMateriaPrima();
          this.cancelarEdicion();
        }
      });
    }
  }

  /** 🗑️ Elimina una materia prima (segura con confirmación) */
  eliminarMateriaPrima(id?: number): void {
    if (!id) return;
    if (confirm('¿Está seguro de que desea eliminar esta materia prima?')) {
      this.http.delete(`${environment.apiUrl}/materias-primas/${id}`).subscribe({
        next: () => {
          this.listaMateriaPrima = this.listaMateriaPrima.filter(m => m.id !== id);
          this.filtrarMateriaPrima();
        },
        error: (error) => {
          console.error('Error al eliminar materia prima:', error);
          // Simular eliminación local
          this.listaMateriaPrima = this.listaMateriaPrima.filter(m => m.id !== id);
          this.filtrarMateriaPrima();
        }
      });
    }
  }

  /** ❌ Cancela edición o creación */
  cancelarEdicion(): void {
    this.mostrarForm = false;
    this.materiaPrimaEditando = null;
    this.materiaPrimaActual = this.nuevaMateriaPrima();
  }

  /** 🔍 Filtra la lista según el término de búsqueda */
  filtrarMateriaPrima(): void {
    const termino = this.terminoBusqueda.trim().toLowerCase();
    this.materiaPrimaFiltrada = termino
      ? this.listaMateriaPrima.filter(m =>
        m.nombre.toLowerCase().includes(termino) ||
        m.descripcion?.toLowerCase().includes(termino)
      )
      : [...this.listaMateriaPrima];
  }

  /** ↩️ Vuelve al menú principal */
  volverAlMenu(): void {
    this.router.navigate(['/home']);
  }
}
