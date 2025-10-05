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
  id: number;       // <-- Cambiado de productoId a id
  nombre: string;
  precio: number;
  cantidad?: number;
}

interface Pedido {
  id?: number;
  clienteId: number;
  estado: string;
  productos?: Producto[];
  fechaSolicitado?: string;
}

interface PedidoDTO {
  clienteId: number;
  estado: string;
  productos: { productoId: number; cantidad: number }[];
}

@Component({
  selector: 'app-pedidos',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule],
  templateUrl: './pedidos.component.html',
  styleUrls: ['./pedidos.component.css']
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
    // Cargar clientes primero
    this.http.get<Cliente[]>(`${environment.apiUrl}/clientes`).subscribe({
      next: clientesData => {
        this.clientes = clientesData;

        // Ahora sí cargar productos
        this.http.get<Producto[]>(`${environment.apiUrl}/productos`).subscribe({
          next: productosData => this.productos = productosData,
          error: error => console.error('Error al cargar productos:', error)
        });

        // Y ahora cargar pedidos
        this.cargarPedidos();
      },
      error: error => console.error('Error al cargar clientes:', error)
    });
  }


  cargarPedidos(): void {
    this.http.get<Pedido[]>(`${environment.apiUrl}/pedidos`).subscribe({
      next: data => {
        this.pedidos = data;
        this.pedidosFiltrados = [...this.pedidos];
      },
      error: error => console.error('Error al cargar pedidos:', error)
    });
  }

  mostrarFormulario(): void {
    this.mostrarForm = true;
    this.pedidoEditando = null;
    this.pedido = {clienteId: 0, estado: '', productos: []};
    this.productosSeleccionados = [];
    this.productos.forEach(p => p.cantidad = undefined);
  }

  editarPedido(pedido: Pedido): void {
    this.pedidoEditando = pedido;
    this.pedido = {...pedido};
    this.productosSeleccionados = pedido.productos?.map(p => p.id) || [];
    this.productos.forEach(p => {
      if (this.productosSeleccionados.includes(p.id)) {
        const prod = pedido.productos?.find(pp => pp.id === p.id);
        p.cantidad = prod?.cantidad;
      } else {
        p.cantidad = undefined;
      }
    });
    this.mostrarForm = true;
  }

  toggleProducto(productoId: number, event: any): void {
    if (event.target.checked) {
      this.productosSeleccionados.push(productoId);
    } else {
      this.productosSeleccionados = this.productosSeleccionados.filter(id => id !== productoId);
      const producto = this.productos.find(p => p.id === productoId);
      if (producto) producto.cantidad = undefined;
    }
  }

  guardarPedido(): void {
    const pedidoDTO: PedidoDTO = {
      clienteId: this.pedido.clienteId,
      estado: this.pedido.estado,
      productos: this.productos
        .filter(p => this.productosSeleccionados.includes(p.id) && p.cantidad && p.cantidad > 0)
        .map(p => ({productoId: p.id, cantidad: p.cantidad!}))
    };

    if (this.pedidoEditando) {
      this.http.put<Pedido>(`${environment.apiUrl}/pedidos/${this.pedidoEditando.id}`, pedidoDTO)
        .subscribe({
          next: pedidoActualizado => {
            const index = this.pedidos.findIndex(p => p.id === pedidoActualizado.id);
            if (index !== -1) this.pedidos[index] = pedidoActualizado;
            this.filtrarPedidos();
            this.cancelarEdicion();
          },
          error: error => console.error('Error al actualizar pedido:', error)
        });
    } else {
      this.http.post<Pedido>(`${environment.apiUrl}/pedidos`, pedidoDTO)
        .subscribe({
          next: nuevoPedido => {
            this.pedidos.push(nuevoPedido);
            this.filtrarPedidos();
            this.cancelarEdicion();
          },
          error: error => console.error('Error al crear pedido:', error)
        });
    }
  }

  eliminarPedido(id?: number): void {
    if (!id) return;
    if (confirm('¿Está seguro de que desea eliminar este pedido?')) {
      this.http.delete(`${environment.apiUrl}/pedidos/${id}`)
        .subscribe({
          next: () => {
            this.pedidos = this.pedidos.filter(p => p.id !== id);
            this.filtrarPedidos();
          },
          error: error => console.error('Error al eliminar pedido:', error)
        });
    }
  }

  cancelarEdicion(): void {
    this.mostrarForm = false;
    this.pedidoEditando = null;
    this.pedido = {clienteId: 0, estado: '', productos: []};
    this.productosSeleccionados = [];
    this.productos.forEach(p => p.cantidad = undefined);
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
