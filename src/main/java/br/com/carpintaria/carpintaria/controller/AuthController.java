package br.com.carpintaria.carpintaria.controller;

import java.security.Principal;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.carpintaria.carpintaria.model.Usuario;
import br.com.carpintaria.carpintaria.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@Controller
public class AuthController {


    private final UsuarioService usuarioService;


    public AuthController(
            UsuarioService usuarioService) {

        this.usuarioService =
                usuarioService;
    }


    /*
     * ==================================================
     * LOGIN
     * ==================================================
     */

    @GetMapping("/login")
    public String login() {

        return "login";
    }


    /*
     * ==================================================
     * ACESSO NEGADO
     * ==================================================
     *
     * Esta página é utilizada quando o usuário
     * está autenticado, mas seu perfil não possui
     * permissão para acessar determinada área.
     */

    @GetMapping("/acesso-negado")
    public String acessoNegado(
            Principal principal,
            Model model) {


        /*
         * Se por algum motivo não houver
         * usuário autenticado, volta ao login.
         */
        if (principal == null) {

            return "redirect:/login";
        }


        Usuario usuario =
                usuarioService.buscarPorLogin(
                    principal.getName()
                );


        model.addAttribute(
            "nomeUsuario",
            usuario.getNome()
        );


        model.addAttribute(
            "loginUsuario",
            usuario.getLogin()
        );


        model.addAttribute(
            "perfilUsuario",
            usuario.getPerfil()
        );


        return "acesso-negado";
    }


    /*
     * ==================================================
     * PRIMEIRO ACESSO
     * ==================================================
     */

    @GetMapping("/primeiro-acesso")
    public String primeiroAcesso(
            Principal principal,
            Model model) {


        /*
         * O usuário precisa estar autenticado.
         */
        if (principal == null) {

            return "redirect:/login";
        }


        String login =
                principal.getName();


        Usuario usuario =
                usuarioService.buscarPorLogin(
                    login
                );


        /*
         * Se o usuário já realizou a troca
         * obrigatória, não precisa acessar
         * esta tela novamente.
         */
        if (!usuario.precisaTrocarSenha()) {

            return "redirect:/dashboard";
        }


        model.addAttribute(
            "nomeUsuario",
            usuario.getNome()
        );


        model.addAttribute(
            "loginUsuario",
            usuario.getLogin()
        );


        return "primeiro-acesso";
    }


    /*
     * ==================================================
     * DEFINIR NOVA SENHA
     * ==================================================
     */

    @PostMapping("/primeiro-acesso")
    public String definirSenhaPrimeiroAcesso(

            Principal principal,

            @RequestParam("novaSenha")
            String novaSenha,

            @RequestParam("confirmarSenha")
            String confirmarSenha,

            Model model,

            HttpServletRequest request) {


        /*
         * Precisa estar autenticado.
         */
        if (principal == null) {

            return "redirect:/login";
        }


        String login =
                principal.getName();


        try {


            usuarioService
                .definirSenhaPrimeiroAcesso(

                    login,

                    novaSenha,

                    confirmarSenha

                );


        } catch (
                IllegalArgumentException
                | IllegalStateException e) {


            Usuario usuario =
                    usuarioService.buscarPorLogin(
                        login
                    );


            model.addAttribute(
                "nomeUsuario",
                usuario.getNome()
            );


            model.addAttribute(
                "loginUsuario",
                usuario.getLogin()
            );


            model.addAttribute(
                "erro",
                e.getMessage()
            );


            return "primeiro-acesso";
        }


        /*
         * ==================================================
         * ENCERRAR A SESSÃO
         * ==================================================
         *
         * Depois da troca da senha,
         * obrigamos o usuário a entrar
         * novamente utilizando a nova senha.
         */


        SecurityContextHolder
            .clearContext();


        HttpSession session =
                request.getSession(
                    false
                );


        if (session != null) {

            session.invalidate();
        }


        return "redirect:/login?senhaAlterada";
    }
}