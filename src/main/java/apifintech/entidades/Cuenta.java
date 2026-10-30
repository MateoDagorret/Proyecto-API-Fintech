package apifintech.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import apifintech.excepciones.ImporteInvalidoException;
import apifintech.excepciones.SaldoInsuficienteException;
import apifintech.interfaces.OperacionesBancarias;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "cuentas")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_cuenta")
public abstract class Cuenta implements OperacionesBancarias {

    //El id lo genera la base sola. Por eso no se manda al crear una cuenta.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Numero de cuenta tipo CBU: 22 digitos, generado por el sistema.
    private String numero;

    private double saldo;

    private boolean activa = true;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnoreProperties("cuentas")
    private Usuario usuario;

    //Constructor vacio.
    protected Cuenta() {
    }

    protected Cuenta(String numero, double saldo, Usuario usuario) {
        this.numero = numero;
        this.saldo = saldo;
        this.usuario = usuario;
    }

    @Override
    public void depositar(double importe) {
        validarImportePositivo(importe);
        this.saldo += importe;
    }

    //extrae un importe si la cuenta lo permite.
    @Override
    public void extraer(double importe) {
        validarImportePositivo(importe);
        if (importe > saldoDisponibleParaExtraer()) {
            throw new SaldoInsuficienteException(this.id, importe, saldoDisponibleParaExtraer());
        }
        this.saldo -= importe;
    }

    @Override
    public double consultarSaldo() {
        return saldo;
    }

    // Cuanto se puede extraer como maximo. Cada tipo de cuenta lo define.
    public abstract double saldoDisponibleParaExtraer();

    public abstract String getTipo();

    private void validarImportePositivo(double importe) {
        if (importe <= 0) {
            throw new ImporteInvalidoException(importe);
        }
    }

    //Getters y setters.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
