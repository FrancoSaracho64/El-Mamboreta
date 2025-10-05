import {Component, OnInit} from '@angular/core';
import {HttpClient, HttpClientModule} from '@angular/common/http';
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
  productoId: number;
  cantidad: number;
  // precio: number;
}

interface Pedido {
  id?: number;
  clienteId: number;
  estado: string;
  productos?: Producto[];
  fechaSolicitado?: string;
}

@Component({
  selector: 'app-pedidos',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule],
  templateUrl: './pedidos.component.html',
  styleUrl: './pedidos.component.css'
})
export class PedidosComponent implements OnInit {
  pedidos: Pedido[] = [];
  pedidosFiltrados: Pedido[] = [];
  clientes: Cliente[] = [];
  productos: Producto[] = [];
  pedido: Pedido = {
    clienteId: 0,
    estado: '',
    productos: []
  };

  mostrarForm = false;
  pedidoEditando: Pedido | null = null;
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

    // Cargar pedidos
    this.cargarPedidos();
  }

  cargarPedidos(): void {
    this.http.get<Pedido[]>(`${environment.apiUrl}/pedidos`).subscribe({
      next: (data) => {
        this.pedidos = data;
        this.pedidosFiltrados = [...this.pedidos];
      },
      error: (error) => {
        console.error('Error al cargar pedidos:', error);
      }
    });
  }

  mostrarFormulario(): void {
    this.mostrarForm = true;
    this.pedidoEditando = null;
    this.pedido = {
      clienteId: 0,
      estado: '',
      productos: []
    };
    this.productosSeleccionados = [];
  }

  editarPedido(pedido: Pedido): void {
    this.pedidoEditando = pedido;
    this.pedido = {...pedido};
    this.productosSeleccionados = pedido.productos?.map(p => p.productoId) || [];
    this.mostrarForm = true;
  }

  toggleProducto(productoId: number, event: any): void {
    if (event.target.checked) {
      this.productosSeleccionados.push(productoId);
    } else {
      this.productosSeleccionados = this.productosSeleccionados.filter(id => id !== productoId);
    }
  }

  /*guardarPedido(): void {
    // Agregar productos seleccionados al pedido
    this.pedido.productos = this.productos.filter(p => this.productosSeleccionados.includes(p.productoId));

    if (this.pedidoEditando) {
      // Actualizar pedido existente
      this.http.put<Pedido>(`${environment.apiUrl}/pedidos/${this.pedidoEditando.id}`, this.pedido)
        .subscribe({
          next: (pedidoActualizado) => {
            const index = this.pedidos.findIndex(p => p.id === pedidoActualizado.id);
            if (index !== -1) {
              this.pedidos[index] = pedidoActualizado;
              this.filtrarPedidos();
            }
            this.cancelarEdicion();
          },
          error: (error) => {
            console.error('Error al actualizar pedido:', error);
            // Simular actualización local para desarrollo
            const index = this.pedidos.findIndex(p => p.id === this.pedidoEditando?.id);
            if (index !== -1) {
              this.pedidos[index] = {...this.pedido, id: this.pedidoEditando?.id};
              this.filtrarPedidos();
            }
            this.cancelarEdicion();
          }
        });
    } else {
      // Crear nuevo pedido
      debugger
      this.http.post<Pedido>(`${environment.apiUrl}/pedidos`, this.pedido)
        .subscribe({
          next: (nuevoPedido) => {
            this.pedidos.push(nuevoPedido);
            this.filtrarPedidos();
            this.cancelarEdicion();
          },
          error: (error) => {
            console.error('Error al crear pedido:', error);
            // Simular creación local para desarrollo
            const nuevoId = Math.max(...this.pedidos.map(p => p.id || 0)) + 1;
            const nuevoPedido = {...this.pedido, id: nuevoId};
            this.pedidos.push(nuevoPedido);
            this.filtrarPedidos();
            this.cancelarEdicion();
          }
        });
    }
  }*/

  guardarPedido(): void {
    // Mapear productos seleccionados con cantidad
    this.pedido.productos = this.productosSeleccionados.map(productoId => {
      const producto = this.productos.find(p => p.productoId === productoId);
      return {
        productoId: productoId,
        cantidad: producto ? Number(producto.cantidad) : 1
      };
    });

    if (this.pedidoEditando) {
      // lógica para actualizar pedido...
    } else {
      // Crear nuevo pedido
      this.http.post<Pedido>(`${environment.apiUrl}/pedidos`, this.pedido)
        .subscribe({
          next: (nuevoPedido) => {
            this.pedidos.push(nuevoPedido);
            this.filtrarPedidos();
            this.cancelarEdicion();
          },
          error: (error) => {
            console.error('Error al crear pedido:', error);
          }
        });
    }
  }


  eliminarPedido(id: number): void {
    if (confirm('¿Está seguro de que desea eliminar este pedido?')) {
      this.http.delete(`${environment.apiUrl}/pedidos/${id}`)
        .subscribe({
          next: () => {
            this.pedidos = this.pedidos.filter(p => p.id !== id);
            this.filtrarPedidos();
          },
          error: (error) => {
            console.error('Error al eliminar pedido:', error);
            // Simular eliminación local para desarrollo
            this.pedidos = this.pedidos.filter(p => p.id !== id);
            this.filtrarPedidos();
          }
        });
    }
  }

  cancelarEdicion(): void {
    this.mostrarForm = false;
    this.pedidoEditando = null;
    this.pedido = {
      clienteId: 0,
      estado: '',
      productos: []
    };
    this.productosSeleccionados = [];
  }

  filtrarPedidos(): void {
    if (!this.terminoBusqueda.trim()) {
      this.pedidosFiltrados = [...this.pedidos];
    } else {
      const termino = this.terminoBusqueda.toLowerCase();
      this.pedidosFiltrados = this.pedidos.filter(pedido => {
        const nombreCliente = this.obtenerNombreCliente(pedido.clienteId).toLowerCase();
        return nombreCliente.includes(termino) ||
          pedido.estado.toLowerCase().includes(termino);
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
