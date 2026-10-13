package br.com.carpintaria.carpintaria.controller;

import java.util.Optional;

import org.springframework.core.io.Resource;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.carpintaria.carpintaria.dto.AtualizacaoPerfilDTO;
import br.com.carpintaria.carpintaria.model.Usuario;

import br.com.carpintaria.carpintaria.service.FotoPerfilService;
import br.com.carpintaria.carpintaria.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.validation.Valid;


@Controller
public class PerfilController {


    private final UsuarioService usuarioService;

    private final FotoPerfilService fotoPerfilService;


    public PerfilController(

            UsuarioService usuarioService,

            FotoPerfilService fotoPerfilService) {


        this.usuarioService =
                usuarioService;


        this.fotoPerfilService =
                fotoPerfilService;
    }


    /*
     * ==================================================
     * ABRIR MEU PERFIL
     * ==================================================
     */

    @GetMapping("/meu-perfil")
    public String abrirPerfil(
            Authentication authentication,
            Model model) {


        Usuario usuario =
                buscarUsuarioLogado(
                    authentication
                );


        AtualizacaoPerfilDTO perfil =
                new AtualizacaoPerfilDTO();


        perfil.setNome(
            usuario.getNome()
        );


        perfil.setEmail(
            usuario.getEmail()
        );


        model.addAttribute(
            "perfil",
            perfil
        );


        model.addAttribute(
            "usuario",
            usuario
        );


        return "perfil";
    }


    /*
     * ==================================================
     * SALVAR MEU PERFIL
     * ==================================================
     */

    @PostMapping("/meu-perfil")
    public String salvarPerfil(

            @ModelAttribute("perfil")
            @Valid
            AtualizacaoPerfilDTO perfil,

            BindingResult bindingResult,

            Authentication authentication,

            HttpServletRequest request,

            HttpServletResponse response,

            Model model,

            RedirectAttributes redirectAttributes) {


        Usuario usuario =
                buscarUsuarioLogado(
                    authentication
                );


        /*
         * ==================================================
         * VALIDAÇÃO DO DTO
         * ==================================================
         */

        if (bindingResult.hasErrors()) {


            model.addAttribute(
                "usuario",
                usuario
            );


            return "perfil";
        }


        boolean senhaAlterada =
                perfil.getNovaSenha() != null
                && !perfil
                    .getNovaSenha()
                    .isBlank();


        /*
         * ==================================================
         * ATUALIZAÇÃO
         * ==================================================
         */

        try {


            usuarioService.atualizarPerfil(
                usuario.getId(),
                perfil
            );


        } catch (IllegalArgumentException e) {


            bindingResult.reject(
                "perfil.erro",
                e.getMessage()
            );


            model.addAttribute(
                "usuario",
                usuario
            );


            return "perfil";
        }


        /*
         * ==================================================
         * ALTEROU A SENHA
         * ==================================================
         */

        if (senhaAlterada) {


            SecurityContextLogoutHandler logoutHandler =
                    new SecurityContextLogoutHandler();


            logoutHandler.logout(
                request,
                response,
                authentication
            );


            return "redirect:/login?perfilAtualizado";
        }


        redirectAttributes.addFlashAttribute(
            "sucesso",
            "Perfil atualizado com sucesso."
        );


        return "redirect:/meu-perfil";
    }


    /*
     * ==================================================
     * ENVIAR FOTO DE PERFIL
     * ==================================================
     */

    @PostMapping("/meu-perfil/foto")
    public String enviarFotoPerfil(

            @RequestParam("foto")
            MultipartFile foto,

            Authentication authentication,

            RedirectAttributes redirectAttributes) {


        Usuario usuario =
                buscarUsuarioLogado(
                    authentication
                );


        try {


            fotoPerfilService
                .salvarFotoPerfil(
                    usuario,
                    foto
                );


        } catch (
                IllegalArgumentException
                | IllegalStateException e) {


            redirectAttributes
                .addFlashAttribute(
                    "erroFoto",
                    e.getMessage()
                );


            return "redirect:/meu-perfil";
        }


        redirectAttributes
            .addFlashAttribute(
                "sucesso",
                "Foto de perfil atualizada com sucesso."
            );


        return "redirect:/meu-perfil";
    }


    /*
     * ==================================================
     * EXIBIR FOTO DE PERFIL
     * ==================================================
     */

    @GetMapping("/meu-perfil/foto")
    public ResponseEntity<Resource> exibirFotoPerfil(
            Authentication authentication) {


        Usuario usuario =
                buscarUsuarioLogado(
                    authentication
                );


        Optional<Resource> foto =
                fotoPerfilService
                    .carregarFotoPerfil(
                        usuario
                    );


        if (foto.isEmpty()) {

            return ResponseEntity
                    .notFound()
                    .build();
        }


        String contentType =
                fotoPerfilService
                    .obterContentType(
                        usuario
                    );


        return ResponseEntity
                .ok()

                .cacheControl(
                    CacheControl.noCache()
                )

                .header(
                    HttpHeaders.CONTENT_TYPE,
                    contentType
                )

                .body(
                    foto.get()
                );
    }


    /*
     * ==================================================
     * REMOVER FOTO DE PERFIL
     * ==================================================
     */

    @PostMapping("/meu-perfil/foto/remover")
    public String removerFotoPerfil(

            Authentication authentication,

            RedirectAttributes redirectAttributes) {


        Usuario usuario =
                buscarUsuarioLogado(
                    authentication
                );


        fotoPerfilService
            .removerFotoPerfil(
                usuario
            );


        redirectAttributes
            .addFlashAttribute(
                "sucesso",
                "Foto de perfil removida com sucesso."
            );


        return "redirect:/meu-perfil";
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
}