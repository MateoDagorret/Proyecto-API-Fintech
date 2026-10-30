package apifintech.interfaces;

public interface OperacionesBancarias {

    // Suma un importe al saldo. El importe tiene que ser positivo.
    void depositar(double importe);

    //Resta un importe del saldo, si las reglas de la cuenta lo permiten
    void extraer(double importe);

    // Devuelve el saldo actual.
    double consultarSaldo();
}
