package apifintech.entidades;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import apifintech.excepciones.ImporteInvalidoException;
import apifintech.excepciones.SaldoInsuficienteException;


class CuentaTest {

    private final Usuario titular = new Usuario("Ana", "Gomez", "30123456", "ana@mail.com");

    @Test
    @DisplayName("Depositar suma el importe al saldo")
    void depositarSumaAlSaldo() {
        Cuenta cuenta = new CajaDeAhorro("0001", 1000, titular, 30);

        cuenta.depositar(500);

        assertEquals(1500, cuenta.consultarSaldo());
    }

    @Test
    @DisplayName("Depositar un importe negativo lanza ImporteInvalidoException")
    void depositarImporteNegativoFalla() {
        Cuenta cuenta = new CajaDeAhorro("0001", 1000, titular, 30);

        assertThrows(ImporteInvalidoException.class, () -> cuenta.depositar(-100));
        assertEquals(1000, cuenta.consultarSaldo());
    }

    @Test
    @DisplayName("La caja de ahorro no permite extraer mas que el saldo")
    void cajaDeAhorroNoPermiteSaldoNegativo() {
        Cuenta cuenta = new CajaDeAhorro("0001", 1000, titular, 30);

        assertThrows(SaldoInsuficienteException.class, () -> cuenta.extraer(1500));
        assertEquals(1000, cuenta.consultarSaldo());
    }

    @Test
    @DisplayName("La caja de ahorro permite extraer todo el saldo")
    void cajaDeAhorroPermiteExtraerTodo() {
        Cuenta cuenta = new CajaDeAhorro("0001", 1000, titular, 30);

        cuenta.extraer(1000);

        assertEquals(0, cuenta.consultarSaldo());
    }

    @Test
    @DisplayName("La cuenta corriente permite quedar en negativo hasta el descubierto")
    void cuentaCorrienteUsaElDescubierto() {
        Cuenta cuenta = new CuentaCorriente("0002", 1000, titular, 500);

        cuenta.extraer(1400);

        assertEquals(-400, cuenta.consultarSaldo());
    }

    @Test
    @DisplayName("La cuenta corriente no permite superar el descubierto")
    void cuentaCorrienteNoSuperaElDescubierto() {
        Cuenta cuenta = new CuentaCorriente("0002", 1000, titular, 500);

        // Saldo 1000 + descubierto 500 = 1500 disponible; 1501 no se puede.
        assertThrows(SaldoInsuficienteException.class, () -> cuenta.extraer(1501));
        assertEquals(1000, cuenta.consultarSaldo());
    }

    @Test
    @DisplayName("El interes mensual se calcula sobre el saldo (tasa anual / 12)")
    void cajaDeAhorroAplicaInteresMensual() {
        CajaDeAhorro cuenta = new CajaDeAhorro("0001", 120000, titular, 48);

        cuenta.aplicarInteresMensual();

        // 120000 * 48% / 12 = 4800 de interes.
        assertEquals(124800, cuenta.consultarSaldo());
    }
}
