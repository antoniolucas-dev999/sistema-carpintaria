package br.com.carpintaria.carpintaria.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.carpintaria.carpintaria.dto.CadastroUsuarioDTO;
import br.com.carpintaria.carpintaria.dto.EdicaoUsuarioDTO;
import br.com.carpintaria.carpintaria.model.PerfilUsuario;
import br.com.carpintaria.carpintaria.model.Usuario;
import br.com.carpintaria.carpintaria.service.UsuarioService;

import jakarta.validation.Valid;


@Controller
public class UsuarioController {


    private final UsuarioService usuarioService;


    public UsuarioController(
            UsuarioService usuarioService) {

        this.usuarioService =
                usuarioService;
    }


    /*
     * ==================================================
     * LISTAR USUÁRIOS
     * ==================================================
     */

    @GetMapping("/usuarios")
    public String listarUsuarios(
            Model model,
            Authentication authentication) {


        Usuario usuarioLogado =
                buscarUsuarioLogado(
                    authentication
                );


        /*
         * Somente ADMIN pode acessar
         * o gerenciamento de usuários.
         */

        if (!ehAdministrador(
                usuarioLogado)) {

            return "redirect:/dashboard";
        }


        /*
         * Lista todos:
         *
         * ativos e inativos.
         */

        model.addAttribute(
            "usuarios",
            usuarioService.listarTodosUsuarios()
        );


        model.addAttribute(
            "usuarioLogadoId",
            usuarioLogado.getId()
        );


        model.addAttribute(
            "usuarioLogadoPerfil",
            usuarioLogado.getPerfil()
        );


        return "usuarios/lista";
    }


    /*
     * ==================================================
     * ABRIR NOVO USUÁRIO
     * ==================================================
     */

    @GetMapping("/usuarios/novo")
    public String novoUsuario(
            Model model,
            Authentication authentication) {


        Usuario usuarioLogado =
                buscarUsuarioLogado(
                    authentication
                );


        /*
         * Somente ADMIN pode criar usuários.
         */

        if (!ehAdministrador(
                usuarioLogado)) {

            return "redirect:/dashboard";
        }


        /*
         * DTO vazio para o formulário.
         */

        model.addAttribute(
            "cadastro",
            new CadastroUsuarioDTO()
        );


        /*
         * Perfis disponíveis.
         */

        model.addAttribute(
            "perfis",
            PerfilUsuario.values()
        );


        return "usuarios/novo";
    }


    /*
     * ==================================================
     * CADASTRAR NOVO USUÁRIO
     * ==================================================
     */

    @PostMapping("/usuarios/novo")
    public String cadastrarUsuario(

            @Valid
            @ModelAttribute("cadastro")
            CadastroUsuarioDTO cadastro,

            BindingResult result,

            Model model,

            Authentication authentication,

            RedirectAttributes redirectAttributes) {


        Usuario usuarioLogado =
                buscarUsuarioLogado(
                    authentication
                );


        /*
         * Somente ADMIN pode cadastrar.
         */

        if (!ehAdministrador(
                usuarioLogado)) {

            return "redirect:/dashboard";
        }


        /*
         * ==================================================
         * VERIFICAR LOGIN DUPLICADO
         * ==================================================
         */

        if (cadastro.getLogin() != null
                && !cadastro.getLogin()
                    .isBlank()
                && usuarioService
                    .loginJaCadastrado(
                        cadastro.getLogin()
                    )) {


            result.rejectValue(
                "login",
                "login.existente",
                "Este login já está sendo utilizado."
            );
        }


        /*
         * ==================================================
         * VERIFICAR E-MAIL DUPLICADO
         * ==================================================
         *
         * O e-mail é opcional.
         */

        if (cadastro.getEmail() != null
                && !cadastro.getEmail()
                    .isBlank()
                && usuarioService
                    .emailJaCadastrado(
                        cadastro.getEmail()
                    )) {


            result.rejectValue(
                "email",
                "email.existente",
                "Este e-mail já está sendo utilizado."
            );
        }


        /*
         * ==================================================
         * ERROS DE VALIDAÇÃO
         * ==================================================
         */

        if (result.hasErrors()) {


            model.addAttribute(
                "perfis",
                PerfilUsuario.values()
            );


            return "usuarios/novo";
        }


        /*
         * ==================================================
         * CRIAR USUÁRIO
         * ==================================================
         */

        try {


            usuarioService.cadastrar(
                cadastro
            );


        } catch (IllegalArgumentException e) {


            model.addAttribute(
                "perfis",
                PerfilUsuario.values()
            );


            model.addAttribute(
                "erro",
                e.getMessage()
            );


            return "usuarios/novo";
        }


        /*
         * ==================================================
         * SUCESSO
         * ==================================================
         */

        redirectAttributes.addFlashAttribute(
            "sucesso",
            "Usuário criado com sucesso. A senha temporária é CarpintariaReis."
        );


        return "redirect:/usuarios";
    }


