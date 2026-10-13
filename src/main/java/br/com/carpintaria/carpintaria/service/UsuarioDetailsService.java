package br.com.carpintaria.carpintaria.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.carpintaria.carpintaria.model.PerfilUsuario;
import br.com.carpintaria.carpintaria.model.Usuario;
import br.com.carpintaria.carpintaria.repository.UsuarioRepository;


@Service
public class UsuarioDetailsService
        implements UserDetailsService {


    private final UsuarioRepository usuarioRepository;


    public UsuarioDetailsService(
            UsuarioRepository usuarioRepository) {

        this.usuarioRepository =
                usuarioRepository;
    }


    @Override
    public UserDetails loadUserByUsername(
            String login)
            throws UsernameNotFoundException {


        /*
         * ==================================================
         * BUSCAR USUÁRIO
         * ==================================================
         */

        Usuario usuario =
                usuarioRepository
                    .findByLoginIgnoreCase(
                        login.trim()
                    )
                    .orElseThrow(() ->

                        new UsernameNotFoundException(
                            "Usuário não encontrado."
                        )

                    );


        /*
         * ==================================================
         * MONTAR ACESSOS DO SPRING SECURITY
         * ==================================================
         *
         * Cada acesso do sistema será convertido
         * para uma ROLE do Spring Security.
         *
         * Exemplos:
         *
         * VENDEDOR
         * vira:
         * ROLE_VENDEDOR
         *
         * FINANCEIRO
         * vira:
         * ROLE_FINANCEIRO
         *
         * SUPERVISOR_PRODUCAO
         * vira:
         * ROLE_SUPERVISOR_PRODUCAO
         *
         * Um mesmo usuário pode possuir várias
         * ROLEs simultaneamente.
         */

        List<GrantedAuthority> authorities =
                new ArrayList<>();


        /*
         * Percorre todos os tipos de acesso
         * existentes no sistema.
         *
         * A própria entidade Usuario possui
         * a regra de segurança através do método
         * possuiAcesso().
         *
         * ADMIN:
         *
         * recebe automaticamente todos os acessos.
         *
         * Usuários comuns:
         *
         * recebem somente os acessos cadastrados
         * na tabela usuario_acessos.
         */

        for (PerfilUsuario acesso :
                PerfilUsuario.values()) {


            if (usuario.possuiAcesso(
                    acesso)) {


                authorities.add(

                    new SimpleGrantedAuthority(
                        "ROLE_"
                        + acesso.name()
                    )

                );
            }
        }


        /*
         * ==================================================
         * CRIAR USER DETAILS
         * ==================================================
         */

        return User.builder()

                /*
                 * Identificador utilizado
                 * para realizar login.
                 */

                .username(
                    usuario.getLogin()
                )


                /*
                 * Senha BCrypt armazenada
                 * no banco.
                 */

                .password(
                    usuario.getSenha()
                )


                /*
                 * Agora podem existir várias
                 * permissões simultaneamente.
                 *
                 * Exemplo:
                 *
                 * ROLE_VENDEDOR
                 * ROLE_FINANCEIRO
                 * ROLE_SUPERVISOR_PRODUCAO
                 */

                .authorities(
                    authorities
                )


                /*
                 * Usuário inativo não consegue
                 * realizar login.
                 */

                .disabled(
                    !usuario.estaAtivo()
                )


                .build();
    }
}