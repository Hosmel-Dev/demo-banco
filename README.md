# Demo Banco

Proyecto de práctica con dos microservicios Spring Boot, un API Gateway y Spring Cloud Config:

- `account-service`: gestión de cuentas y saldo.
- `transaction-service`: registro de débitos, créditos y transferencias.
- `apigateway`: entrada HTTP para el frontend.
- `config-server`: configuración centralizada por servicio y perfil.

## Requisitos

- Docker Desktop con Docker Compose.
- Java 21 y Maven, solo si se ejecutarán los servicios fuera de Docker.

## Configuración para Docker Compose

Trabaja desde `demo banco`. Si todavía no existe `.env`, copia `.env.example`
como punto de partida y sustituye sus valores de ejemplo:

```powershell
Copy-Item .env.example .env
```

`.env` está ignorado por Git; `.env.example` es solo una plantilla. Compose lee
`.env` para sustituir `${VARIABLE}` en `docker-compose.yml`, pero **no envía
todas sus variables automáticamente a los contenedores**. El archivo Compose
pasa explícitamente `BANK_INTERNAL_DEBIT_KEY` a `account-service` y
`transaction-service`; ni el Gateway ni el frontend reciben esa clave.
`account-service-docker.yml` y `transaction-service-docker.yml` referencian
variables que Compose entrega a cada cliente. Los archivos `*-local.yml` son
locales e ignorados por Git; copia los ejemplos incluidos y configura allí los
valores de tu equipo. Config Server también puede servir el perfil `local`.

Configura `BANK_KEYS_DIR` con la carpeta que contiene `public.pem` y
`private.pem`, fuera del repositorio. Si omites la variable, Compose usa
`../bank-keys`. La clave pública se monta en los tres procesos Java y la privada
solo en `account-service`, siempre en modo de solo lectura. Configura también
las credenciales de MySQL y PostgreSQL, `ACCOUNT_SERVICE_BASE_URL` y una clave
interna larga y aleatoria en el `.env` real. No copies secretos reales a
`.env.example`. Si cambias `GATEWAY_PORT`, ajusta también `GATEWAY_URL` en el
frontend para que el proxy de Vite apunte al puerto nuevo.

## Ejecutar y comprobar

Detén los servicios Java que tengas abiertos en el IDE, especialmente el
Gateway del puerto 8080. Con Docker Desktop activo, ejecuta:

```powershell
docker compose config --quiet
docker compose up --build -d
docker compose ps
```

Ese `up` inicia todos los servicios, incluidos Grafana, Loki y Zipkin. Para
iniciar solo los servicios del banco y sus bases de datos, ejecuta:

```powershell
docker compose up --build -d config-server account-service transaction-service apigateway
```

Compose construye las imágenes de los servicios que declaran `build:` y usa
las imágenes publicadas para MySQL, PostgreSQL y las herramientas de
observabilidad. Conserva los contenedores existentes si su imagen y su
configuración no cambiaron.

La primera compilación puede descargar imágenes base y dependencias Maven.
Las compilaciones posteriores reutilizan las imágenes y capas disponibles;
nuestros Dockerfiles aún no tienen una caché persistente de Maven para
recompilaciones después de cambiar `src`. Para iniciar imágenes ya construidas
sin Internet ni compilación, usa `docker compose up -d --no-build --pull never`.
Esto requiere que todas las imágenes necesarias ya estén en el equipo.

### Spring Cloud Config

`config-server` sirve los archivos de `config-repo/` con el perfil `native`.
Compose monta esa carpeta en modo de solo lectura en `/config-repo` y establece
`CONFIG_REPO_LOCATION=file:/config-repo`. Publica el puerto 8888 solo en
`127.0.0.1`. Los tres clientes usan el perfil `docker`, esperan a que el
servidor esté sano y se conectan a `configserver:http://config-server:8888`.
En Docker la importación es obligatoria: si Config Server no responde, los
clientes no arrancan.

