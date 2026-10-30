package apifintech.controladores;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apifintech.entidades.Cuenta;
import apifintech.entidades.Usuario;
import apifintech.excepciones.UsuarioNoEncontradoException;
import apifintech.servicios.CuentaService;
import apifintech.servicios.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Alta, baja y consulta de usuarios del sistema")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final CuentaService cuentaService;

    public UsuarioController(UsuarioService usuarioService, CuentaService cuentaService) {
        this.usuarioService = usuarioService;
        this.cuentaService = cuentaService;
    }

    //GET /usuarios -> todos.
    @Operation(summary = "Lista todos los usuarios")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios (puede venir vacia)")
    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.listarTodos();
    }

    //GET /usuarios/1
    @Operation(summary = "Trae un usuario por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con ese id")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioService.obtenerOFallar(id)); // 200
        } catch (UsuarioNoEncontradoException e) {
            System.out.println("GET /usuarios/" + id + " -> " + e.getMessage());
            return ResponseEntity.notFound().build(); // 404
        }
    }

    //GET /usuarios/1/cuentas -> las cuentas del usuario 1.
    @Operation(summary = "Lista las cuentas de un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de cuentas (puede venir vacia)"),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con ese id")
    })
    @GetMapping("/{id}/cuentas")
    public ResponseEntity<List<Cuenta>> listarCuentas(@PathVariable Long id) {
        try {
            usuarioService.obtenerOFallar(id);
            return ResponseEntity.ok(cuentaService.listarPorUsuario(id)); // 200
        } catch (UsuarioNoEncontradoException e) {
            System.out.println("GET /usuarios/" + id + "/cuentas -> " + e.getMessage());
            return ResponseEntity.notFound().build(); // 404
        }
    }

    // POST /usuarios
    @Operation(summary = "Crea un usuario")
    @ApiResponse(responseCode = "201", description = "Usuario creado")
    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody Usuario usuario) {
        Usuario creado = usuarioService.crear(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado); // 201
    }

    //PUT /usuarios/1
    @Operation(summary = "Actualiza nombre, apellido y email de un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con ese id")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Long id, @RequestBody Usuario datosNuevos) {
        Usuario actualizado = usuarioService.actualizar(id, datosNuevos);
        if (actualizado == null) {
            return ResponseEntity.notFound().build(); // 404
        }
        return ResponseEntity.ok(actualizado); // 200
    }

    //DELETE /usuarios/1 (borra tambien sus cuentas).
    @Operation(summary = "Elimina un usuario y sus cuentas")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "404", description = "No existe un usuario con ese id")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!usuarioService.eliminar(id)) {
            return ResponseEntity.notFound().build(); // 404
        }
        return ResponseEntity.noContent().build(); // 204
    }
}
