param(
    [Parameter(Mandatory)][ValidateSet('staging', 'production')][string]$Environment,
    [Parameter(Mandatory)][string]$Image,
    [string]$DeployRoot = $env:ECOACTIONS_DEPLOY_ROOT
)

$ErrorActionPreference = 'Stop'
if (-not $DeployRoot -or -not [IO.Path]::IsPathRooted($DeployRoot)) {
    throw 'Defina ECOACTIONS_DEPLOY_ROOT com o diretório absoluto dos ambientes no host.'
}
$sourceRoot = Split-Path $PSScriptRoot -Parent
$target = Join-Path $DeployRoot $Environment
$envFile = Join-Path $target '.env'
if (-not (Test-Path -LiteralPath $envFile)) {
    throw "Configure $envFile a partir de deploy/$Environment.env.example antes do deploy."
}

# O diretório de implantação fica fora do checkout descartável do runner.
Copy-Item (Join-Path $sourceRoot 'docker-compose.yml') $target -Force
Copy-Item (Join-Path $sourceRoot 'docker') $target -Recurse -Force
Copy-Item (Join-Path $sourceRoot 'scripts') $target -Recurse -Force
$composeFile = Join-Path $target 'docker-compose.yml'
$compose = @('compose', '--project-directory', $target, '--file', $composeFile, '--env-file', $envFile)

# A imagem recebida do pipeline tem prioridade sobre APP_IMAGE do arquivo .env.
$env:APP_IMAGE = $Image
if ($env:GITHUB_ACTIONS -eq 'true') {
    $config = (& docker @compose config --format json | ConvertFrom-Json)
    if ($LASTEXITCODE -ne 0) { throw 'Configuração Compose inválida.' }
    foreach ($service in $config.services.PSObject.Properties.Value) {
        foreach ($setting in $service.environment.PSObject.Properties) {
            if ($setting.Name -like '*PASSWORD*') {
                Write-Output "::add-mask::$($setting.Value)"
                Write-Output "::add-mask::$($setting.Value.Replace('$$', '$'))"
            }
        }
    }
}
try {
    & docker @compose up --detach --no-build --wait --wait-timeout 180
    if ($LASTEXITCODE -ne 0) { throw "Falha ao iniciar $Environment." }
    $smokeParams = @{ EnvFile = $envFile; ComposeFile = $composeFile }
    if ($Environment -eq 'staging') { $smokeParams.CheckCrud = $true }
    & (Join-Path $PSScriptRoot 'smoke-test.ps1') @smokeParams
    & docker @compose ps
    if ($LASTEXITCODE -ne 0) { throw 'Falha ao consultar os serviços.' }
    Set-Content -LiteralPath (Join-Path $target 'deployed-image.txt') -Value $Image
    Write-Output "Deploy de $Environment concluído: $Image. Serviços e volumes permanecem no host."
} catch {
    & docker @compose logs --no-color --tail 100
    throw
}
