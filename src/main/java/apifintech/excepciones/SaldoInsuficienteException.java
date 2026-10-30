package apifintech.excepciones;

//Se lanza cuando se intenta extraer mas de lo que la cuenta permite.

public class SaldoInsuficienteException extends RuntimeException {

    public SaldoInsuficienteException(Long cuentaId, double importe, double disponible) {
        super("La cuenta " + cuentaId + " no tiene saldo suficiente: se intentaron extraer "
                + importe + " pero solo hay disponible " + disponible);
    }
}
