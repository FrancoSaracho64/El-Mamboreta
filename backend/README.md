# Módulo Backend - El Mamboreta

Este módulo contiene toda la lógica de negocio y gestión de datos para el sistema El Mamboreta.

## Estructura del Proyecto

```
backend/
├── src/main/java/com/mamboreta/backend/
│   ├── entity/           # Entidades JPA
│   ├── repository/       # Repositorios de datos
│   ├── service/          # Servicios de negocio
│   └── database/         # Configuración de base de datos
```

## Entidades

### Cliente
- **Campos principales**: id, nombre, apellido, email, direccion, activo, fechaRegistro, observaciones
- **Relaciones**: 
  - OneToOne con Documento
  - OneToMany con Telefono
  - OneToMany con RedSocial

### Producto
- **Campos principales**: id, nombre, precio, descripcion, activo, stock
- **Relaciones**: ManyToMany con MateriaPrima

### MateriaPrima
- **Campos principales**: id, nombre, descripcion, precio, stock, activo, unidadMedida

### Pedido
- **Campos principales**: id, estado, fechaSolicitado
- **Relaciones**: 
  - ManyToMany con Producto
  - ManyToOne con Cliente

### Venta
- **Campos principales**: id, precioVenta, fechaEntrega, observaciones, fecha
- **Relaciones**: OneToOne con Pedido

### Entidades de Soporte
- **Documento**: tipo, numero
- **Telefono**: numero, tipo, activo
- **RedSocial**: red, usuario, url, activo

## Servicios Implementados

### ClienteService
- Gestión completa de clientes (CRUD)
- Validación de email y documento únicos
- Búsquedas por nombre, email, documento
- Borrado lógico (desactivación)

### ProductoService
- Gestión completa de productos (CRUD)
- Control de stock (incrementar/decrementar)
- Búsquedas por nombre, precio, stock
- Validación de precios y stock

### MateriaPrimaService
- Gestión completa de materias primas (CRUD)
- Control de stock
- Búsquedas por nombre, precio, unidad de medida

### PedidoService
- Gestión completa de pedidos (CRUD)
- Control de estados (PENDIENTE → EN_PROCESO → COMPLETADO/CANCELADO)
- Validación de stock al crear pedidos
- Cálculo de totales

### VentaService
- Gestión completa de ventas (CRUD)
- Registro de entregas
- Estadísticas de ventas
- Validación de pedidos completados

### Servicios de Soporte
- **DocumentoService**: Gestión de documentos
- **TelefonoService**: Gestión de teléfonos con validación de formato
- **RedSocialService**: Gestión de redes sociales con validación de URLs

## Características Implementadas

### Validaciones
- Validación de datos con anotaciones JPA y Bean Validation
- Validación de formato de teléfonos
- Validación de URLs de redes sociales
- Validación de precios y stock
- Validación de transiciones de estado en pedidos

### Lógica de Negocio
- Control de stock automático
- Estados de pedidos con transiciones válidas
- Borrado lógico en lugar de físico
- Validación de unicidad de datos críticos
- Cálculo automático de totales y estadísticas

### Repositorios
- Métodos de búsqueda personalizados
- Consultas optimizadas con @Query
- Filtros por estado activo
- Búsquedas por rangos de fechas y precios

## Dependencias

- **Spring Boot Starter Data JPA**: Persistencia de datos
- **Spring Boot Starter Web**: Servicios web
- **Spring Boot Starter Validation**: Validación de datos
- **Lombok**: Reducción de código boilerplate
- **H2 Database**: Base de datos en memoria para desarrollo
- **Hibernate Community Dialects**: Dialectos de base de datos

## Configuración

El módulo está configurado para usar:
- Base de datos H2 en memoria para desarrollo
- Validación automática de entidades
- Transacciones automáticas en servicios
- Configuración de dialectos de Hibernate

## Uso

Los servicios están listos para ser inyectados en controladores o en otros servicios. Todos los métodos incluyen:

- Validación de datos de entrada
- Manejo de errores con excepciones personalizadas
- Transacciones automáticas
- Logging implícito a través de Spring

## Próximos Pasos

1. Implementar controladores REST
2. Agregar autenticación y autorización
3. Implementar logging detallado
4. Agregar tests unitarios y de integración
5. Configurar base de datos de producción 