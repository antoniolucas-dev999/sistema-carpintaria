package br.com.carpintaria.carpintaria.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.carpintaria.carpintaria.model.Usuario;


public interface UsuarioRepository
        extends JpaRepository<Usuario, Long> {


    /*
     * ==================================================
     * LOGIN
     * ==================================================
     */

    Optional<Usuario> findByLoginIgnoreCase(
            String login
    );


    boolean existsByLoginIgnoreCase(
            String login
    );


    /*
     * ==================================================
     * E-MAIL
     * ==================================================
     *
     * Mantidos temporariamente porque partes antigas
     * do sistema ainda utilizam o e-mail.
     *
     * Depois vamos migrar tudo para login.
     * ==================================================
     */

    Optional<Usuario> findByEmailIgnoreCase(
            String email
    );


    boolean existsByEmailIgnoreCase(
            String email
    );
}