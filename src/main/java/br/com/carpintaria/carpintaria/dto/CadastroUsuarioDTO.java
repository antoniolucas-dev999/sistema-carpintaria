package br.com.carpintaria.carpintaria.dto;

import br.com.carpintaria.carpintaria.model.PerfilUsuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public class CadastroUsuarioDTO {


    /*
     * ==================================================
     * NOME
     * ==================================================
     */

    @NotBlank(
        message = "O nome é obrigatório"
    )
    @Size(
        max = 100,
        message = "O nome deve possuir no máximo 100 caracteres"
    )
    private String nome;


    /*
     * ==================================================
     * LOGIN
     * ==================================================
     *
     * Exemplos:
     *
     * lucas
     * joao.silva
     * financeiro01
     * supervisor_producao
     */

    @NotBlank(
        message = "O login é obrigatório"
    )
    @Size(
        min = 3,
        max = 80,
        message = "O login deve possuir entre 3 e 80 caracteres"
    )
    @Pattern(
        regexp = "^[A-Za-z0-9._-]+$",
        message = "O login pode conter apenas letras, números, ponto, hífen e underline"
    )
    private String login;


    /*
     * ==================================================
     * E-MAIL
     * ==================================================
     *
     * O e-mail agora é opcional.
     */

    @Email(
        message = "Informe um e-mail válido"
    )
    @Size(
        max = 150,
        message = "O e-mail deve possuir no máximo 150 caracteres"
    )
    private String email;


    /*
     * ==================================================
     * PERFIL
     * ==================================================
     */

    @NotNull(
        message = "Selecione o perfil do usuário"
    )
    private PerfilUsuario perfil;


    /*
     * ==================================================
     * CAMPOS TEMPORÁRIOS DE COMPATIBILIDADE
     * ==================================================
     *
     * O cadastro antigo utilizava senha e confirmação.
     *
     * Eles serão removidos depois que ajustarmos
     * o UsuarioService.
     *
     * No novo fluxo o ADMIN não escolherá a senha.
     * O sistema utilizará automaticamente
     * a senha temporária definida para primeiro acesso.
     */

    private String senha;

    private String confirmarSenha;


    /*
     * ==================================================
     * CONSTRUTOR
     * ==================================================
     */

    public CadastroUsuarioDTO() {
    }


    /*
     * ==================================================
     * GETTERS E SETTERS
     * ==================================================
     */

    public String getNome() {

        return nome;
    }


    public void setNome(
            String nome) {

        this.nome =
                nome;
    }


    public String getLogin() {

        return login;
    }


    public void setLogin(
            String login) {

        this.login =
                login;
    }


    public String getEmail() {

        return email;
    }


    public void setEmail(
            String email) {

        this.email =
                email;
    }


    public PerfilUsuario getPerfil() {

        return perfil;
    }


    public void setPerfil(
            PerfilUsuario perfil) {

        this.perfil =
                perfil;
    }


    /*
     * Temporários.
     *
     * Serão removidos após a alteração
     * do UsuarioService.
     */

    public String getSenha() {

        return senha;
    }


    public void setSenha(
            String senha) {

        this.senha =
                senha;
    }


    public String getConfirmarSenha() {

        return confirmarSenha;
    }


    public void setConfirmarSenha(
            String confirmarSenha) {

        this.confirmarSenha =
                confirmarSenha;
    }
}