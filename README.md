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
   ```
   
2. Edita .env y define tus contraseñas locales.
El archivo .env no se versiona. Usa .env.example como plantilla.

## Contratos HTTP internos

- Identificador de cuenta: `Long id`.
- Saldo: `{ "amount": decimal, "currency": "PEN" | "USD" }`.
- Estado de cuenta: `ACTIVE`, `INACTIVE`, `SUSPENDED` o `CLOSED`.
- En `account-service`, el monto de crédito o débito debe estar entre
  `0.01` y `100000.00`.
- Las operaciones de crédito y débito requieren que la moneda enviada
  coincida con la moneda de la cuenta.

## `account-service` consumido por `transaction-service`

- Local: `http://localhost:8100/v1`
- Entre contenedores: `http://account-service:8100/v1`

### Buscar cuenta por número o id

- `GET /accounts/number/{accountNumber}`
- `GET /accounts/{id}`

Respuesta exitosa `200`:

```json
{
  "id": 2,
  "accountNumber": "1234567890",
  "accountType": "SAVINGS",
  "balance": {
    "amount": 150.00,
    "currency": "PEN"
  },
  "status": "ACTIVE"
}
```

### Acreditar y debitar una cuenta

- La cuenta debe estar en estado `ACTIVE`.
- La moneda enviada debe coincidir con la moneda de la cuenta.
- El débito invoca una validación de fondos antes de actualizar el saldo.
  La corrección de su comportamiento ante saldo insuficiente está prevista para
  el Día 8 del sprint.

- `POST /accounts/debit`
- `POST /accounts/credit`

Request:

```json
{
  "id": 2,
  "amount": 100.00,
  "currency": "PEN"
}
```

Respuesta exitosa `200`:

```json
{
  "id": 2,
  "accountNumber": "123456789013",
  "balance": {
    "amount": 150.00,
    "currency": "PEN"
  },
  "createdAt": "2026-09-10T02:05:16.355509"
}
```

#### Errores actuales de `account-service`

| Caso | Estado HTTP | `type` de `ProblemDetail` |
|---|---:|---|
| Cuenta inexistente | 404 | `problems/account-not-found` |
| Cuenta no activa | 423 | `problems/account-not-active` |
| Divisa distinta | 422 | `/problems/different-currency` |
| Saldo insuficiente | 400 | `/problems/resource-not-enough-funds` |

## transaction-service servicio principal

- Local: `http://localhost:8101/v1`
- Entre contenedores: `http://transaction-service:8101/v1`

Todos los endpoints siguientes requieren el header `Idempotency-Key` de 36 caracteres:

```http
Idempotency-Key: <clave-del-cliente>
```

La clave se registra con la transacción, pero la prevención efectiva de
duplicados todavía está pendiente para el Día 7.

### Depositar a una cuenta

`POST /transactions/credit`

```json
{
  "accountId": 1,
  "amount": 3000,
  "currency": "PEN",
  "description": "Pago de servicios profesionales"
}
```

Respuesta exitosa `200`:

```json
{
    "transactionId": 25,
    "idempotencyKey": "123e4567-e89b-12d3-a456-426614174000",
    "type": "DEPOSIT",
    "amount": 3000,
    "currency": "PEN",
    "status": "SUCCESS",
    "accountId": 1,
    "createdAt": "2026-09-14T18:05:27.2731594"
}
```

### Retirar de una cuenta

`POST /transactions/debit`

```json
{
  "accountId": 1,
  "amount": 1000,
  "currency": "PEN",
  "description": "Pago de servicios profesionales"
}
```

Respuesta exitosa `200`:

```json
{
    "transactionId": 24,
    "idempotencyKey": "123e4567-e89b-12d3-a456-426614174001",
    "type": "WITHDRAWAL",
    "amount": 1000,
    "currency": "PEN",
    "status": "SUCCESS",
    "accountId": 1,
    "createdAt": "2026-09-14T18:04:57.875325"
}
```

### Transferencia entre cuentas

- `transaction-service` resuelve la cuenta destino por su número y luego
  solicita el débito de la cuenta origen y el crédito de la cuenta destino.
- `account-service` valida el estado y la moneda en cada operación de débito
  o crédito. La validación previa de ambas cuentas antes de debitar y la
  propagación correcta de errores remotos siguen pendientes para el Día 9.
- No existe conversión de divisas: una operación debe usar la moneda de cada
  cuenta involucrada.

`POST /transactions/transfers`

```json
{
  "originAccountId": 3,
  "destinationAccountNumber": "123456789011",
  "amount": 1000,
  "currency": "PEN",
  "description": "Hola"
}
```

Respuesta exitosa `200`:

```json
{
    "transactionId": 21,
    "idempotencyKey": "123e4567-e89b-12d3-a456-426614174002",
    "type": "TRANSFER",
    "amount": 1000,
    "currency": "PEN",
    "status": "SUCCESS",
    "fromAccountId": 3,
    "toAccountId": 1,
    "createdAt": "2026-09-14T17:32:00.7590715"
}
```

## Límites conocidos

- Un error HTTP de `account-service` durante un crédito puede convertirse en
  `Mono.empty()` dentro de `transaction-service`. Por tanto, una transferencia
  con divisas distintas todavía no se informa correctamente al cliente de
  `transaction-service`; su corrección está planificada para el Día 9.
- Las validaciones HTTP de entrada de `transaction-service` y el formato
  unificado de sus errores se trabajarán en el Día 5.
