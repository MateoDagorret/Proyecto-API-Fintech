package apifintech.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import apifintech.entidades.Cuenta;

// Acceso a la tabla de cuentas.
@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    List<Cuenta> findByUsuarioId(Long usuarioId);

    boolean existsByNumero(String numero);
}
