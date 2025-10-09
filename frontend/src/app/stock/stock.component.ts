import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {environment} from "../../environments/environment";
import {NotificationService} from '../services/notification.service';
import {RoleService} from '../services/role.service';
import {ValidationService} from '../services/validation.service';

interface Producto {
  id: number;
  nombre: string;
  stock: number;
}

@Component({
  selector: 'app-stock',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './stock.component.html',
  styleUrls: ['./stock.component.css']
})
export class StockComponent implements OnInit {
  productos: Producto[] = [];
  selectedProductoId: number | null = null;
  cantidad: number | null = null;
  mensaje: string = '';
  loading = false;

  constructor(
    private http: HttpClient,
    private router: Router,
    private notificationService: NotificationService,
    private roleService: RoleService,
    private validationService: ValidationService
  ) {}

  ngOnInit() {
    this.http.get<Producto[]>(`${environment.apiUrl}/productos`).subscribe(productos => {
      this.productos = productos;
    });
  }

  cargarStock() {
    // Validar campos requeridos
    if (!this.selectedProductoId) {
      this.notificationService.warning('Campos Requeridos', 'Selecciona un producto');
      return;
    }

    if (!this.cantidad || this.cantidad <= 0) {
      this.notificationService.warning('Cantidad Inválida', 'Ingresa una cantidad mayor a 0');
      return;
    }

    const producto = this.productos.find(p => p.id === this.selectedProductoId);
    this.loading = true;

    this.http.put(`${environment.apiUrl}/productos/${this.selectedProductoId}/incrementar-stock?cantidad=${this.cantidad}`, {})
      .subscribe({
        next: (productoActualizado: any) => {
          this.loading = false;
          // Actualizar el stock localmente
          const prod = this.productos.find(p => p.id === this.selectedProductoId);
          if (prod && productoActualizado && productoActualizado.stock !== undefined) {
            prod.stock = productoActualizado.stock;
          }
          
          this.notificationService.success(
            'Stock Actualizado',
            `Se agregaron ${this.cantidad} unidades a "${producto?.nombre}". Stock actual: ${productoActualizado.stock || prod?.stock}`
          );
          
          this.cantidad = null;
          this.selectedProductoId = null;
        },
        error: (error) => {
          this.loading = false;
          console.error('Error al actualizar el stock:', error);
        }
      });
  }

  getStockStatus(stock: number): string {
    if (stock < 10) return 'low';
    if (stock < 50) return 'medium';
    return 'high';
  }

  volverAlMenu(): void {
    this.router.navigate(['/home']);
  }
}
