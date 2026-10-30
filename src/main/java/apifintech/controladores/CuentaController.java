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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import apifintech.entidades.Cuenta;
import apifintech.entidades.CuentaRequest;
import apifintech.excepciones.CuentaNoEncontradaException;
import apifintech.excepciones.ImporteInvalidoException;
import apifintech.excepciones.SaldoInsuficienteException;
import apifintech.excepciones.UsuarioNoEncontradoException;
import apifintech.servicios.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/cuentas")
@Tag(name = "Cuentas", description = "Alta, baja, consulta y movimientos de cuentas bancarias")
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    //GET /cuentas                -> todas.
    //GET /cuentas?usuarioId=1    -> solo las del usuario 1.
    @Operation(summary = "Lista todas las cuentas",
            description = "Devuelve la lista completa. Con el parametro 'usuarioId' filtra "
                    + "por el titular de la cuenta.")
    @ApiResponse(responseCode = "200", description = "Lista de cuentas (puede venir vacia)")
    @GetMapping
    public List<Cuenta> listar(@Parameter(description = "Id del usuario titular", example = "1")
                               @RequestParam(required = false) Long usuarioId) {
        if (usuarioId == null) {
            return cuentaService.listarTodas();
        }
        return cuentaService.listarPorUsuario(usuarioId);
    }

    //GET /cuentas/1
    @Operation(summary = "Trae una cuenta por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe una cuenta con ese id")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Cuenta> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(cuentaService.obtenerOFallar(id)); // 200
        } catch (CuentaNoEncontradaException e) {
            System.out.println("GET /cuentas/" + id + " -> " + e.getMessage());
            return ResponseEntity.notFound().build(); // 404
        }
    }

    //POST /cuentas
    @Operation(summary = "Crea una cuenta para un usuario existente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cuenta creada"),
            @ApiResponse(responseCode = "404", description = "El usuario titular no existe")
    })
    @PostMapping
    public ResponseEntity<Cuenta> crear(@RequestBody CuentaRequest datos) {
        try {
            Cuenta creada = cuentaService.crear(datos);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada); // 201
        } catch (UsuarioNoEncontradoException e) {
            System.out.println("POST /cuentas -> " + e.getMessage());
            return ResponseEntity.notFound().build(); // 404
        }
    }

    //PUT /cuentas/1
    @Operation(summary = "Actualiza los datos propios del tipo de cuenta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta actualizada"),
            @ApiResponse(responseCode = "404", description = "No existe una cuenta con ese id")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Cuenta> actualizar(@PathVariable Long id, @RequestBody CuentaRequest datosNuevos) {
        Cuenta actualizada = cuentaService.actualizar(id, datosNuevos);
        if (actualizada == null) {
            return ResponseEntity.notFound().build(); // 404
        }
        return ResponseEntity.ok(actualizada); // 200
    }

    // DELETE /cuentas/1
    @Operation(summary = "Elimina una cuenta")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cuenta eliminada"),
            @ApiResponse(responseCode = "404", description = "No existe una cuenta con ese id")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!cuentaService.eliminar(id)) {
            return ResponseEntity.notFound().build(); // 404
        }
        return ResponseEntity.noContent().build(); // 204
    }

    //POST /cuentas/1/depositos?importe=15000
    @Operation(summary = "Deposita un importe en la cuenta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deposito realizado"),
            @ApiResponse(responseCode = "400", description = "El importe es cero o negativo"),
            @ApiResponse(responseCode = "404", description = "No existe una cuenta con ese id")
    })
    @PostMapping("/{id}/depositos")
    public ResponseEntity<Cuenta> depositar(@PathVariable Long id, @RequestParam double importe) {
        try {
            return ResponseEntity.ok(cuentaService.depositar(id, importe)); // 200
        } catch (CuentaNoEncontradaException e) {
            System.out.println("POST /cuentas/" + id + "/depositos -> " + e.getMessage());
            return ResponseEntity.notFound().build(); // 404
        } catch (ImporteInvalidoException e) {
            System.out.println("POST /cuentas/" + id + "/depositos -> " + e.getMessage());
            return ResponseEntity.badRequest().build(); // 400
        }
    }

    //POST /cuentas/1/extracciones?importe=5000
    @Operation(summary = "Extrae un importe de la cuenta, si su regla lo permite")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Extraccion realizada"),
            @ApiResponse(responseCode = "400", description = "Importe invalido o saldo insuficiente"),
            @ApiResponse(responseCode = "404", description = "No existe una cuenta con ese id")
    })
    @PostMapping("/{id}/extracciones")
    public ResponseEntity<Cuenta> extraer(@PathVariable Long id, @RequestParam double importe) {
        try {
            return ResponseEntity.ok(cuentaService.extraer(id, importe)); // 200
        } catch (CuentaNoEncontradaException e) {
            System.out.println("POST /cuentas/" + id + "/extracciones -> " + e.getMessage());
            return ResponseEntity.notFound().build(); // 404
        } catch (ImporteInvalidoException | SaldoInsuficienteException e) {
            System.out.println("POST /cuentas/" + id + "/extracciones -> " + e.getMessage());
            return ResponseEntity.badRequest().build(); // 400
        }
    }
}
