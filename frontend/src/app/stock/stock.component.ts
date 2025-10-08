import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {environment} from "../../environments/environment";

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
  clientes: any;
  mp: any;
  selectedProductoId: number | null = null;
  cantidad: number | null = null;
  mensaje: string = '';

  constructor(private http: HttpClient) {
  }

  ngOnInit() {
    this.http.get<Producto[]>(`${environment.apiUrl}/productos`).subscribe(productos => {
      this.productos = productos;
    });

    this.http.get<Producto[]>(`${environment.apiUrl}/clientes`).subscribe(data => {
      this.clientes = data;
    });

    this.http.get<Producto[]>(`${environment.apiUrl}/materias-primas`).subscribe(data => {
      this.mp = data;
    });
  }

  cargarStock() {
    if (this.selectedProductoId && this.cantidad && this.cantidad > 0) {
      this.http.put(`${environment.apiUrl}/productos/${this.selectedProductoId}/incrementar-stock?cantidad=${this.cantidad}`, {})
        .subscribe({
          next: (producto: any) => {
            this.mensaje = 'Stock actualizado correctamente';
            // Actualizar el stock localmente
            const prod = this.productos.find(p => p.id === this.selectedProductoId);
            if (prod && producto && producto.stock !== undefined) prod.stock = producto.stock;
            this.cantidad = null;
          },
          error: () => {
            this.mensaje = 'Error al actualizar el stock';
          }
        });
    } else {
      this.mensaje = 'Selecciona un producto y una cantidad válida';
    }
  }
}
