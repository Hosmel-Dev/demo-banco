# Demo Banco

Proyecto de práctica con dos servicios Spring Boot:

- `account-service`: gestión de cuentas y saldo.
- `transaction-service`: registro de débitos, créditos y transferencias.

## Requisitos

- Docker Desktop con Docker Compose.
- Java 21 y Maven, solo si se ejecutarán los servicios fuera de Docker.

## Configuración local


1. Crea tu archivo de configuración local:

   ```powershell
   Copy-Item .env.example .env
   
2. Edita .env y define tus contraseñas locales.
El archivo .env no se versiona. Usa .env.example como plantilla.