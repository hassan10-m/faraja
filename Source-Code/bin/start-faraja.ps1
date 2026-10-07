$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) { throw 'Install Maven and reopen PowerShell first.' }
if (-not $env:DB_USER) { $env:DB_USER = Read-Host 'MySQL username (for example faraja_app)' }
$secret = Read-Host 'MySQL password (hidden)' -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $secret).Password
if (-not $env:ADMIN_USER) { $env:ADMIN_USER = 'admin' }
Write-Host 'For an existing database, press Enter at the next prompt. For first setup, choose 12+ characters.'
$adminSecret = Read-Host 'Initial administrator password (hidden)' -AsSecureString
$env:ADMIN_PASSWORD = [System.Net.NetworkCredential]::new('', $adminSecret).Password
try { & mvn spring-boot:run 2>&1 | Tee-Object -FilePath faraja-error.log }
finally { Remove-Item Env:DB_PASSWORD -ErrorAction SilentlyContinue; Remove-Item Env:ADMIN_PASSWORD -ErrorAction SilentlyContinue }
