package br.com.carpintaria.carpintaria.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.carpintaria.carpintaria.model.Usuario;
import br.com.carpintaria.carpintaria.service.ClienteService;
import br.com.carpintaria.carpintaria.service.OrcamentoService;
import br.com.carpintaria.carpintaria.service.UsuarioService;

@Controller
public class DashboardController {

    private final UsuarioService usuarioService;
    private final ClienteService clienteService;
    private final OrcamentoService orcamentoService;


    public DashboardController(
            UsuarioService usuarioService,
            ClienteService clienteService,
            OrcamentoService orcamentoService) {

        this.usuarioService = usuarioService;
        this.clienteService = clienteService;
        this.orcamentoService = orcamentoService;
    }


    /*
     * PÁGINA INICIAL
     */
    @GetMapping("/")
    public String inicio() {

        return "redirect:/dashboard";
    }


    /*
     * DASHBOARD
     */
    @GetMapping("/dashboard")
    public String dashboard(
            Principal principal,
            Model model) {

        /*
         * USUÁRIO LOGADO
         *
         * Agora o Principal guarda o LOGIN
         * e não mais o e-mail.
         *
         * Exemplo:
         * adm.carpintaria
         * lucas
         * financeiro01
         */
        String login =
                principal.getName();


        Usuario usuario =
                usuarioService.buscarPorLogin(
                    login
                );


        /*
         * INFORMAÇÕES DO USUÁRIO
         */
        model.addAttribute(
            "nomeUsuario",
            usuario.getNome()
        );


        model.addAttribute(
            "loginUsuario",
            usuario.getLogin()
        );


        model.addAttribute(
            "emailUsuario",
            usuario.getEmail()
        );


        model.addAttribute(
            "perfilUsuario",
            usuario.getPerfil()
        );


        /*
         * CLIENTES
         */
        model.addAttribute(
            "totalClientes",
            clienteService.contarClientes()
        );


        /*
         * ORÇAMENTOS
         */
        model.addAttribute(
            "totalOrcamentos",
            orcamentoService.contarOrcamentos()
        );


        /*
         * VALOR TOTAL ORÇADO
         */
        model.addAttribute(
            "valorTotalOrcado",
            orcamentoService.calcularValorTotalOrcado()
        );


        return "dashboard";
    }
}