    /*
     * ==================================================
     * ABRIR EDIÇÃO DE USUÁRIO
     * ==================================================
     */

    @GetMapping(
        "/usuarios/{id}/editar"
    )
    public String abrirEdicaoUsuario(

            @PathVariable Long id,

            Model model,

            Authentication authentication,

            RedirectAttributes redirectAttributes) {


        Usuario usuarioLogado =
                buscarUsuarioLogado(
                    authentication
                );


        /*
         * Somente ADMIN pode editar usuários.
         */

        if (!ehAdministrador(
                usuarioLogado)) {

            return "redirect:/dashboard";
        }


        /*
         * ==================================================
         * PROTEGER A PRÓPRIA CONTA ADMINISTRATIVA
         * ==================================================
         *
         * A própria conta deve ser alterada
         * através de "Meu Perfil".
         *
         * Isso impede que o administrador
         * altere acidentalmente seu perfil
         * ou login enquanto está autenticado.
         */

        if (usuarioLogado
                .getId()
                .equals(id)) {


            redirectAttributes
                .addFlashAttribute(
                    "erro",
                    "Sua própria conta deve ser alterada pela opção Meu Perfil."
                );


            return "redirect:/usuarios";
        }


        /*
         * Busca o usuário que será editado.
         */

        Usuario usuario =
                usuarioService.buscarPorId(
                    id
                );


        /*
         * ==================================================
         * PREENCHER DTO
         * ==================================================
         */

        EdicaoUsuarioDTO edicao =
                new EdicaoUsuarioDTO();


        edicao.setNome(
            usuario.getNome()
        );


        edicao.setLogin(
            usuario.getLogin()
        );


        edicao.setEmail(
            usuario.getEmail()
        );


        edicao.setPerfil(
            usuario.getPerfil()
        );


        /*
         * ==================================================
         * ACESSOS ATUAIS
         * ==================================================
         *
         * Carrega os acessos já cadastrados
         * na tabela usuario_acessos.
         *
         * Exemplo:
         *
         * VENDEDOR
         * SUPERVISOR_PRODUCAO
         * FINANCEIRO
         *
         * Assim os checkboxes correspondentes
         * aparecerão marcados na tela.
         */

        edicao.setAcessos(
            usuario.getAcessos()
        );


        /*
         * ==================================================
         * ENVIAR DADOS PARA A TELA
         * ==================================================
         */

        model.addAttribute(
            "edicao",
            edicao
        );


        model.addAttribute(
            "usuario",
            usuario
        );


        /*
         * Perfis principais disponíveis.
         *
         * Aqui ADMIN continua existindo,
         * pois ADMIN é um perfil principal,
         * e não uma permissão adicional comum.
         */

        model.addAttribute(
            "perfis",
            PerfilUsuario.values()
        );


        /*
         * Acessos adicionais que podem ser
         * concedidos ao usuário.
         *
         * ADMIN propositalmente não aparece.
         */

        model.addAttribute(
            "acessosDisponiveis",
            acessosDisponiveis()
        );


        return "usuarios/editar";
    }


