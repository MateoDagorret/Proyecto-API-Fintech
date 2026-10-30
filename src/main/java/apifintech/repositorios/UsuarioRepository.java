package apifintech.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import apifintech.entidades.Usuario;


//Acceso a la tabla de usuarios.
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
