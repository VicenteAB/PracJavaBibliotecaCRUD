# Apuntes Spring Boot - Proyecto Biblioteca

---

## 1. MODELO / ENTIDAD

Una entidad representa una tabla en la base de datos.

| Anotación | Qué hace |
|-----------|----------|
| `@Entity` | Le dice a JPA que esta clase es una tabla en la base de datos |
| `@Table(name = "nombre")` | Define el nombre exacto de la tabla en MySQL |
| `@Id` | Marca el atributo como la clave primaria (Primary Key) |
| `@GeneratedValue(strategy = GenerationType.IDENTITY)` | El id se genera automáticamente con AUTO_INCREMENT de MySQL |
| `@NotBlank` | El campo no puede estar vacío ni en blanco |
| `@Size(min=1, max=100)` | Define el largo mínimo y máximo del campo |

**Reglas importantes:**
- Siempre necesita un constructor vacío `public Libro() {}` para que JPA pueda crear objetos
- El id que se genera automáticamente no va en el constructor con parámetros

```java
@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    @Size(min=1, max=100)
    private String titulo;

    public Libro() {} // obligatorio para JPA
    public Libro(String titulo) { this.titulo = titulo; }
}
```

---

## 2. REPOSITORY

El Repository es la capa que habla directamente con la base de datos.
Con JPA solo necesitas una interfaz, los métodos se generan automáticamente.

| Método heredado | Qué hace |
|----------------|----------|
| `save(objeto)` | INSERT - guarda o actualiza |
| `findById(id)` | SELECT WHERE id = ? |
| `findAll()` | SELECT * |
| `deleteById(id)` | DELETE WHERE id = ? |
| `existsById(id)` | Retorna true si existe |

**Métodos personalizados:**
JPA genera el SQL automáticamente según el nombre del método:
```java
// findBy + NombreExactoDelAtributo
Optional<Usuario> findByUsername(String username);
// genera: SELECT * FROM usuarios WHERE username = ?
```

**¿Qué es Optional?**
Es una "caja" que puede contener un objeto o estar vacía:
```java
Optional<Usuario> caja = repo.findByUsername("juan");
caja.isPresent()   // ¿tiene algo adentro?
caja.get()         // saca el objeto
caja.orElse(null)  // si está vacía, retorna null
```

```java
@Repository
public interface LibroRepository extends JpaRepository<Libro, String> {
    // JpaRepository<TipoDeObjeto, TipoDelId>
    Optional<Libro> findByTitulo(String titulo); // método personalizado
}
```

---

## 3. SERVICE

El Service contiene la lógica del negocio. Es el intermediario entre el Controller y el Repository.

| Anotación | Qué hace |
|-----------|----------|
| `@Service` | Le dice a Spring que gestione esta clase |
| `@Autowired` | Spring inyecta el objeto automáticamente, sin necesidad de hacer `new` |

**¿Qué es @Autowired?**
En vez de crear el objeto manualmente:
```java
// Sin @Autowired (manual)
private LibroRepository repo = new LibroRepository();

// Con @Autowired (Spring lo crea por ti)
@Autowired
private LibroRepository repo;
```

```java
@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    public void guardarLibro(Libro libro) {
        libroRepository.save(libro);
    }
}
```

---

## 4. CONTROLLER

El Controller recibe los requests de Postman o el frontend y devuelve respuestas HTTP.

| Anotación | Qué hace |
|-----------|----------|
| `@RestController` | Esta clase recibe y responde requests HTTP |
| `@RequestMapping("/ruta")` | Define la ruta base para todos los endpoints de la clase |
| `@GetMapping` | Endpoint para GET (consultar datos) |
| `@PostMapping` | Endpoint para POST (crear datos) |
| `@DeleteMapping` | Endpoint para DELETE (eliminar datos) |
| `@PutMapping` | Endpoint para PUT (actualizar datos) |
| `@RequestBody` | Los datos vienen en el body del request como JSON |
| `@PathVariable` | El dato viene en la URL ej: `/libros/{isbn}` |
| `@Valid` | Activa las validaciones del objeto antes de procesarlo |

**ResponseEntity - Códigos HTTP:**
```java
ResponseEntity.ok(dato)              // 200 OK + dato
ResponseEntity.status(201).body(dato) // 201 Created + dato
ResponseEntity.status(409).build()   // 409 Conflict sin dato
ResponseEntity.notFound().build()    // 404 Not Found sin dato
ResponseEntity.status(401).build()   // 401 Unauthorized sin dato
```

