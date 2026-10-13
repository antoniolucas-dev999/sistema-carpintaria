package br.com.carpintaria.carpintaria.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class AtualizacaoPerfilDTO {


    /*
     * ==================================================
     * NOME
     * ==================================================
     */

    @NotBlank(
        message = "O nome é obrigatório."
    )
    @Size(
        min = 3,
        max = 100,
        message = "O nome deve ter entre 3 e 100 caracteres."
    )
    private String nome;


    /*
     * ==================================================
     * E-MAIL
     * ==================================================
     *
     * O e-mail é opcional.
     *
     * Caso seja informado,
     * precisa possuir formato válido.
     */

    @Email(
        message = "Informe um e-mail válido."
    )
    @Size(
        max = 150,
        message = "O e-mail deve possuir no máximo 150 caracteres."
    )
    private String email;


    /*
     * ==================================================
     * NOVA SENHA
     * ==================================================
     *
     * A senha não é obrigatória.
     *
     * Se ficar vazia,
     * mantém a senha atual.
     */

    private String novaSenha;


    /*
     * ==================================================
     * CONFIRMAÇÃO DA NOVA SENHA
     * ==================================================
     *
     * Só será exigida pelo UsuarioService
     * quando uma nova senha for informada.
     */

    private String confirmarNovaSenha;


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


    public String getEmail() {

        return email;
    }


    public void setEmail(
            String email) {

        this.email =
                email;
    }


    public String getNovaSenha() {

        return novaSenha;
    }


    public void setNovaSenha(
            String novaSenha) {

        this.novaSenha =
                novaSenha;
    }


    public String getConfirmarNovaSenha() {

        return confirmarNovaSenha;
    }


    public void setConfirmarNovaSenha(
            String confirmarNovaSenha) {

        this.confirmarNovaSenha =
                confirmarNovaSenha;
    }
}