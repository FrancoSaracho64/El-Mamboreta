import {Component, OnInit} from '@angular/core';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";

interface Cliente {
  id?: number;
  nombre: string;
  apellido?: string;
  email?: string;
  direccion?: string;
  observaciones?: string;
  activo: boolean;
  fechaRegistro?: string;
}

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule],
  templateUrl: './clientes.component.html',
  styleUrls: ['./clientes.component.css']
})
export class ClientesComponent implements OnInit {
  clientes: Cliente[] = [];
  clientesFiltrados: Cliente[] = [];
  cliente: Cliente = this.nuevoCliente();

  mostrarForm = false;
  clienteEditando: Cliente | null = null;
  terminoBusqueda = '';

  constructor(private http: HttpClient, private router: Router) {
  }

  ngOnInit(): void {
    this.cargarClientes();
  }

  private nuevoCliente(): Cliente {
    return {
      nombre: '',
      apellido: '',
      email: '',
      direccion: '',
      observaciones: '',
      activo: true
    };
  }

  cargarClientes(): void {
    this.http.get<Cliente[]>(`${environment.apiUrl}/clientes`).subscribe({
      next: (data) => {
        this.clientes = data;
        this.clientesFiltrados = [...this.clientes];
      },
      error: (error) => {
        console.error('Error al cargar clientes:', error);
      }
    });
  }

  mostrarFormulario(): void {
    this.mostrarForm = true;
    this.clienteEditando = null;
    this.cliente = this.nuevoCliente();
  }

  editarCliente(cliente: Cliente): void {
    this.clienteEditando = cliente;
    this.cliente = {...cliente};
    this.mostrarForm = true;
  }

  guardarCliente(): void {
    if (this.clienteEditando) {
      this.http.put<Cliente>(`${environment.apiUrl}/clientes/${this.clienteEditando.id}`, this.cliente)
        .subscribe({
          next: (clienteActualizado) => {
            const index = this.clientes.findIndex(c => c.id === clienteActualizado.id);
            if (index !== -1) this.clientes[index] = clienteActualizado;
            this.filtrarClientes();
            this.cancelarEdicion();
          },
          error: (error) => {
            console.error('Error al actualizar cliente:', error);
            const index = this.clientes.findIndex(c => c.id === this.clienteEditando?.id);
            if (index !== -1) this.clientes[index] = {...this.cliente, id: this.clienteEditando?.id};
            this.filtrarClientes();
            this.cancelarEdicion();
          }
        });
    } else {
      this.http.post<Cliente>(`${environment.apiUrl}/clientes`, this.cliente)
        .subscribe({
          next: (nuevoCliente) => {
            this.clientes.push(nuevoCliente);
            this.filtrarClientes();
            this.cancelarEdicion();
          },
          error: (error) => {
            console.error('Error al crear cliente:', error);
            const nuevoId = Math.max(...this.clientes.map(c => c.id || 0)) + 1;
            this.clientes.push({...this.cliente, id: nuevoId});
            this.filtrarClientes();
            this.cancelarEdicion();
          }
        });
    }
  }

  eliminarCliente(id: number): void {
    if (!id) return;
    if (confirm('¿Está seguro de que desea eliminar este cliente?')) {
      this.http.delete(`${environment.apiUrl}/clientes/${id}`).subscribe({
        next: () => {
          this.clientes = this.clientes.filter(c => c.id !== id);
          this.filtrarClientes();
        },
        error: (error) => {
          console.error('Error al eliminar cliente:', error);
          this.clientes = this.clientes.filter(c => c.id !== id);
          this.filtrarClientes();
        }
      });
    }
  }

  cancelarEdicion(): void {
    this.mostrarForm = false;
    this.clienteEditando = null;
    this.cliente = this.nuevoCliente();
  }

  filtrarClientes(): void {
    const termino = this.terminoBusqueda.trim().toLowerCase();
    this.clientesFiltrados = termino
      ? this.clientes.filter(c =>
        (c.nombre?.toLowerCase().includes(termino)) ||
        (c.apellido?.toLowerCase().includes(termino)) ||
        (c.email?.toLowerCase().includes(termino))
      )
      : [...this.clientes];
  }

  volverAlMenu(): void {
    this.router.navigate(['/home']);
  }
}
