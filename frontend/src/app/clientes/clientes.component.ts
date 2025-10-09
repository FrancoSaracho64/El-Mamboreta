import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {environment} from '../../environments/environment';
import {CommonModule} from "@angular/common";
import {FormsModule} from "@angular/forms";
import {NotificationService} from '../services/notification.service';
import {RoleService} from '../services/role.service';
import {ValidationService} from '../services/validation.service';

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
  imports: [CommonModule, FormsModule],
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

  constructor(
    private http: HttpClient, 
    private router: Router,
    private notificationService: NotificationService,
    public roleService: RoleService,
    private validationService: ValidationService
  ) {}

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
    if (!this.validationService.canPerformOperation('create', 'clientes')) {
      return;
    }
    
    this.mostrarForm = true;
    this.clienteEditando = null;
    this.cliente = this.nuevoCliente();
  }

  editarCliente(cliente: Cliente): void {
    if (!this.validationService.canPerformOperation('update', 'clientes')) {
      return;
    }
    
    this.clienteEditando = cliente;
    this.cliente = {...cliente};
    this.mostrarForm = true;
  }

  guardarCliente(): void {
    // Validar campos requeridos
    if (!this.validationService.validateRequiredFields(this.cliente, ['nombre'])) {
      return;
    }

    // Validar email si se proporciona
    if (this.cliente.email && !this.validationService.validateEmail(this.cliente.email)) {
      return;
    }

    if (this.clienteEditando) {
      // Actualizar cliente existente
      this.http.put<Cliente>(`${environment.apiUrl}/clientes/${this.clienteEditando.id}`, this.cliente)
        .subscribe({
          next: (clienteActualizado) => {
            const index = this.clientes.findIndex(c => c.id === clienteActualizado.id);
            if (index !== -1) this.clientes[index] = clienteActualizado;
            this.filtrarClientes();
            this.validationService.showSuccessMessage('update', 'Cliente', clienteActualizado.nombre);
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
      // Crear nuevo cliente
      this.http.post<Cliente>(`${environment.apiUrl}/clientes`, this.cliente)
        .subscribe({
          next: (nuevoCliente) => {
            this.clientes.push(nuevoCliente);
            this.filtrarClientes();
            this.validationService.showSuccessMessage('create', 'Cliente', nuevoCliente.nombre);
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
    
    if (!this.validationService.canPerformOperation('delete', 'clientes')) {
      return;
    }

    const cliente = this.clientes.find(c => c.id === id);
    if (confirm(`¿Está seguro de que desea eliminar el cliente "${cliente?.nombre} ${cliente?.apellido || ''}"?`)) {
      this.http.delete(`${environment.apiUrl}/clientes/${id}`).subscribe({
        next: () => {
          this.clientes = this.clientes.filter(c => c.id !== id);
          this.filtrarClientes();
          this.validationService.showSuccessMessage('delete', 'Cliente', cliente?.nombre);
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
