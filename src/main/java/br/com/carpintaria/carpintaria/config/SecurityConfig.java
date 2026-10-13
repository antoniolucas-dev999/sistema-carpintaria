package br.com.carpintaria.carpintaria.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.DisabledException;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.filter.OncePerRequestFilter;

import br.com.carpintaria.carpintaria.model.Usuario;
import br.com.carpintaria.carpintaria.service.UsuarioService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Configuration
public class SecurityConfig {


    /*
     * ==================================================
     * PASSWORD ENCODER
     * ==================================================
     */

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    /*
     * ==================================================
     * SEGURANÇA
     * ==================================================
     */

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UsuarioService usuarioService)
            throws Exception {


        /*
         * ==================================================
         * FILTRO DE PRIMEIRO ACESSO
         * ==================================================
         *
         * Impede que um usuário com troca obrigatória
         * de senha utilize o restante do sistema
         * antes de cadastrar a senha definitiva.
         */

        OncePerRequestFilter primeiroAcessoFilter =
                new OncePerRequestFilter() {


            @Override
            protected void doFilterInternal(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain filterChain)
                    throws ServletException, IOException {


                /*
                 * Remove o context path para trabalhar
                 * somente com a rota real.
                 */

                String uri =
                        request.getRequestURI();


                String contextPath =
                        request.getContextPath();


                if (contextPath != null
                        && !contextPath.isEmpty()
                        && uri.startsWith(contextPath)) {


                    uri =
                            uri.substring(
                                contextPath.length()
                            );
                }


                /*
                 * Rotas que precisam funcionar
                 * durante o primeiro acesso.
                 */

                if (rotaPermitidaDurantePrimeiroAcesso(
                        uri)) {


                    filterChain.doFilter(
                        request,
                        response
                    );


                    return;
                }


                Authentication authentication =
                        SecurityContextHolder
                            .getContext()
                            .getAuthentication();


                /*
                 * Só verifica usuários
                 * realmente autenticados.
                 */

                if (authentication != null
                        && authentication.isAuthenticated()
                        && !(authentication
                            instanceof
                            AnonymousAuthenticationToken)) {


                    String login =
                            authentication
                                .getName();


                    Usuario usuario =
                            usuarioService
                                .buscarPorLogin(
                                    login
                                );


                    /*
                     * Se a troca de senha
                     * ainda for obrigatória,
                     * não permite acessar
                     * nenhuma outra área.
                     */

                    if (usuario
                            .precisaTrocarSenha()) {


                        response.sendRedirect(

                            request.getContextPath()
                            + "/primeiro-acesso"

                        );


                        return;
                    }
                }


                filterChain.doFilter(
                    request,
                    response
                );
            }
        };


        http


            /*
             * ==================================================
             * AUTORIZAÇÃO DAS ROTAS
             * ==================================================
             */

            .authorizeHttpRequests(auth -> auth


                /*
                 * ==================================================
                 * ROTAS PÚBLICAS
                 * ==================================================
                 */

                .requestMatchers(

                    "/login",

                    "/css/**",

                    "/js/**",

                    "/img/**",

                    "/images/**",

                    "/favicon.ico",

                    "/error"

                )
                .permitAll()


                /*
                 * ==================================================
                 * ACESSO NEGADO
                 * ==================================================
                 *
                 * A página será exibida apenas para
                 * usuários que já estejam autenticados.
                 */

                .requestMatchers(
                    "/acesso-negado"
                )
                .authenticated()


                /*
                 * ==================================================
                 * USUÁRIOS
                 * ==================================================
                 *
                 * Somente ADMIN.
                 */

                .requestMatchers(
                    "/usuarios",
                    "/usuarios/**"
                )
                .hasRole(
                    "ADMIN"
                )


                /*
                 * ==================================================
                 * CLIENTES - CONSULTA
                 * ==================================================
                 */

                .requestMatchers(
                    HttpMethod.GET,
                    "/clientes",
                    "/clientes/**"
                )
                .hasAnyRole(
                    "ADMIN",
                    "VENDEDOR",
                    "DIRETOR"
                )


                /*
                 * ==================================================
                 * CLIENTES - ALTERAÇÕES
                 * ==================================================
                 */

                .requestMatchers(
                    "/clientes",
                    "/clientes/**"
                )
                .hasAnyRole(
                    "ADMIN",
                    "VENDEDOR"
                )


                /*
                 * ==================================================
                 * ORÇAMENTOS - CONSULTA
                 * ==================================================
                 */

                .requestMatchers(
                    HttpMethod.GET,
                    "/orcamentos",
                    "/orcamentos/**"
                )
                .hasAnyRole(
                    "ADMIN",
                    "VENDEDOR",
                    "DIRETOR"
                )


                /*
                 * ==================================================
                 * ORÇAMENTOS - ALTERAÇÕES
                 * ==================================================
                 */

                .requestMatchers(
                    "/orcamentos",
                    "/orcamentos/**"
                )
                .hasAnyRole(
                    "ADMIN",
                    "VENDEDOR"
                )


                /*
                 * ==================================================
                 * PEDIDOS - CONSULTA
                 * ==================================================
                 */

                .requestMatchers(
                    HttpMethod.GET,
                    "/pedidos",
                    "/pedidos/**"
                )
                .hasAnyRole(

                    "ADMIN",

                    "VENDEDOR",

                    "FINANCEIRO",

                    "SUPERVISOR_PRODUCAO",

                    "GERENTE_PRODUCAO",

                    "DIRETOR"

                )


                /*
                 * ==================================================
                 * PEDIDOS - ALTERAÇÕES
                 * ==================================================
                 *
                 * DIRETOR fica de fora,
                 * pois seu acesso é somente leitura.
                 *
                 * Depois estas permissões serão
                 * separadas pelas etapas específicas
                 * do pedido.
                 */

                .requestMatchers(
                    "/pedidos",
                    "/pedidos/**"
                )
                .hasAnyRole(

                    "ADMIN",

                    "VENDEDOR",

                    "FINANCEIRO",

                    "SUPERVISOR_PRODUCAO",

                    "GERENTE_PRODUCAO"

                )


                /*
                 * ==================================================
                 * DASHBOARD
                 * ==================================================
                 */

                .requestMatchers(
                    "/",
                    "/dashboard"
                )
                .authenticated()


                /*
                 * ==================================================
                 * MEU PERFIL
                 * ==================================================
                 */

                .requestMatchers(
                    "/meu-perfil",
                    "/meu-perfil/**"
                )
                .authenticated()


                /*
                 * ==================================================
                 * OUTRAS ROTAS
                 * ==================================================
                 */

                .anyRequest()
                .authenticated()

            )


            /*
             * ==================================================
             * ACESSO NEGADO
             * ==================================================
             *
             * Quando o usuário está autenticado,
             * mas seu perfil não possui permissão,
             * será enviado para uma página própria.
             */

            .exceptionHandling(exception -> exception

                .accessDeniedPage(
                    "/acesso-negado"
                )

            )


            /*
             * ==================================================
             * LOGIN
             * ==================================================
             */

            .formLogin(form -> form


                .loginPage(
                    "/login"
                )


                .usernameParameter(
                    "login"
                )


                .passwordParameter(
                    "senha"
                )


                /*
                 * ==================================================
                 * LOGIN REALIZADO COM SUCESSO
                 * ==================================================
                 */

                .successHandler(
                    (
                        request,
                        response,
                        authentication
                    ) -> {


                        String login =
                                authentication
                                    .getName();


                        Usuario usuario =
                                usuarioService
                                    .buscarPorLogin(
                                        login
                                    );


                        /*
                         * Primeiro acesso.
                         */

                        if (usuario
                                .precisaTrocarSenha()) {


                            response.sendRedirect(

                                request.getContextPath()
                                + "/primeiro-acesso"

                            );


                            return;
                        }


                        /*
                         * Usuário liberado.
                         */

                        response.sendRedirect(

                            request.getContextPath()
                            + "/dashboard"

                        );

                    }
                )


                /*
                 * ==================================================
                 * ERRO DE LOGIN
                 * ==================================================
                 */

                .failureHandler(
                    (
                        request,
                        response,
                        exception
                    ) -> {


                        /*
                         * Usuário inativo.
                         */

                        if (
                            exception
                            instanceof
                            DisabledException
                        ) {


                            response.sendRedirect(

                                request.getContextPath()
                                + "/login?inativo"

                            );


                            return;
                        }


                        /*
                         * Login ou senha incorretos.
                         */

                        response.sendRedirect(

                            request.getContextPath()
                            + "/login?erro"

                        );

                    }
                )


                .permitAll()

            )


            /*
             * ==================================================
             * LOGOUT
             * ==================================================
             */

            .logout(logout -> logout


                .logoutSuccessUrl(
                    "/login?logout"
                )


                .permitAll()

            )


            /*
             * ==================================================
             * FILTRO DE PRIMEIRO ACESSO
             * ==================================================
             */

            .addFilterAfter(

                primeiroAcessoFilter,

                UsernamePasswordAuthenticationFilter.class

            );


        return http.build();
    }


    /*
     * ==================================================
     * ROTAS PERMITIDAS DURANTE O PRIMEIRO ACESSO
     * ==================================================
     */

    private boolean rotaPermitidaDurantePrimeiroAcesso(
            String uri) {


        if (uri == null) {

            return false;
        }


        /*
         * Tela e envio da nova senha.
         */

        if (uri.equals(
                "/primeiro-acesso")) {

            return true;
        }


        /*
         * Login e logout.
         */

        if (uri.equals(
                "/login")
                || uri.equals(
                    "/logout")) {

            return true;
        }


        /*
         * Página de erro.
         */

        if (uri.equals(
                "/error")) {

            return true;
        }


        /*
         * Arquivos visuais.
         */

        if (uri.startsWith(
                "/css/")
                || uri.startsWith(
                    "/js/")
                || uri.startsWith(
                    "/img/")
                || uri.startsWith(
                    "/images/")) {

            return true;
        }


        /*
         * Ícone do navegador.
         */

        return uri.equals(
            "/favicon.ico"
        );
    }
}