package apifintech.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import apifintech.entidades.Usuario;
import apifintech.excepciones.UsuarioNoEncontradoException;
import apifintech.repositorios.UsuarioRepository;


 // Reglas del negocio de usuarios.
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // Inyeccion por constructor:
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // Devuelve el usuario, o un Optional vacio si ese id no existe.
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // Devuelve el usuario o lanza UsuarioNoEncontradoException.
    public Usuario obtenerOFallar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }

    // Crea un usuario. El id lo pone la base: si viene uno en el JSON, se ignora.
    public Usuario crear(Usuario usuario) {
        usuario.setId(null);
        return usuarioRepository.save(usuario);
    }

    // Actualiza nombre, apellido y email. Devuelve null si el id no existe.
    public Usuario actualizar(Long id, Usuario datosNuevos) {
        Usuario existente = buscarPorId(id).orElse(null);
        if (existente == null) {
            return null;
        }

        existente.setNombre(datosNuevos.getNombre());
        existente.setApellido(datosNuevos.getApellido());
        existente.setEmail(datosNuevos.getEmail());


        return usuarioRepository.save(existente);
    }

    //Elimina un usuario (con sus cuentas). Si el id no existe, no hace nada.
    public boolean eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            return false;
        }
        usuarioRepository.deleteById(id);
        return true;
    }
}
