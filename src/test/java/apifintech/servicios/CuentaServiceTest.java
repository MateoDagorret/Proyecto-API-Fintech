package apifintech.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import apifintech.entidades.CajaDeAhorro;
import apifintech.entidades.Cuenta;
import apifintech.entidades.CuentaRequest;
import apifintech.entidades.Usuario;
import apifintech.excepciones.CuentaNoEncontradaException;
import apifintech.excepciones.SaldoInsuficienteException;
import apifintech.excepciones.UsuarioNoEncontradoException;
import apifintech.repositorios.CuentaRepository;

@ExtendWith(MockitoExtension.class)
class CuentaServiceTest {

    @Mock
    private CuentaRepository dummyCuentaRepository;

    @Mock
    private UsuarioService dummyUsuarioService;

    @InjectMocks
    private CuentaService cuentaService;

    @Test
    @DisplayName("listarTodas devuelve lo que le da el repositorio y lo consulta una vez")
    void listarTodasConsultaAlRepositorio() {
        // given
        Usuario titular = new Usuario("Ana", "Gomez", "30123456", "ana@mail.com");
        List<Cuenta> cuentasDeMentira = List.of(new CajaDeAhorro("0001", 1000, titular, 30));
        when(dummyCuentaRepository.findAll()).thenReturn(cuentasDeMentira);

        // when
        List<Cuenta> resultado = cuentaService.listarTodas();

        // then
        assertEquals(1, resultado.size());
        assertEquals("0001", resultado.get(0).getNumero());
        verify(dummyCuentaRepository).findAll();
    }

    @Test
    @DisplayName("buscarPorId con un id inexistente devuelve un Optional vacio")
    void buscarPorIdInexistenteDevuelveVacio() {
        // given
        when(dummyCuentaRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        Optional<Cuenta> resultado = cuentaService.buscarPorId(999L);

        // then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(dummyCuentaRepository).findById(999L);
    }

    @Test
    @DisplayName("obtenerOFallar con un id inexistente lanza CuentaNoEncontradaException")
    void obtenerOFallarLanzaExcepcionCustom() {
        // given
        when(dummyCuentaRepository.findById(999L)).thenReturn(Optional.empty());

        // when + then
        assertThrows(CuentaNoEncontradaException.class, () -> cuentaService.obtenerOFallar(999L));
    }

    @Test
    @DisplayName("crear con un usuario inexistente lanza UsuarioNoEncontradoException")
    void crearSinTitularFalla() {
        // given
        CuentaRequest datos = new CuentaRequest();
        datos.setTipo("CAJA_DE_AHORRO");
        datos.setUsuarioId(999L);
        datos.setSaldoInicial(1000);
        when(dummyUsuarioService.obtenerOFallar(999L))
                .thenThrow(new UsuarioNoEncontradoException(999L));

        // when + then
        assertThrows(UsuarioNoEncontradoException.class, () -> cuentaService.crear(datos));
    }

    @Test
    @DisplayName("crear una caja de ahorro guarda la cuenta con numero generado")
    void crearCajaDeAhorroGuardaLaCuenta() {
        // given
        Usuario titular = new Usuario("Ana", "Gomez", "30123456", "ana@mail.com");
        CuentaRequest datos = new CuentaRequest();
        datos.setTipo("CAJA_DE_AHORRO");
        datos.setUsuarioId(1L);
        datos.setSaldoInicial(1000);
        datos.setTasaInteres(30);

        when(dummyUsuarioService.obtenerOFallar(1L)).thenReturn(titular);
        when(dummyCuentaRepository.existsByNumero(anyString())).thenReturn(false);
        when(dummyCuentaRepository.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        Cuenta creada = cuentaService.crear(datos);

        // then
        assertEquals(1000, creada.consultarSaldo());
        assertEquals("CAJA_DE_AHORRO", creada.getTipo());
        assertEquals(22, creada.getNumero().length());
        assertEquals(titular, creada.getUsuario());
        verify(dummyCuentaRepository).save(any(Cuenta.class));
    }

    @Test
    @DisplayName("depositar en una cuenta inexistente lanza CuentaNoEncontradaException")
    void depositarEnCuentaInexistenteFalla() {
        // given
        when(dummyCuentaRepository.findById(999L)).thenReturn(Optional.empty());

        // when + then
        assertThrows(CuentaNoEncontradaException.class, () -> cuentaService.depositar(999L, 100));
    }

    @Test
    @DisplayName("extraer mas de lo disponible lanza SaldoInsuficienteException y no guarda")
    void extraerSinSaldoFalla() {
        // given
        Usuario titular = new Usuario("Ana", "Gomez", "30123456", "ana@mail.com");
        Cuenta cuenta = new CajaDeAhorro("0001", 100, titular, 30);
        when(dummyCuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));

        // when + then
        assertThrows(SaldoInsuficienteException.class, () -> cuentaService.extraer(1L, 500));
        assertEquals(100, cuenta.consultarSaldo());
    }

    @Test
    @DisplayName("extraer un importe valido actualiza el saldo y guarda la cuenta")
    void extraerImporteValidoGuarda() {
        // given
        Usuario titular = new Usuario("Ana", "Gomez", "30123456", "ana@mail.com");
        Cuenta cuenta = new CajaDeAhorro("0001", 1000, titular, 30);
        when(dummyCuentaRepository.findById(1L)).thenReturn(Optional.of(cuenta));
        when(dummyCuentaRepository.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        Cuenta resultado = cuentaService.extraer(1L, 400);

        // then
        assertEquals(600, resultado.consultarSaldo());
        verify(dummyCuentaRepository).save(cuenta);
    }
}