Config Server combina `config-repo/application.yml` con el archivo del nombre
y perfil de cada cliente. Por ejemplo, `account-service` con el perfil `docker`
recibe `account-service-docker.yml` y `application.yml`.

Comprueba la configuración servida desde el host:

```powershell
curl.exe http://localhost:8888/account-service/docker
curl.exe http://localhost:8888/api-gateway/docker
```

El primer resultado debe incluir `server.port=8100` y el segundo las rutas
del Gateway hacia los nombres de servicio de Compose. Las credenciales de las
bases de datos, la clave interna y las rutas de archivos PEM para el perfil
`docker` llegan a los clientes mediante variables de entorno. Config Server
puede entregar cualquier perfil solicitado, incluido `local`.

Si el host no puede publicar el puerto 8888, cambia el puerto externo en
`docker-compose.yml` o elimina el mapeo `ports` de `config-server`. La
comunicación interna seguirá usando `config-server:8888`. Sin puerto publicado,
puedes consultar su salud dentro del contenedor con:

```powershell
docker compose exec config-server wget -q -O - http://localhost:8888/actuator/health
```

Para cambiar configuración compartida, edita `config-repo/` y reinicia el
cliente afectado con `docker compose restart <servicio>`; este proyecto la
obtiene durante el arranque, sin refresco automático.

### Ejecución local desde el IDE

Si aún no tienes los archivos locales, cópialos desde `demo banco` y reemplaza
los valores de ejemplo, incluidas las rutas de las claves PEM. Usa la misma
clave interna en `account-service-local.yml` y `transaction-service-local.yml`:

```powershell
Copy-Item config-repo/account-service-local.yml.example config-repo/account-service-local.yml
Copy-Item config-repo/transaction-service-local.yml.example config-repo/transaction-service-local.yml
Copy-Item config-repo/api-gateway-local.yml.example config-repo/api-gateway-local.yml
```

Inicia primero Config Server en el puerto 8888. Si su directorio de trabajo en
el IDE es `demo banco`, el valor predeterminado `file:./config-repo` encuentra
los archivos. Configura `SPRING_PROFILES_ACTIVE=local` en los tres clientes;
así solicitarán `account-service-local.yml`, `transaction-service-local.yml` y
`api-gateway-local.yml`, además de la configuración común. `account-service`
requiere Config Server; los otros dos tienen la importación marcada como
opcional en su `application.yml`, pero necesitan los archivos remotos para la
configuración local prevista.

Si ejecutas Config Server con Maven desde su propio directorio, indica la
ruta relativa correcta al repositorio:

```powershell
cd config-server
$env:CONFIG_REPO_LOCATION = 'file:../config-repo'
mvn spring-boot:run
```

Los datos de MySQL y PostgreSQL están en volúmenes con nombre y se conservan
al recrear contenedores con `up`. `docker compose down -v` **sí elimina esos
volúmenes**; úsalo solo si quieres borrar los datos locales.

### Red y acceso HTTP

```text
Frontend (Vite) → localhost:8080 → apigateway → account-service:8100
                                      └───────→ transaction-service:8101
                                                 └→ account-service:8100
```

Compose publica el puerto HTTP 8080 del Gateway. No publica 8100 ni 8101 en
el host: el navegador y Postman deben entrar por el Gateway. Los tres servicios
comparten la red interna predeterminada de Compose, así que **cualquier
contenedor en esa red** puede conectarse a los microservicios. Quitar `ports`
no reemplaza la autenticación ni equivale a permitir únicamente al Gateway.
MySQL y PostgreSQL sí conservan puertos publicados para usarlos desde el IDE.

Cada microservicio valida por sí mismo el JWT RSA emitido por `account-service`.
`transaction-service` reenvía el Bearer JWT a `account-service` y añade
`X-Internal-Service-Key` a sus llamadas internas de débito, crédito y búsqueda
por número de cuenta. Esas rutas internas no están publicadas por el Gateway.
El navegador nunca envía esa clave interna.

