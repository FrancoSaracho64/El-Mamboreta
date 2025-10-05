import {Routes} from '@angular/router';
import {HomeComponent} from './home/home.component';
import {ProductosComponent} from './productos/productos.component';
import {ClientesComponent} from './clientes/clientes.component';
import {PedidosComponent} from './pedidos/pedidos.component';
import {MateriaPrimaComponent} from './materia-prima/materia-prima.component';
import {VentasComponent} from './ventas/ventas.component';
import { LoginComponent } from './login/login.component';
import { AuthGuard } from './auth.guard';
import { RoleGuard } from './role.guard';

export const routes: Routes = [
  {path: 'home', component: HomeComponent},
  {path: 'productos', component: ProductosComponent},
  {path: 'clientes', component: ClientesComponent},
  {path: 'pedidos', component: PedidosComponent},
  {path: 'materia-prima', component: MateriaPrimaComponent},
  {path: 'ventas', component: VentasComponent},
  { path: 'login', component: LoginComponent },
  { path: 'admin', canActivate: [AuthGuard, RoleGuard], data: { role: 'ADMIN' }, loadComponent: () => import('./admin/admin.component').then(m => m.AdminComponent) },
  { path: 'empleado', canActivate: [AuthGuard, RoleGuard], data: { role: 'EMPLEADO' }, loadComponent: () => import('./empleado/empleado.component').then(m => m.EmpleadoComponent) },
  {path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' }
];
