import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {CommonModule} from "@angular/common";
import {FormsModule} from '@angular/forms';
import {environment} from '../../environments/environment';
import {NotificationService} from '../services/notification.service';
import {RoleService} from '../services/role.service';
import {ValidationService} from '../services/validation.service';

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
  imports: [CommonModule, FormsModule],
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

  constructor(
    private http: HttpClient, 
    private router: Router,
    private notificationService: NotificationService,
    public roleService: RoleService,
    private validationService: ValidationService
  ) {}

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
    if (!this.roleService.isAdmin()) {
      this.notificationService.error(
        'Acceso Denegado',
        'Solo los administradores pueden crear productos'
      );
      return;
    }
    
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
    if (!this.roleService.isAdmin()) {
      this.notificationService.error(
        'Acceso Denegado',
        'Solo los administradores pueden editar productos'
      );
      return;
    }
    
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
            this.notificationService.success(
              'Producto Actualizado',
              `El producto "${productoActualizado.nombre}" se actualizó correctamente`
            );
            this.cancelarEdicion();
          },
          error: (error) => {
            console.error('Error al actualizar producto:', error);
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
            this.notificationService.success(
              'Producto Creado',
              `El producto "${nuevoProducto.nombre}" se creó correctamente`
            );
            this.cancelarEdicion();
          },
          error: (error) => {
            console.error('Error al crear producto:', error);
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
    if (!this.roleService.isAdmin()) {
      this.notificationService.error(
        'Acceso Denegado',
        'Solo los administradores pueden eliminar productos'
      );
      return;
    }

    const producto = this.productos.find(p => p.id === id);
    if (confirm(`¿Está seguro de que desea eliminar el producto "${producto?.nombre}"?`)) {
      this.http.delete(`${environment.apiUrl}/productos/${id}`)
        .subscribe({
          next: () => {
            this.productos = this.productos.filter(p => p.id !== id);
            this.filtrarProductos();
            this.notificationService.success(
              'Producto Eliminado',
              `El producto "${producto?.nombre}" se eliminó correctamente`
            );
          },
          error: (error) => {
            console.error('Error al eliminar producto:', error);
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

  getEmojiForRoute(route: string): string {
    switch (route) {
      case '/clientes': return '👥';
      case '/productos': return '📦';
      case '/pedidos': return '📋';
      case '/materia-prima': return '🔧';
      case '/ventas': return '💰';
      case '/stock': return '📈';
      default: return '📊';
    }
  }
}
