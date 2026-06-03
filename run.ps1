# Chay simulation tu thu muc goc project (khong chay file Main.java rieng le)
Set-Location $PSScriptRoot

$jdkBin = "C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot\bin"
if (Test-Path $jdkBin) {
    $env:Path = "$jdkBin;" + $env:Path
}

$files = Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
Write-Host "Compiling $($files.Count) files..."
javac -encoding UTF-8 -d bin -cp bin $files
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "Starting simulation..."
java -cp bin ecosystem.Main