Con los procesos del IDE detenidos, comprueba:

```powershell
curl.exe -i http://localhost:8080/v1/accounts/me
```

Sin JWT debe responder `401`. `localhost:8100` y `localhost:8101` no deben
aceptar conexiones desde el host; si responden, revisa si siguen abiertos en
el IDE u otro contenedor los publicó. El frontend está en `../frontend`:
`npm run dev` usa su proxy de Vite para enviar `/auth` y `/v1` al Gateway.

### Rutas públicas del Gateway

| Método | Ruta | Acceso |
|---|---|---|
| POST | `/auth/login` | Sin JWT; devuelve `username`, `role` y `token` |
| GET | `/auth/me` | JWT válido |
| GET | `/v1/accounts/me` | JWT válido; lista cuentas propias |
| GET | `/v1/accounts/{id}` | JWT válido; solo titular de la cuenta |
| POST | `/v1/transactions/debit` | JWT válido; retiro de cuenta propia |
| POST | `/v1/transactions/transfers` | JWT válido; origen autorizado |
| POST | `/v1/transactions/credit` | JWT válido con rol `OPERATOR` |

El login recibe `{ "username": "...", "password": "..." }`. Para el resto,
envía `Authorization: Bearer <JWT>`. Los tres POST de transacciones requieren
además `Idempotency-Key` de 36 caracteres, por ejemplo un UUID. El Gateway y
los microservicios conservan sus propias reglas de autorización.

## Contratos HTTP internos

- Identificador de cuenta: `Long id`.
- Saldo: `{ "amount": decimal, "currency": "PEN" | "USD" }`.
- Estado de cuenta: `ACTIVE`, `INACTIVE`, `SUSPENDED` o `CLOSED`.
- En `account-service`, el monto de crédito o débito debe estar entre
  `0.01` y `100000.00`.
- Las operaciones de crédito y débito requieren que la moneda enviada
  coincida con la moneda de la cuenta.

## `account-service` consumido por `transaction-service`

- En el IDE: `http://localhost:8100/v1`.
- Entre contenedores: `http://account-service:8100/v1`.
- Desde el navegador: solo las rutas autorizadas del Gateway en
  `http://localhost:8080/v1/accounts`.

### Buscar cuenta por número o id

- `GET /accounts/number/{accountNumber}`
- `GET /accounts/{id}`

La búsqueda por número es interna y exige JWT más `X-Internal-Service-Key`.
La consulta por ID valida que el usuario del JWT sea titular de la cuenta.

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
  "status": "ACTIVE",
  "ownerUserId": 1
}
```

### Acreditar y debitar una cuenta

- La cuenta debe estar en estado `ACTIVE`.
- La moneda enviada debe coincidir con la moneda de la cuenta.
- El débito valida los fondos antes de actualizar el saldo.

- `POST /accounts/debit`
- `POST /accounts/credit`

Estas dos rutas son internas. Requieren el Bearer JWT reenviado por
`transaction-service` y `X-Internal-Service-Key`; el Gateway no las enruta.

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
| Cuenta inexistente | 404 | `/problems/account-not-found` |
| Cuenta ajena | 403 | `/problems/forbidden-access` |
| Cuenta no activa | 423 | `/problems/account-not-active` |
| Divisa distinta | 422 | `/problems/different-currency` |
| Saldo insuficiente | 400 | `/problems/resource-not-enough-funds` |

## API de transacciones

- Desde el navegador: `http://localhost:8080/v1/transactions`.
- En el IDE: `http://localhost:8101/v1/transactions`.
- Entre contenedores: `http://transaction-service:8101/v1/transactions`.

Todos los endpoints siguientes requieren el header `Idempotency-Key` de 36 caracteres:

```http
Idempotency-Key: <clave-del-cliente>
```

Envía también `Authorization: Bearer <JWT>`. El servicio consulta la clave de
idempotencia antes de registrar una nueva operación.

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
  o crédito.
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
