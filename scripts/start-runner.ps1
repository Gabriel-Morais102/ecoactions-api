param(
    [string]$RunnerDirectory = (Join-Path (Split-Path $PSScriptRoot -Parent) '.runner'),
    [string]$DeployRoot = (Join-Path (Split-Path $PSScriptRoot -Parent) '.deployment')
)

$ErrorActionPreference = 'Stop'
if (-not (Test-Path (Join-Path $RunnerDirectory '.runner'))) {
    throw 'Registre o runner neste diretório antes de iniciá-lo. Consulte deploy/README.md.'
}
$listenerPath = [IO.Path]::GetFullPath((Join-Path $RunnerDirectory 'bin/Runner.Listener.exe'))
$listener = Get-Process -Name 'Runner.Listener' -ErrorAction SilentlyContinue | Where-Object { $_.Path -eq $listenerPath }
if ($listener) {
    Write-Output 'O runner deste diretório já está em execução.'
    return
}
$env:ECOACTIONS_DEPLOY_ROOT = [IO.Path]::GetFullPath($DeployRoot)
$portablePowerShell = Join-Path $RunnerDirectory 'tools/pwsh'
if (Test-Path (Join-Path $portablePowerShell 'pwsh.exe')) {
    $env:PATH = "$portablePowerShell;$env:PATH"
}
if (-not (Get-Command pwsh -ErrorAction SilentlyContinue)) {
    throw 'PowerShell 7 precisa estar instalado no host do runner.'
}
$process = Start-Process -FilePath (Join-Path $RunnerDirectory 'run.cmd') -WorkingDirectory $RunnerDirectory -WindowStyle Hidden -PassThru
Write-Output "Runner iniciado (processo $($process.Id)); deploys em $env:ECOACTIONS_DEPLOY_ROOT."
