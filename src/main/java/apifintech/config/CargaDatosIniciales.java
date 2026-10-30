package apifintech.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import apifintech.entidades.CajaDeAhorro;
import apifintech.entidades.CuentaCorriente;
import apifintech.entidades.Usuario;
import apifintech.repositorios.UsuarioRepository;

@Component
public class CargaDatosIniciales implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    public CargaDatosIniciales(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        // Como Usuario tiene cascade = ALL hacia sus cuentas, al guardar el usuario se guardan tambien las cuentas de su lista.
        Usuario mariela = new Usuario("Mariela", "Morales", "30123456", "mariela.morales@mail.com");
        mariela.getCuentas().add(new CajaDeAhorro("101", 152300.50, mariela, 30));
        mariela.getCuentas().add(new CuentaCorriente("102", 8400.00, mariela, 50000));

        Usuario marcos = new Usuario("Marcos", "Acuña", "28987654", "marcos.acuña@mail.com");
        marcos.getCuentas().add(new CajaDeAhorro("103", 0.00, marcos, 42));

        Usuario jazmin = new Usuario("Jazmin", "Torres", "35411223", "jazmintorres@mail.com");
        jazmin.getCuentas().add(new CuentaCorriente("104", 1250000.00, jazmin, 200000));

        // saveAll hace un INSERT por cada usuario (y sus cuentas, por el cascade).
        usuarioRepository.saveAll(List.of(mariela, marcos, jazmin));

        System.out.println("=== " + usuarioRepository.count());
    }
}
