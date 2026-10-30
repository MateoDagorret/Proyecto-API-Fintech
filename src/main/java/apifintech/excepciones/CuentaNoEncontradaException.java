package apifintech.excepciones;

//Se lanza cuando se busca una cuenta por un id que no existe.

public class CuentaNoEncontradaException extends RuntimeException {

    public CuentaNoEncontradaException(Long id) {
        super("No existe una cuenta con el id " + id);
    }
}
