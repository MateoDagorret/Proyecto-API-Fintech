package apifintech.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

import apifintech.entidades.Usuario;
import apifintech.excepciones.UsuarioNoEncontradoException;
import apifintech.repositorios.UsuarioRepository;


@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository dummyUsuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("listarTodos devuelve lo que le da el repositorio y lo consulta una vez")
    void listarTodosConsultaAlRepositorio() {
        // given
        List<Usuario> usuariosDeMentira = List.of(
                new Usuario("Ana", "Gomez", "30123456", "ana@mail.com"),
                new Usuario("Bruno", "Diaz", "28987654", "bruno@mail.com"));
        when(dummyUsuarioRepository.findAll()).thenReturn(usuariosDeMentira);

        // when
        List<Usuario> resultado = usuarioService.listarTodos();

        // then
        assertEquals(2, resultado.size());
        assertEquals("Ana", resultado.get(0).getNombre());
        verify(dummyUsuarioRepository).findAll();
    }

    @Test
    @DisplayName("buscarPorId con un id inexistente devuelve un Optional vacio")
    void buscarPorIdInexistenteDevuelveVacio() {
        // given
        when(dummyUsuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        Optional<Usuario> resultado = usuarioService.buscarPorId(999L);

        // then
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(dummyUsuarioRepository).findById(999L);
    }

    @Test
    @DisplayName("obtenerOFallar con un id inexistente lanza UsuarioNoEncontradoException")
    void obtenerOFallarLanzaExcepcionCustom() {
        // given
        when(dummyUsuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // when + then
        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioService.obtenerOFallar(999L));
    }

    @Test
    @DisplayName("crear ignora el id que venga en el JSON y guarda el usuario")
    void crearIgnoraElIdYGuarda() {
        // given
        Usuario nuevo = new Usuario("Ana", "Gomez", "30123456", "ana@mail.com");
        nuevo.setId(55L); // no deberia respetarse
        when(dummyUsuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        Usuario creado = usuarioService.crear(nuevo);

        // then
        assertNull(creado.getId());
        verify(dummyUsuarioRepository).save(nuevo);
    }

    @Test
    @DisplayName("actualizar con un id inexistente devuelve null y no guarda nada")
    void actualizarInexistenteDevuelveNull() {
        // given
        when(dummyUsuarioRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        Usuario resultado = usuarioService.actualizar(999L, new Usuario());

        // then
        assertNull(resultado);
    }

    @Test
    @DisplayName("eliminar con un id existente borra y devuelve true")
    void eliminarExistenteDevuelveTrue() {
        // given
        when(dummyUsuarioRepository.existsById(1L)).thenReturn(true);

        // when
        boolean resultado = usuarioService.eliminar(1L);

        // then
        assertTrue(resultado);
        verify(dummyUsuarioRepository).deleteById(1L);
    }
}
