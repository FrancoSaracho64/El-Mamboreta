import {Routes} from '@angular/router';
import {AuthGuard} from './auth.guard';
import {RoleGuard} from './role.guard';

export const routes: Routes = [
  {path: 'login', loadComponent: () => import('./login/login.component').then(m => m.LoginComponent)},
  {path: 'home', canActivate: [AuthGuard, RoleGuard], loadComponent: () => import('./home/home.component').then(m => m.HomeComponent)},
  {path: 'productos', canActivate: [AuthGuard, RoleGuard], data: {role: 'EMPLEADO'}, loadComponent: () => import('./productos/productos.component').then(m => m.ProductosComponent)},
  {path: 'stock', canActivate: [AuthGuard, RoleGuard], data: {role: 'EMPLEADO'}, loadComponent: () => import('./stock/stock.component').then(m => m.StockComponent)},
  {path: 'clientes', canActivate: [AuthGuard, RoleGuard], data: {role: 'EMPLEADO'}, loadComponent: () => import('./clientes/clientes.component').then(m => m.ClientesComponent)},
  {path: 'pedidos', canActivate: [AuthGuard, RoleGuard], data: {role: 'EMPLEADO'}, loadComponent: () => import('./pedidos/pedidos.component').then(m => m.PedidosComponent)},
  {path: 'materia-prima', canActivate: [AuthGuard, RoleGuard], data: {role: 'EMPLEADO'}, loadComponent: () => import('./materia-prima/materia-prima.component').then(m => m.MateriaPrimaComponent)},
  {path: 'ventas', canActivate: [AuthGuard, RoleGuard], data: {role: 'EMPLEADO'}, loadComponent: () => import('./ventas/ventas.component').then(m => m.VentasComponent)},
  {path: 'admin', canActivate: [AuthGuard, RoleGuard], loadComponent: () => import('./admin/admin.component').then(m => m.AdminComponent)},
  {path: 'empleado', canActivate: [AuthGuard, RoleGuard], data: {role: 'EMPLEADO'}, loadComponent: () => import('./empleado/empleado.component').then(m => m.EmpleadoComponent)},
  {path: '', redirectTo: '/login', pathMatch: 'full'},
  {path: '**', redirectTo: '/login'}
];