    /*
     * ==================================================
     * SALVAR EDIÇÃO DE USUÁRIO
     * ==================================================
     */

    @PostMapping(
        "/usuarios/{id}/editar"
    )
    public String salvarEdicaoUsuario(

            @PathVariable Long id,

            @Valid
            @ModelAttribute("edicao")
            EdicaoUsuarioDTO edicao,

            BindingResult result,

            Model model,

            Authentication authentication,

            RedirectAttributes redirectAttributes) {


        Usuario usuarioLogado =
                buscarUsuarioLogado(
                    authentication
                );


        /*
         * Somente ADMIN pode editar usuários.
         */

        if (!ehAdministrador(
                usuarioLogado)) {

            return "redirect:/dashboard";
        }


        /*
         * ==================================================
         * PROTEGER A PRÓPRIA CONTA
         * ==================================================
         */

        if (usuarioLogado
                .getId()
                .equals(id)) {


            redirectAttributes
                .addFlashAttribute(
                    "erro",
                    "Sua própria conta deve ser alterada pela opção Meu Perfil."
                );


            return "redirect:/usuarios";
        }


        /*
         * Usuário que está sendo editado.
         */

        Usuario usuario =
                usuarioService.buscarPorId(
                    id
                );


        /*
         * ==================================================
         * ERROS DO DTO
         * ==================================================
         */

        if (result.hasErrors()) {


            model.addAttribute(
                "usuario",
                usuario
            );


            model.addAttribute(
                "perfis",
                PerfilUsuario.values()
            );


            model.addAttribute(
                "acessosDisponiveis",
                acessosDisponiveis()
            );


            return "usuarios/editar";
        }


        /*
         * ==================================================
         * ATUALIZAR
         * ==================================================
         *
         * O UsuarioService verifica:
         *
         * login duplicado
         * e-mail duplicado
         * perfil obrigatório
         * acessos escolhidos
         * proteção do acesso ADMIN
         */

        try {


            usuarioService.editarUsuario(
                id,
                edicao
            );


        } catch (IllegalArgumentException e) {


            /*
             * Exibe a mensagem na própria
             * tela de edição.
             */

            result.reject(
                "usuario.edicao",
                e.getMessage()
            );


            model.addAttribute(
                "usuario",
                usuario
            );


            model.addAttribute(
                "perfis",
                PerfilUsuario.values()
            );


            model.addAttribute(
                "acessosDisponiveis",
                acessosDisponiveis()
            );


            return "usuarios/editar";
        }


        /*
         * ==================================================
         * SUCESSO
         * ==================================================
         */

        redirectAttributes
            .addFlashAttribute(
                "sucesso",
                "Usuário atualizado com sucesso."
            );


        return "redirect:/usuarios";
    }


    /*
     * ==================================================
     * REDEFINIR SENHA TEMPORÁRIA
     * ==================================================
     */

