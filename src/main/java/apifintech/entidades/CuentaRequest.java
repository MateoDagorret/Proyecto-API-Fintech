package apifintech.entidades;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Datos para dar de alta una cuenta")
public class CuentaRequest {

    @Schema(description = "Tipo de cuenta", example = "CAJA_DE_AHORRO",
            allowableValues = {"CAJA_DE_AHORRO", "CUENTA_CORRIENTE"})
    private String tipo;

    @Schema(description = "Id del usuario titular", example = "1")
    private Long usuarioId;

    @Schema(description = "Saldo con el que abre la cuenta", example = "10000")
    private double saldoInicial;

    @Schema(description = "Solo para CUENTA_CORRIENTE: cuanto puede quedar en negativo", example = "5000")
    private double descubierto;

    @Schema(description = "Solo para CAJA_DE_AHORRO: tasa de interes anual en %", example = "30")
    private double tasaInteres;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public double getSaldoInicial() {
        return saldoInicial;
    }

    public void setSaldoInicial(double saldoInicial) {
        this.saldoInicial = saldoInicial;
    }

    public double getDescubierto() {
        return descubierto;
    }

    public void setDescubierto(double descubierto) {
        this.descubierto = descubierto;
    }

    public double getTasaInteres() {
        return tasaInteres;
    }

    public void setTasaInteres(double tasaInteres) {
        this.tasaInteres = tasaInteres;
    }
}
