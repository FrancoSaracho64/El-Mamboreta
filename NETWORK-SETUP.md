# Configuración de Red para El Mamboreta

## Problema
La aplicación funciona en `localhost` pero no es accesible desde otras computadoras en la red.

## Solución Automática

### Opción 1: Script Automático (Recomendado)
```bash
# Desde el directorio frontend
npm run start:network
```

Este comando:
1. Detecta automáticamente tu IP de red
2. Configura el proxy.conf.json
3. Inicia Angular con acceso externo
4. Muestra las URLs de acceso

### Opción 2: Solo Configurar (sin iniciar)
```bash
# Desde el directorio frontend
npm run setup
```

Luego iniciar manualmente:
```bash
ng serve --host 0.0.0.0 --proxy-config proxy.conf.json
```

## Configuración Manual (si los scripts fallan)

### Frontend
1. **Environment dinámico**: Ya configurado para usar `window.location.hostname`
2. **Angular serve**: 
   ```bash
   ng serve --host 0.0.0.0 --proxy-config proxy.conf.json
   ```

### Backend
Asegúrate de que `application.properties` tenga:
```properties
server.address=0.0.0.0
server.port=8090
```

## URLs de Acceso

- **Local**: `http://localhost:4200`
- **Red**: `http://[TU-IP]:4200`
- **Backend directo**: `http://[TU-IP]:8090/api`

## Notas Importantes

1. **Firewall**: Asegúrate de que Windows Firewall permita conexiones en puertos 4200 y 8090
2. **Red**: Ambas computadoras deben estar en la misma red
3. **IP dinámica**: El script detecta automáticamente cambios de IP
4. **Seguridad**: Esta configuración permite acceso desde cualquier dispositivo en tu red local

## Troubleshooting

Si no funciona:
1. Verificar que el backend esté corriendo
2. Verificar firewall de Windows
3. Verificar que ambas máquinas estén en la misma red
4. Ejecutar `ipconfig` para verificar la IP actual
