package apifintech.excepciones;

//Se lanza cuando un deposito o extraccion llega con importe cero o negativo.
public class ImporteInvalidoException extends RuntimeException {

    public ImporteInvalidoException(double importe) {
        super("El importe tiene que ser mayor que cero. Valor recibido: " + importe);
    }
}
