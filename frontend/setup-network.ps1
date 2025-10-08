# Script para configurar automáticamente la red para acceso externo
Write-Host "Configurando red para acceso externo..." -ForegroundColor Green

# Obtener la IP principal (excluyendo localhost y IPs virtuales)
$networkAdapters = Get-NetAdapter | Where-Object {$_.Status -eq "Up" -and $_.Name -like "*Wi-Fi*" -or $_.Name -like "*Ethernet*"}
$ipAddress = $null

foreach ($adapter in $networkAdapters) {
    $ip = Get-NetIPAddress -InterfaceIndex $adapter.InterfaceIndex -AddressFamily IPv4 | Where-Object {$_.IPAddress -notlike "127.*" -and $_.IPAddress -notlike "169.254.*"}
    if ($ip -and $ip.IPAddress -match "^192\.168\.|^10\.|^172\.(1[6-9]|2[0-9]|3[0-1])\.") {
        $ipAddress = $ip.IPAddress
        Write-Host "IP detectada: $ipAddress en adaptador: $($adapter.Name)" -ForegroundColor Yellow
        break
    }
}

if (-not $ipAddress) {
    Write-Host "No se pudo detectar una IP válida. Usando localhost como fallback." -ForegroundColor Red
    $ipAddress = "localhost"
}

# Actualizar proxy.conf.json
$proxyConfig = @{
    "/api" = @{
        "target" = "http://$ipAddress`:8090"
        "secure" = $false
        "changeOrigin" = $true
        "logLevel" = "debug"
    }
}

$proxyConfigJson = $proxyConfig | ConvertTo-Json -Depth 3
$proxyConfigJson | Out-File -FilePath "proxy.conf.json" -Encoding UTF8

Write-Host "Proxy configurado para: http://$ipAddress`:8090" -ForegroundColor Green

# Mostrar información de acceso
Write-Host "`n=== INFORMACIÓN DE ACCESO ===" -ForegroundColor Cyan
Write-Host "Frontend (desde esta máquina): http://localhost:4200" -ForegroundColor White
Write-Host "Frontend (desde otra máquina): http://$ipAddress`:4200" -ForegroundColor White
Write-Host "Backend (desde otra máquina): http://$ipAddress`:8090" -ForegroundColor White
Write-Host "`nPara iniciar el servidor: ng serve" -ForegroundColor Yellow
Write-Host "El backend debe estar configurado con server.address=0.0.0.0" -ForegroundColor Yellow
