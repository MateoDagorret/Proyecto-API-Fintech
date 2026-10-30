package apifintech.servicios;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import apifintech.entidades.CajaDeAhorro;
import apifintech.entidades.Cuenta;
import apifintech.entidades.CuentaCorriente;
import apifintech.entidades.CuentaRequest;
import apifintech.entidades.Usuario;
import apifintech.excepciones.CuentaNoEncontradaException;
import apifintech.repositorios.CuentaRepository;


// Reglas del negocio de cuentas.
@Service
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final UsuarioService usuarioService;

    public CuentaService(CuentaRepository cuentaRepository, UsuarioService usuarioService) {
        this.cuentaRepository = cuentaRepository;
        this.usuarioService = usuarioService;
    }

    public List<Cuenta> listarTodas() {
        return cuentaRepository.findAll();
    }

    // Devuelve la cuenta, o un Optional vacio si ese id no existe.
    public Optional<Cuenta> buscarPorId(Long id) {
        return cuentaRepository.findById(id);
    }

    // Devuelve la cuenta o lanza CuentaNoEncontradaException.
    public Cuenta obtenerOFallar(Long id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new CuentaNoEncontradaException(id));
    }

    public List<Cuenta> listarPorUsuario(Long usuarioId) {
        return cuentaRepository.findByUsuarioId(usuarioId);
    }


     // Da de alta una cuenta para un usuario existente.
    public Cuenta crear(CuentaRequest datos) {
        // Si el usuario no existe, obtenerOFallar lanza la excepcion aca mismo.
        Usuario titular = usuarioService.obtenerOFallar(datos.getUsuarioId());

        String numero = generarNumeroDeCuenta();

        Cuenta cuenta;
        if ("CUENTA_CORRIENTE".equalsIgnoreCase(datos.getTipo())) {
            cuenta = new CuentaCorriente(numero, datos.getSaldoInicial(), titular, datos.getDescubierto());
        } else {
            // Por defecto se crea una caja de ahorro.
            cuenta = new CajaDeAhorro(numero, datos.getSaldoInicial(), titular, datos.getTasaInteres());
        }

        return cuentaRepository.save(cuenta);
    }


    //Actualiza los datos propios de cada tipo de cuenta. Devuelve null si el id no existe.
    public Cuenta actualizar(Long id, CuentaRequest datosNuevos) {
        Cuenta existente = buscarPorId(id).orElse(null);
        if (existente == null) {
            return null;
        }

        if (existente instanceof CuentaCorriente corriente) {
            corriente.setDescubierto(datosNuevos.getDescubierto());
        } else if (existente instanceof CajaDeAhorro caja) {
            caja.setTasaInteres(datosNuevos.getTasaInteres());
        }

        return cuentaRepository.save(existente);
    }

    // Elimina una cuenta. Si el id no existe, no hace nada.
    public boolean eliminar(Long id) {
        if (!cuentaRepository.existsById(id)) {
            return false;
        }
        cuentaRepository.deleteById(id);
        return true;
    }

    //Suma un importe al saldo. Lanza CuentaNoEncontradaException si el id no existe, o ImporteInvalidoException si el importe no es positivo.
    public Cuenta depositar(Long id, double importe) {
        Cuenta cuenta = obtenerOFallar(id);
        cuenta.depositar(importe);
        return cuentaRepository.save(cuenta);
    }

    // Resta un importe del saldo, respetando la regla de cada tipo de cuenta. Lanza CuentaNoEncontradaException si el id no existe, o SaldoInsuficienteException si la cuenta no lo permite.
    public Cuenta extraer(Long id, double importe) {
        Cuenta cuenta = obtenerOFallar(id);
        cuenta.extraer(importe);
        return cuentaRepository.save(cuenta);
    }

    //Genera un numero de 3 digitos que no este repetido en la base
    private String generarNumeroDeCuenta() {
        String numero;
        do {
            // 3 digitos: el primero distinto de cero para que tenga largo fijo.
            long primero = ThreadLocalRandom.current().nextLong(1, 10);
            long resto = ThreadLocalRandom.current().nextLong(0, 1_000L);
            numero = primero + String.format("%03d", resto);
        } while (cuentaRepository.existsByNumero(numero));
        return numero;
    }
}
