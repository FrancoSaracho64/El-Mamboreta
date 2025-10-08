import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";

interface Cliente {
  id: number;
  nombre: string;
  apellido?: string;
}

interface Producto {
  id: number;
  nombre: string;
  precio: number;
}

interface Venta {
  id?: number;
  clienteId: number;
  total: number;
  productos?: Producto[];
  fechaVenta?: string;
}

@Component({
  selector: 'app-ventas',
  templateUrl: './ventas.component.html',
  standalone: true,
  imports: [CommonModule, FormsModule],
  styleUrl: './ventas.component.css'
})
export class VentasComponent implements OnInit {
  ventas: Venta[] = [];
  ventasFiltradas: Venta[] = [];
  clientes: Cliente[] = [];
  productos: Producto[] = [];
  venta: Venta = {
    clienteId: 0,
    total: 0,
    productos: []
  };

  mostrarForm = false;
  ventaEditando: Venta | null = null;
  terminoBusqueda = '';
  productosSeleccionados: number[] = [];

  constructor(private http: HttpClient, private router: Router) {
  }

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    // Cargar clientes
  this.http.get<Cliente[]>(`${environment.apiUrl}/clientes`).subscribe({
      next: (data) => {
        this.clientes = data;
      },
      error: (error) => {
        console.error('Error al cargar clientes:', error);
      }
    });

    // Cargar productos
  this.http.get<Producto[]>(`${environment.apiUrl}/productos`).subscribe({
      next: (data) => {
        this.productos = data;
      },
      error: (error) => {
        console.error('Error al cargar productos:', error);
      }
    });

    // Cargar ventas
    this.cargarVentas();
  }

  cargarVentas(): void {
  this.http.get<Venta[]>(`${environment.apiUrl}/ventas`).subscribe({
      next: (data) => {
        this.ventas = data;
        this.ventasFiltradas = [...this.ventas];
      },
      error: (error) => {
        console.error('Error al cargar ventas:', error);
        this.ventasFiltradas = [...this.ventas];
      }
    });
  }

  mostrarFormulario(): void {
    this.mostrarForm = true;
    this.ventaEditando = null;
    this.venta = {
      clienteId: 0,
      total: 0,
      productos: []
    };
    this.productosSeleccionados = [];
  }

  editarVenta(venta: Venta): void {
    this.ventaEditando = venta;
    this.venta = {...venta};
    this.productosSeleccionados = venta.productos?.map(p => p.id) || [];
    this.mostrarForm = true;
  }

  toggleProducto(productoId: number, event: any): void {
    if (event.target.checked) {
      this.productosSeleccionados.push(productoId);
    } else {
      this.productosSeleccionados = this.productosSeleccionados.filter(id => id !== productoId);
    }
    this.calcularTotal();
  }

  calcularTotal(): void {
    this.venta.total = this.productos
      .filter(p => this.productosSeleccionados.includes(p.id))
      .reduce((sum, producto) => sum + producto.precio, 0);
  }

  guardarVenta(): void {
    // Agregar productos seleccionados a la venta
    this.venta.productos = this.productos.filter(p => this.productosSeleccionados.includes(p.id));
    this.calcularTotal();

    if (this.ventaEditando) {
      // Actualizar venta existente
  this.http.put<Venta>(`${environment.apiUrl}/ventas/${this.ventaEditando.id}`, this.venta)
        .subscribe({
          next: (ventaActualizada) => {
            const index = this.ventas.findIndex(v => v.id === ventaActualizada.id);
            if (index !== -1) {
              this.ventas[index] = ventaActualizada;
              this.filtrarVentas();
            }
            this.cancelarEdicion();
          },
          error: (error) => {
            console.error('Error al actualizar venta:', error);
            // Simular actualización local para desarrollo
            const index = this.ventas.findIndex(v => v.id === this.ventaEditando?.id);
            if (index !== -1) {
              this.ventas[index] = {...this.venta, id: this.ventaEditando?.id};
              this.filtrarVentas();
            }
            this.cancelarEdicion();
          }
        });
    } else {
      // Crear nueva venta
  this.http.post<Venta>(`${environment.apiUrl}/ventas`, this.venta)
        .subscribe({
          next: (nuevaVenta) => {
            this.ventas.push(nuevaVenta);
            this.filtrarVentas();
            this.cancelarEdicion();
          },
          error: (error) => {
            console.error('Error al crear venta:', error);
            // Simular creación local para desarrollo
            const nuevoId = Math.max(...this.ventas.map(v => v.id || 0)) + 1;
            const nuevaVenta = {...this.venta, id: nuevoId};
            this.ventas.push(nuevaVenta);
            this.filtrarVentas();
            this.cancelarEdicion();
          }
        });
    }
  }

  eliminarVenta(id: number): void {
    if (confirm('¿Está seguro de que desea eliminar esta venta?')) {
      this.http.delete(`${environment.apiUrl}/ventas/${id}`)
        .subscribe({
          next: () => {
            this.ventas = this.ventas.filter(v => v.id !== id);
            this.filtrarVentas();
          },
          error: (error) => {
            console.error('Error al eliminar venta:', error);
            // Simular eliminación local para desarrollo
            this.ventas = this.ventas.filter(v => v.id !== id);
            this.filtrarVentas();
          }
        });
    }
  }

  cancelarEdicion(): void {
    this.mostrarForm = false;
    this.ventaEditando = null;
    this.venta = {
      clienteId: 0,
      total: 0,
      productos: []
    };
    this.productosSeleccionados = [];
  }

  filtrarVentas(): void {
    if (!this.terminoBusqueda.trim()) {
      this.ventasFiltradas = [...this.ventas];
    } else {
      const termino = this.terminoBusqueda.toLowerCase();
      this.ventasFiltradas = this.ventas.filter(venta => {
        const nombreCliente = this.obtenerNombreCliente(venta.clienteId).toLowerCase();
        return nombreCliente.includes(termino);
      });
    }
  }

  obtenerNombreCliente(clienteId: number): string {
    const cliente = this.clientes.find(c => c.id === clienteId);
    return cliente ? `${cliente.nombre} ${cliente.apellido || ''}`.trim() : 'Cliente no encontrado';
  }

  volverAlMenu(): void {
    this.router.navigate(['/home']);
  }
}
