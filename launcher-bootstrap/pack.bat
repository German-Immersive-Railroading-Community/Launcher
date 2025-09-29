@echo off
setlocal enabledelayedexpansion

if "%~1"=="" (
    echo Version number is required.
    echo Usage: build.bat [version] [extra_args...]
    exit /b 1
)

cd %~dp0

echo.
echo Building Rust
cargo build -r
if errorlevel 1 exit /b 1

echo.
echo Building Velopack Release v%~1

move target\release\launcher-bootstrap.exe publish\launcher-bootstrap.exe
if errorlevel 1 exit /b 1

vpk pack -u launcher-bootstrap -o releases -p publish -v %*
if errorlevel 1 exit /b 1