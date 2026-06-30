param(
    [string]$AdminUser = "postgres",
    [string]$AdminPassword
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
if (-not (Test-Path "$root\db-init")) { $root = $PSScriptRoot }

$env:DB_USER = "ayurveda"
if (-not $env:DB_PASSWORD) {
    Write-Host "Set DB_PASSWORD to the ayurveda database password, then re-run."
    exit 1
}

if ($AdminPassword) {
    $env:DB_ADMIN_USER = $AdminUser
    $env:DB_ADMIN_PASSWORD = $AdminPassword
}

$jar = "$env:USERPROFILE\.m2\repository\org\postgresql\postgresql\42.7.7\postgresql-42.7.7.jar"
if (-not (Test-Path $jar)) {
    Write-Host "PostgreSQL JDBC jar not found. Run: .\mvnw.cmd -pl patient-service dependency:resolve"
    exit 1
}

Push-Location "$root\db-check"
javac DbInit.java
Pop-Location

Push-Location $root
java -cp "$root\db-check;$jar" DbInit
Pop-Location
