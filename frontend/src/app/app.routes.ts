import {Routes} from '@angular/router';
import {HomeComponent} from './home/home.component';
import {ProductosComponent} from './productos/productos.component';
import {ClientesComponent} from './clientes/clientes.component';
import {PedidosComponent} from './pedidos/pedidos.component';
import {MateriaPrimaComponent} from './materia-prima/materia-prima.component';
import {VentasComponent} from './ventas/ventas.component';

export const routes: Routes = [
  {path: 'home', component: HomeComponent},
  {path: 'productos', component: ProductosComponent},
  {path: 'clientes', component: ClientesComponent},
  {path: 'pedidos', component: PedidosComponent},
  {path: 'materia-prima', component: MateriaPrimaComponent},
  {path: 'ventas', component: VentasComponent},
  {path: '', redirectTo: '/home', pathMatch: 'full'}
];