**Regla:** `.body(dato)` cuando quieres enviar datos, `.build()` cuando solo envías el código HTTP.

```java
@RestController
@RequestMapping("/libros")
public class LibroController {

    @Autowired
    private LibroService libroService;

    @GetMapping("/{isbn}")
    public ResponseEntity<Libro> buscar(@PathVariable String isbn) {
        Libro libro = libroService.buscarPorISBN(isbn);
        if (libro != null) {
            return ResponseEntity.ok(libro);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
```

---

## 5. MANEJO DE ERRORES - GlobalExceptionHandler

Captura errores automáticamente sin que tú los llames manualmente.

| Anotación | Qué hace |
|-----------|----------|
| `@RestControllerAdvice` | Esta clase escucha errores de todos los Controllers |
| `@ExceptionHandler(X.class)` | Maneja un tipo específico de error |

**¿Cómo funciona?**
Cuando ocurre un error, Spring busca automáticamente si hay un método con `@ExceptionHandler` para ese tipo de error y lo ejecuta solo.

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Se activa cuando @Valid falla
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> manejaValidacion(MethodArgumentNotValidException ex) {
        var errores = ex.getBindingResult().getFieldErrors();
        String mensaje = "";
        for (var error : errores) {
            mensaje += error.getField() + ": " + error.getDefaultMessage() + ", ";
        }
        mensaje = mensaje.substring(0, mensaje.length() - 2);
        return ResponseEntity.badRequest().body(mensaje);
    }
}
```

---

## 6. SPRING SECURITY + JWT

### ¿Qué es JWT?
Un token (pulsera) que el servidor entrega al usuario cuando hace login.
El usuario lo envía en cada request para demostrar que está autenticado.

**Flujo completo:**
```
1. POST /auth/registro  → guarda el usuario con contraseña cifrada
2. POST /auth/login     → verifica credenciales y entrega un TOKEN
3. GET  /libros         → envía el token en el header, Spring lo verifica
```

**¿Cómo se envía el token en Postman?**
```
Header: Authorization
Value:  Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

### Clases necesarias para JWT:

**1. JwtUtil** - La fábrica de tokens
- `generarToken(username)` → crea y retorna el token
- `extraerUsername(token)` → lee el token y retorna el username

**2. JwtFilter** - El guardia en la puerta
- Se ejecuta en cada request automáticamente
- Lee el header `Authorization`
- Verifica que empiece con `Bearer `
- Extrae el username del token
- Le avisa a Spring Security quién está autenticado

**3. SecurityConfig** - El reglamento
- Define qué rutas son públicas: `.requestMatchers("/auth/**").permitAll()`
- Define qué rutas necesitan token: `.anyRequest().authenticated()`
- Registra el JwtFilter

---

### Anotaciones de Spring Security:

| Elemento | Qué hace |
|----------|----------|
| `@EnableWebSecurity` | Activa la configuración de seguridad |
| `@Configuration` | Esta clase tiene configuraciones de Spring |
| `@Bean` | Spring gestiona el resultado de este método |
| `@Component` | Similar a @Service, Spring gestiona esta clase (uso general) |
| `@Value("${jwt.secret}")` | Lee un valor del archivo application.properties |
| `BCryptPasswordEncoder` | Cifra contraseñas. `encode()` cifra, `matches()` compara |

---

### application.properties (NO subir a GitHub):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/biblioteca
spring.datasource.username=root
spring.datasource.password=tuContrasena
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
jwt.secret=tuClaveSecretaSinEspacios123456
```

---

## 7. ARQUITECTURA GENERAL

```
Postman/Frontend
      ↓
  Controller    → Recibe el request y el JSON (@RequestBody)
      ↓            Devuelve ResponseEntity con código HTTP
   Service      → Aplica la lógica del negocio
      ↓            Decide qué hacer con los datos
 Repository     → Ejecuta la query en la base de datos
      ↓
   MySQL
```

**Cada capa solo habla con la de al lado, nunca se saltan capas.**

---

## 8. DIFERENCIAS IMPORTANTES

| Concepto | Para qué sirve |
|----------|---------------|
| `@Entity` | Relacionado con la base de datos |
| `@Component/@Service/@Repository/@RestController` | Relacionado con Spring (para inyectar con @Autowired) |
| `Optional<T>` | Caja que puede contener un objeto o estar vacía |
| `.body()` | Enviar datos en la respuesta HTTP |
| `.build()` | Enviar solo el código HTTP sin datos |
| `executeQuery()` | Para SELECT (retorna datos) |
| `executeUpdate()` | Para INSERT, UPDATE, DELETE |
