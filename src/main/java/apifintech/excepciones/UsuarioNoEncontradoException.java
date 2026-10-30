package apifintech.excepciones;

// Se lanza cuando se busca un usuario por un id que no existe.

public class UsuarioNoEncontradoException extends RuntimeException {

    public UsuarioNoEncontradoException(Long id) {
        super("No existe un usuario con el id " + id);
    }
}
