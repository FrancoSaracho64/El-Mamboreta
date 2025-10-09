# Sistema de Roles y Notificaciones - El Mamboreta

## Descripción General

Este sistema proporciona una gestión centralizada de roles de usuario y un sistema de notificaciones flotantes para mejorar la experiencia del usuario en la aplicación El Mamboreta.

## Componentes Principales

### 1. RoleService (`services/role.service.ts`)

Servicio centralizado para gestión de roles y permisos.

#### Características:
- Definición centralizada de opciones de menú con permisos
- Validación de acceso a rutas
- Verificación de permisos por rol
- Integración con AuthService

#### Uso:
```typescript
constructor(private roleService: RoleService) {}

// Verificar si puede acceder a una ruta
if (this.roleService.canAccessRoute('/productos')) {
  // Permitir acceso
}

// Verificar rol específico
if (this.roleService.isAdmin()) {
  // Funcionalidad solo para admin
}

// Obtener opciones de menú para el usuario actual
this.roleService.getMenuOptionsForCurrentUser().subscribe(options => {
  this.menuOptions = options;
});
```

### 2. NotificationService (`services/notification.service.ts`)

Sistema de notificaciones flotantes personalizado.

#### Tipos de Notificaciones:
- **Success**: Operaciones exitosas
- **Error**: Errores y fallos
- **Warning**: Advertencias
- **Info**: Información general

#### Uso:
```typescript
constructor(private notificationService: NotificationService) {}

// Notificación de éxito
this.notificationService.success('Título', 'Mensaje de éxito');

// Notificación de error
this.notificationService.error('Error', 'Descripción del error');

// Manejar errores del backend automáticamente
this.notificationService.handleBackendError(error);

// Manejar respuestas exitosas del backend
this.notificationService.handleBackendSuccess(response, 'Operación completada');
```

### 3. ValidationService (`services/validation.service.ts`)

Servicio de utilidades para validaciones comunes.

#### Uso:
```typescript
constructor(private validationService: ValidationService) {}

// Validar operación CRUD
if (this.validationService.canPerformOperation('create', 'productos')) {
  // Permitir creación
}

// Validar campos requeridos
if (this.validationService.validateRequiredFields(this.producto, ['nombre', 'precio'])) {
  // Todos los campos están completos
}

// Mostrar mensaje de éxito
this.validationService.showSuccessMessage('create', 'Producto', producto.nombre);
```

### 4. RoleGuard (`guards/role.guard.ts`)

Guard para proteger rutas basado en roles de usuario.

#### Configuración en rutas:
```typescript
{
  path: 'productos',
  canActivate: [AuthGuard, RoleGuard],
  loadComponent: () => import('./productos/productos.component').then(m => m.ProductosComponent)
}
```

### 5. ErrorInterceptor (`interceptors/error.interceptor.ts`)

Interceptor que maneja automáticamente errores HTTP y muestra notificaciones.

## Configuración de Roles

### Roles Disponibles:
- **ADMIN**: Acceso completo a todas las funcionalidades
- **EMPLEADO**: Acceso limitado a funcionalidades específicas

### Permisos por Funcionalidad:

| Funcionalidad | ADMIN | EMPLEADO |
|---------------|-------|----------|
| Dashboard     | ✅    | ✅       |
| Clientes      | ✅    | ❌       |
| Productos     | ✅    | ❌       |
| Stock         | ✅    | ✅       |
| Ventas        | ✅    | ❌       |
| Pedidos       | ✅    | ✅       |
| Materia Prima | ✅    | ❌       |

## Integración en Componentes

### Ejemplo de Componente con Validaciones:

```typescript
import { Component, OnInit } from '@angular/core';
import { RoleService } from '../services/role.service';
import { NotificationService } from '../services/notification.service';
import { ValidationService } from '../services/validation.service';

@Component({
  selector: 'app-ejemplo',
  templateUrl: './ejemplo.component.html'
})
export class EjemploComponent implements OnInit {

  constructor(
    private roleService: RoleService,
    private notificationService: NotificationService,
    private validationService: ValidationService
  ) {}

  crearElemento() {
    // Validar permisos
    if (!this.validationService.canPerformOperation('create', 'elementos')) {
      return;
    }

    // Validar datos
    if (!this.validationService.validateRequiredFields(this.elemento, ['nombre'])) {
      return;
    }

    // Realizar operación
    this.http.post('/api/elementos', this.elemento).subscribe({
      next: (response) => {
        this.validationService.showSuccessMessage('create', 'Elemento', response.nombre);
      },
      error: (error) => {
        // El ErrorInterceptor maneja automáticamente la notificación
      }
    });
  }
}
```

## Componente de Notificaciones

### Ubicación:
El componente `<app-notification>` debe estar incluido en `app.component.html` para mostrar las notificaciones en toda la aplicación.

### Estilos:
- Posicionadas en la esquina superior derecha
- Animaciones de entrada suaves
- Auto-cierre configurable
- Responsive para dispositivos móviles

## Mejores Prácticas

### 1. Validación de Permisos:
- Siempre validar permisos antes de mostrar formularios o ejecutar operaciones
- Usar el RoleService para verificaciones centralizadas
- Mostrar mensajes claros cuando se deniega el acceso

### 2. Notificaciones:
- Usar notificaciones de éxito para confirmar operaciones
- Permitir que el ErrorInterceptor maneje errores automáticamente
- Personalizar mensajes según el contexto

### 3. Consistencia:
- Mantener las mismas validaciones entre componentes HOME y SIDEBAR
- Usar el mismo sistema de permisos en toda la aplicación
- Seguir los patrones establecidos para nuevos componentes

## Configuración Adicional

### En app.config.ts:
```typescript
import { ErrorInterceptor } from './interceptors/error.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    // ... otros providers
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ErrorInterceptor,
      multi: true
    }
  ]
};
```

### En app.component.html:
```html
<!-- Contenido principal -->
<router-outlet></router-outlet>

<!-- Notificaciones flotantes -->
<app-notification></app-notification>
```

## Solución al Problema de Interceptores

**Importante**: Basado en la memoria del problema anterior con interceptores JWT, asegúrate de que los componentes **NO** importen `HttpClientModule` junto con `HttpClient`. Solo importa `HttpClient` directamente para evitar crear instancias separadas que no pasen por los interceptores.

### ❌ Incorrecto:
```typescript
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  imports: [HttpClientModule, ...] // NO hacer esto
})
```

### ✅ Correcto:
```typescript
import { HttpClient } from '@angular/common/http';

@Component({
  imports: [...] // Solo importar HttpClient, no HttpClientModule
})
```
