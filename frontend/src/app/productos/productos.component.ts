import {HttpClient, HttpClientModule} from '@angular/common/http';
import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import { FormsModule } from '@angular/forms';
import {environment} from '../../environments/environment';
import {CommonModule} from "@angular/common";

interface Producto {
  id?: number;
  nombre: string;
  precio: number;
  stock: number;
  descripcion?: string;
  activo: boolean;
}

@Component({
  selector: 'app-productos',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule],
  templateUrl: './productos.component.html',
  styleUrls: ['./productos.component.css']
})

export class ProductosComponent implements OnInit {
  productos: Producto[] = [];
  productosFiltrados: Producto[] = [];
  producto: Producto = {
    nombre: '',
    precio: 0,
    stock: 0,
    descripcion: '',
    activo: true
  };

  mostrarForm = false;
  productoEditando: Producto | null = null;
  terminoBusqueda = '';

  constructor(private http: HttpClient, private router: Router) {
  }

  ngOnInit(): void {
    this.cargarProductos();
  }

  cargarProductos(): void {
    this.http.get<Producto[]>(`${environment.apiUrl}/productos`).subscribe({
      next: (data) => {
        this.productos = data;
        this.productosFiltrados = [...this.productos];
      },
      error: (error) => {
        console.error('Error al cargar productos:', error);
        // Datos mock para desarrollo
        this.productos = [
          {
            id: 1,
            nombre: 'Producto A',
            precio: 100.50,
            stock: 25,
            descripcion: 'Descripción del producto A',
            activo: true
          },
          {
            id: 2,
            nombre: 'Producto B',
            precio: 75.00,
            stock: 5,
            descripcion: 'Descripción del producto B',
            activo: true
          }
        ];
        this.productosFiltrados = [...this.productos];
      }
    });
  }

  mostrarFormulario(): void {
    this.mostrarForm = true;
    this.productoEditando = null;
    this.producto = {
      nombre: '',
      precio: 0,
      stock: 0,
      descripcion: '',
      activo: true
    };
  }

  editarProducto(producto: Producto): void {
    this.productoEditando = producto;
    this.producto = {...producto};
    this.mostrarForm = true;
  }

  guardarProducto(): void {
    if (this.productoEditando) {
      // Actualizar producto existente
      this.http.put<Producto>(`${environment.apiUrl}/productos/${this.productoEditando.id}`, this.producto)
        .subscribe({
          next: (productoActualizado) => {
            const index = this.productos.findIndex(p => p.id === productoActualizado.id);
            if (index !== -1) {
              this.productos[index] = productoActualizado;
              this.filtrarProductos();
            }
            this.cancelarEdicion();
          },
          error: (error) => {
            console.error('Error al actualizar producto:', error);
            // Simular actualización local para desarrollo
            const index = this.productos.findIndex(p => p.id === this.productoEditando?.id);
            if (index !== -1) {
              this.productos[index] = {...this.producto, id: this.productoEditando?.id};
              this.filtrarProductos();
            }
            this.cancelarEdicion();
          }
        });
    } else {
      // Crear nuevo producto
      this.http.post<Producto>(`${environment.apiUrl}/productos`, this.producto)
        .subscribe({
          next: (nuevoProducto) => {
            this.productos.push(nuevoProducto);
            this.filtrarProductos();
            this.cancelarEdicion();
          },
          error: (error) => {
            console.error('Error al crear producto:', error);
            // Simular creación local para desarrollo
            const nuevoId = Math.max(...this.productos.map(p => p.id || 0)) + 1;
            const nuevoProducto = {...this.producto, id: nuevoId};
            this.productos.push(nuevoProducto);
            this.filtrarProductos();
            this.cancelarEdicion();
          }
        });
    }
  }

  eliminarProducto(id: number): void {
    if (confirm('¿Está seguro de que desea eliminar este producto?')) {
      this.http.delete(`${environment.apiUrl}/productos/${id}`)
        .subscribe({
          next: () => {
            this.productos = this.productos.filter(p => p.id !== id);
            this.filtrarProductos();
          },
          error: (error) => {
            console.error('Error al eliminar producto:', error);
            // Simular eliminación local para desarrollo
            this.productos = this.productos.filter(p => p.id !== id);
            this.filtrarProductos();
          }
        });
    }
  }

  cancelarEdicion(): void {
    this.mostrarForm = false;
    this.productoEditando = null;
    this.producto = {
      nombre: '',
      precio: 0,
      stock: 0,
      descripcion: '',
      activo: true
    };
  }

  filtrarProductos(): void {
    if (!this.terminoBusqueda.trim()) {
      this.productosFiltrados = [...this.productos];
    } else {
      const termino = this.terminoBusqueda.toLowerCase();
      this.productosFiltrados = this.productos.filter(producto =>
        producto.nombre.toLowerCase().includes(termino) ||
        producto.descripcion?.toLowerCase().includes(termino)
      );
    }
  }

  volverAlMenu(): void {
    this.router.navigate(['/home']);
  }
}
