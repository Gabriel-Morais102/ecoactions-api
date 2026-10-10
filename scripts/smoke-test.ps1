param(
    [Parameter(Mandatory)][string]$EnvFile,
    [string]$ComposeFile = (Join-Path (Split-Path $PSScriptRoot -Parent) 'docker-compose.yml'),
    [switch]$CheckCrud
)

$ErrorActionPreference = 'Stop'
$containerId = & docker compose --file $ComposeFile --env-file $EnvFile ps --quiet ecoactions-api
if ($LASTEXITCODE -ne 0 -or -not $containerId) { throw 'Contêiner da API não encontrado.' }
$container = (& docker inspect $containerId | ConvertFrom-Json)[0]
if ($LASTEXITCODE -ne 0) { throw 'Não foi possível consultar o contêiner da API.' }
# Usa a configuração efetiva do contêiner, sem reinterpretar aspas e caracteres do .env.
$settings = @{}
foreach ($entry in $container.Config.Env) {
    $parts = $entry -split '=', 2
    $settings[$parts[0]] = $parts[1]
}
foreach ($name in @('SERVER_PORT', 'APP_USER_USERNAME', 'APP_USER_PASSWORD')) {
    if (-not $settings[$name]) { throw "Variável obrigatória ausente: $name" }
}

$port = $container.NetworkSettings.Ports."$($settings.SERVER_PORT)/tcp"[0].HostPort
if (-not $port) { throw 'Porta da API não publicada no host.' }
$baseUrl = "http://127.0.0.1:$port/api"
$readerToken = [Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes(
    "$($settings.APP_USER_USERNAME):$($settings.APP_USER_PASSWORD)"))
if ($env:GITHUB_ACTIONS -eq 'true') {
    Write-Output "::add-mask::$readerToken"
    Write-Output "::add-mask::$($settings.APP_USER_PASSWORD)"
}
$readerHeaders = @{ Authorization = "Basic $readerToken" }
$ready = $false
for ($attempt = 1; $attempt -le 30; $attempt++) {
    try {
        $health = Invoke-RestMethod "$baseUrl/health" -TimeoutSec 5
        if ($health.status -eq 'UP') { $ready = $true; break }
    } catch { }
    Start-Sleep -Seconds 2
}
if (-not $ready) { throw 'A API não ficou pronta com conexão ao MongoDB.' }

Write-Output "GET $baseUrl/health -> 200"
$health | ConvertTo-Json -Compress | Write-Output
foreach ($resource in @('residuos', 'pontos-coleta', 'coletas', 'destinacoes', 'alertas')) {
    $items = Invoke-RestMethod "$baseUrl/$resource" -Headers $readerHeaders -TimeoutSec 10
    Write-Output "GET $baseUrl/$resource -> 200 ($(@($items).Count) registros)"
}

if ($CheckCrud) {
    foreach ($name in @('APP_ADMIN_USERNAME', 'APP_ADMIN_PASSWORD')) {
        if (-not $settings[$name]) { throw "Variável obrigatória ausente: $name" }
    }
    $adminToken = [Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes(
        "$($settings.APP_ADMIN_USERNAME):$($settings.APP_ADMIN_PASSWORD)"))
    if ($env:GITHUB_ACTIONS -eq 'true') {
        Write-Output "::add-mask::$adminToken"
        Write-Output "::add-mask::$($settings.APP_ADMIN_PASSWORD)"
    }
    $adminHeaders = @{ Authorization = "Basic $adminToken" }
    $recordId = $null
    try {
        $body = @{ tipoResiduo = "Smoke-$([Guid]::NewGuid().ToString('N'))"; limiteMaximo = 100 } | ConvertTo-Json
        $created = Invoke-WebRequest "$baseUrl/residuos" -Method Post -Headers $adminHeaders -ContentType 'application/json' -Body $body -TimeoutSec 10
        if ($created.StatusCode -ne 201) { throw 'Criação não retornou 201.' }
        $recordId = ($created.Content | ConvertFrom-Json).id
        $record = Invoke-RestMethod "$baseUrl/residuos/$recordId" -Headers $readerHeaders -TimeoutSec 10
        if ($record.id -ne $recordId) { throw 'O registro criado não foi recuperado.' }
        Write-Output 'Staging: criação 201 e consulta 200 confirmadas no MongoDB.'
    } finally {
        if ($recordId) {
            $deleted = Invoke-WebRequest "$baseUrl/residuos/$recordId" -Method Delete -Headers $adminHeaders -TimeoutSec 10
            if ($deleted.StatusCode -ne 204) { throw 'Limpeza do registro de teste não retornou 204.' }
            Write-Output 'Staging: exclusão 204 do registro de teste confirmada.'
        }
    }
}
Write-Output 'Smoke test aprovado.'
