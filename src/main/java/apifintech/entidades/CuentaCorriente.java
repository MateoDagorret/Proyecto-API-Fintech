package apifintech.entidades;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CUENTA_CORRIENTE")
public class CuentaCorriente extends Cuenta {

    // Monto maximo que la cuenta puede quedar en negativo.
    private double descubierto;

    // Constructor vacio.
    public CuentaCorriente() {
    }

    public CuentaCorriente(String numero, double saldo, Usuario usuario, double descubierto) {
        super(numero, saldo, usuario);
        this.descubierto = descubierto;
    }

    @Override
    public double saldoDisponibleParaExtraer() {
        return getSaldo() + descubierto;
    }

    @Override
    public String getTipo() {
        return "CUENTA_CORRIENTE";
    }

    public double getDescubierto() {
        return descubierto;
    }

    public void setDescubierto(double descubierto) {
        this.descubierto = descubierto;
    }
}