    @PostMapping(
        "/usuarios/{id}/redefinir-senha"
    )
    public String redefinirSenhaTemporaria(

            @PathVariable Long id,

            Authentication authentication,

            RedirectAttributes redirectAttributes) {


        Usuario usuarioLogado =
                buscarUsuarioLogado(
                    authentication
                );


        /*
         * Somente ADMIN pode redefinir
         * senha de usuários.
         */

        if (!ehAdministrador(
                usuarioLogado)) {

            return "redirect:/dashboard";
        }


        /*
         * ==================================================
         * NÃO REDEFINIR A PRÓPRIA SENHA
         * ==================================================
         */

        if (usuarioLogado
                .getId()
                .equals(id)) {


            redirectAttributes.addFlashAttribute(
                "erro",
                "Para alterar sua própria senha, utilize a opção Meu Perfil."
            );


            return "redirect:/usuarios";
        }


        /*
         * Busca o usuário antes da alteração
         * para utilizarmos o nome na mensagem.
         */

        Usuario usuario =
                usuarioService.buscarPorId(
                    id
                );


        /*
         * ==================================================
         * REDEFINIR
         * ==================================================
         */

        usuarioService
            .redefinirSenhaTemporaria(
                id
            );


        /*
         * ==================================================
         * SUCESSO
         * ==================================================
         */

        redirectAttributes.addFlashAttribute(
            "sucesso",
            "Senha de "
            + usuario.getNome()
            + " redefinida com sucesso. "
            + "A senha temporária é CarpintariaReis."
        );


        return "redirect:/usuarios";
    }


    /*
     * ==================================================
     * ATIVAR USUÁRIO
     * ==================================================
     */

    @PostMapping(
        "/usuarios/{id}/ativar"
    )
    public String ativarUsuario(

            @PathVariable Long id,

            Authentication authentication,

            RedirectAttributes redirectAttributes) {


        Usuario usuarioLogado =
                buscarUsuarioLogado(
                    authentication
                );


        /*
         * Somente ADMIN pode ativar usuários.
         */

        if (!ehAdministrador(
                usuarioLogado)) {

            return "redirect:/dashboard";
        }


        usuarioService.ativar(
            id
        );


        redirectAttributes.addFlashAttribute(
            "sucesso",
            "Usuário ativado com sucesso."
        );


        return "redirect:/usuarios";
    }


    /*
     * ==================================================
     * INATIVAR USUÁRIO
     * ==================================================
     */

    @PostMapping(
        "/usuarios/{id}/inativar"
    )
    public String inativarUsuario(

            @PathVariable Long id,

            Authentication authentication,

            RedirectAttributes redirectAttributes) {


        Usuario usuarioLogado =
                buscarUsuarioLogado(
                    authentication
                );


        /*
         * Somente ADMIN pode inativar usuários.
         */

        if (!ehAdministrador(
                usuarioLogado)) {

            return "redirect:/dashboard";
        }


        /*
         * ==================================================
         * NÃO PODE INATIVAR A PRÓPRIA CONTA
         * ==================================================
         */

        if (usuarioLogado
                .getId()
                .equals(id)) {


            redirectAttributes.addFlashAttribute(
                "erro",
                "Você não pode inativar a própria conta."
            );


            return "redirect:/usuarios";
        }


        usuarioService.inativar(
            id
        );


        redirectAttributes.addFlashAttribute(
            "sucesso",
            "Usuário inativado com sucesso."
        );


        return "redirect:/usuarios";
    }


    /*
     * ==================================================
     * ACESSOS DISPONÍVEIS
     * ==================================================
     *
     * São os acessos operacionais que podem
     * ser combinados livremente.
     *
     * ADMIN não aparece nesta lista porque
     * é um perfil administrativo especial.
     */

    private List<PerfilUsuario> acessosDisponiveis() {


        return List.of(

            PerfilUsuario.VENDEDOR,

            PerfilUsuario.FINANCEIRO,

            PerfilUsuario.SUPERVISOR_PRODUCAO,

            PerfilUsuario.GERENTE_PRODUCAO,

            PerfilUsuario.DIRETOR

        );
    }


    /*
     * ==================================================
     * BUSCAR USUÁRIO LOGADO
     * ==================================================
     */

    private Usuario buscarUsuarioLogado(
            Authentication authentication) {


        return usuarioService
                .buscarPorLogin(
                    authentication.getName()
                );
    }


    /*
     * ==================================================
     * VERIFICAR ADMINISTRADOR
     * ==================================================
     */

    private boolean ehAdministrador(
            Usuario usuario) {


        return usuario.getPerfil()
                == PerfilUsuario.ADMIN;
    }
}