package apifintech.entidades;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


@Entity
@DiscriminatorValue("CAJA_DE_AHORRO")
public class CajaDeAhorro extends Cuenta {

    private double tasaInteres;

    //Constructor vacio.
    public CajaDeAhorro() {
    }

    public CajaDeAhorro(String numero, double saldo, Usuario usuario, double tasaInteres) {
        super(numero, saldo, usuario);
        this.tasaInteres = tasaInteres;
    }

    //Solo se puede extraer lo que hay: nada de saldo negativo.
    @Override
    public double saldoDisponibleParaExtraer() {
        return getSaldo();
    }

    @Override
    public String getTipo() {
        return "CAJA_DE_AHORRO";
    }

    public void aplicarInteresMensual() {
        double interes = getSaldo() * (tasaInteres / 100) / 12;
        depositar(interes);
    }

    public double getTasaInteres() {
        return tasaInteres;
    }

    public void setTasaInteres(double tasaInteres) {
        this.tasaInteres = tasaInteres;
    }
}
