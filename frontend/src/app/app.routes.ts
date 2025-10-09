import {Routes} from '@angular/router';
import {AuthGuard} from './auth.guard';
import {RoleGuard} from './guards/role.guard';

export const routes: Routes = [
  {path: 'login', loadComponent: () => import('./login/login.component').then(m => m.LoginComponent)},
  {
    path: 'home',
    canActivate: [AuthGuard, RoleGuard],
    loadComponent: () => import('./home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'productos',
    canActivate: [AuthGuard, RoleGuard],
    loadComponent: () => import('./productos/productos.component').then(m => m.ProductosComponent)
  },
  {
    path: 'stock',
    canActivate: [AuthGuard, RoleGuard],
    loadComponent: () => import('./stock/stock.component').then(m => m.StockComponent)
  },
  {
    path: 'clientes',
    canActivate: [AuthGuard, RoleGuard],
    loadComponent: () => import('./clientes/clientes.component').then(m => m.ClientesComponent)
  },
  {
    path: 'pedidos',
    canActivate: [AuthGuard, RoleGuard],
    loadComponent: () => import('./pedidos/pedidos.component').then(m => m.PedidosComponent)
  },
  {
    path: 'materia-prima',
    canActivate: [AuthGuard, RoleGuard],
    loadComponent: () => import('./materia-prima/materia-prima.component').then(m => m.MateriaPrimaComponent)
  },
  {
    path: 'ventas',
    canActivate: [AuthGuard, RoleGuard],
    loadComponent: () => import('./ventas/ventas.component').then(m => m.VentasComponent)
  },
  {path: '', redirectTo: '/login', pathMatch: 'full'},
  {path: '**', redirectTo: '/login'}
];
