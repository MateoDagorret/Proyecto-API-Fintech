# API Fintech — Gestión de usuarios y cuentas

**Diplomatura en Desarrollo de Software FinTech: IA y Microservicios — Clase 14, Actividad de medio término**

API REST hecha con Spring Boot que simula un sistema básico fintech: permite gestionar
usuarios y sus cuentas bancarias (cajas de ahorro y cuentas corrientes), con depósitos y
extracciones. Los datos se persisten en **MySQL**.

---

## 1. Decisiones de diseño (POO)

El modelo de dominio aplica los conceptos pedidos en la consigna:

- **Herencia:** `CuentaCorriente` y `CajaDeAhorro` extienden la clase **abstracta** `Cuenta`.
  No se puede hacer `new Cuenta()`: solo existen cuentas concretas.
- **Clase abstracta:** `Cuenta` concentra el comportamiento común (`depositar`, `extraer`)
  y deja que cada hija defina su propia regla de extracción con el método abstracto
  `saldoDisponibleParaExtraer()`.
- **Interfaz:** `OperacionesBancarias` es el contrato que `Cuenta` implementa
  (`depositar`, `extraer`, `consultarSaldo`).
- **Polimorfismo:** el servicio crea una `CajaDeAhorro` o una `CuentaCorriente` según el
  tipo pedido, pero todo el resto del código trabaja con la referencia abstracta `Cuenta`.
- **Relación entre entidades:** un `Usuario` tiene muchas `Cuenta` (`@OneToMany` /
  `@ManyToOne`, con `cascade = ALL`).

Reglas de negocio:

| Tipo de cuenta | Regla de extracción | Dato propio |
|---|---|---|
| `CAJA_DE_AHORRO` | No permite quedar en negativo | `tasaInteres` (anual, en %) |
| `CUENTA_CORRIENTE` | Permite quedar en negativo hasta el `descubierto` | `descubierto` |

### Estructura por capas

```
src/main/java/apifintech/
├── ApiFintechApplication.java   <- punto de entrada
├── config/                      <- carga de datos de ejemplo
├── controladores/               <- capa HTTP (endpoints REST)
├── entidades/                   <- modelo de dominio (JPA) + DTO de alta
├── excepciones/                 <- excepciones custom
├── interfaces/                  <- contrato OperacionesBancarias
├── repositorios/                <- acceso a datos (Spring Data JPA)
└── servicios/                   <- reglas de negocio
```

---

## 2. Qué necesitás antes de empezar

- **JDK 21** instalado.
- **IntelliJ IDEA** (Community alcanza).
- **MySQL** corriendo en `localhost:3306`, con usuario `root` y contraseña `1234`.
  Si tu instalación usa otras credenciales, cambialas en
  `src/main/resources/application.yml`.
- **Conexión a internet la primera vez**, para que Maven descargue las dependencias.

La base `api_fintech` **se crea sola** la primera vez
(`createDatabaseIfNotExist=true` en la URL del `application.yml`), y Hibernate crea las
tablas a partir de las entidades (`ddl-auto: update`).

---

## 3. Cómo correrlo

1. Abrí la carpeta del proyecto (la que contiene el `pom.xml`) con **File > Open** en IntelliJ.
2. Esperá a que Maven termine de descargar las dependencias.
3. Ejecutá el `main` de `ApiFintechApplication.java`.

Al arrancar se cargan **3 usuarios y 4 cuentas de ejemplo** (solo si la base está vacía),
así la API se puede probar de inmediato.

**Para probar que funciona**, abrí en el navegador: http://localhost:8080/cuentas

---

## 4. Endpoints

### Cuentas

| Método | Ruta | Qué hace                                     |
|--------|------|----------------------------------------------|
| `GET` | `/cuentas` | Lista todas las cuentas                      |
| `GET` | `/cuentas?usuarioId=1` | Filtra por titular                           |
| `GET` | `/cuentas/{id}` | Trae una cuenta                              |
| `POST` | `/cuentas` | Crea una cuenta para un usuario              |
| `PUT` | `/cuentas/{id}` | Actualiza descubierto o tasa (según el tipo) |
| `DELETE` | `/cuentas/{id}` | Elimina una cuenta                           |
| `POST` | `/cuentas/{id}/depositos?importe=N` | Suma un importe al saldo                     |
| `POST` | `/cuentas/{id}/extracciones?importe=N` | Resta un importe, si la regla lo permite     |

### Usuarios

| Método | Ruta | Qué hace                           |
|--------|------|------------------------------------|
| `GET` | `/usuarios` | Lista todos los usuarios           |
| `GET` | `/usuarios/{id}` | Trae un usuario                    |
| `POST` | `/usuarios` | Crea un usuario nuevo              |
| `PUT` | `/usuarios/{id}` | Actualiza nombre, apellido y email |
| `DELETE` | `/usuarios/{id}` | Elimina el usuario y sus cuentas   |

### Ejemplos con `curl` (desde Git Bash)

**Crear un usuario** (el `id` no se manda: lo genera la base)

```bash
curl -i -X POST http://localhost:8080/usuarios -H "Content-Type: application/json" -d '{"nombre":"Federico","apellido":"Roldan","dni":"33445566","email":"fede.roldan@mail.com"}'
```

**Crear una cuenta corriente para ese usuario** (el número de cuenta lo genera el sistema)

```bash
curl -i -X POST http://localhost:8080/cuentas -H "Content-Type: application/json" -d '{"tipo":"CUENTA_CORRIENTE","usuarioId":4,"saldoInicial":25000,"descubierto":10000}'
```

**Crear una caja de ahorro**

```bash
curl -i -X POST http://localhost:8080/cuentas -H "Content-Type: application/json" -d '{"tipo":"CAJA_DE_AHORRO","usuarioId":4,"saldoInicial":25000,"tasaInteres":36}'
```

**Extraer más de lo que permite la regla** (responde 400 y el saldo no cambia)

```bash
curl -i -X POST "http://localhost:8080/cuentas/3/extracciones?importe=999999"
```

**Traer un id que no existe** (responde 404, excepción custom `CuentaNoEncontradaException`)

```bash
curl -i http://localhost:8080/cuentas/999
```


---

## 5. Manejo de errores

El servicio lanza **excepciones custom** y el controlador las atrapa con **try/catch**
para responder el código HTTP correcto:

| Excepción | Cuándo se lanza | Respuesta |
|---|---|---|
| `CuentaNoEncontradaException` | El id de la cuenta no existe | `404` |
| `UsuarioNoEncontradoException` | El id del usuario no existe | `404` |
| `SaldoInsuficienteException` | La extracción supera lo disponible | `400` |
| `ImporteInvalidoException` | El importe es cero o negativo | `400` |

---

## 6. Testing

Los tests unitarios están en `src/test/java`:

- `entidades/CuentaTest` — lógica de negocio: depósitos, extracciones, descubierto,
  interés mensual, importes inválidos.
- `servicios/CuentaServiceTest` — servicio de cuentas: listados, búsqueda por id,
  excepciones custom, creación con polimorfismo, depósitos y extracciones.
- `servicios/UsuarioServiceTest` — servicio de usuarios: listados, creación,
  actualización y eliminación.



## 7. Documentación Swagger / OpenAPI

Con la aplicación corriendo:

- **Interfaz gráfica (Swagger UI):** http://localhost:8080/swagger-ui/index.html
- **JSON OpenAPI:** http://localhost:8080/v3/api-docs

Todos los endpoints están documentados con `@Operation`, `@ApiResponse` y `@Tag`,
y desde Swagger UI se pueden probar directamente.
