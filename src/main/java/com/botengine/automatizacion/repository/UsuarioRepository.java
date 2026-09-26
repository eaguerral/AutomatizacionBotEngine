package com.botengine.automatizacion.repository;

import com.botengine.automatizacion.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameAndIdNot(
            String username,
            Long id
    );

    boolean existsByEmailAndIdNot(
            String email,
            Long id
    );
}