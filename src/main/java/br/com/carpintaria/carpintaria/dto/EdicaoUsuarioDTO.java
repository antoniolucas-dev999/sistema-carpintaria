package br.com.carpintaria.carpintaria.dto;

import java.util.LinkedHashSet;
import java.util.Set;

import br.com.carpintaria.carpintaria.model.PerfilUsuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public class EdicaoUsuarioDTO {


    @NotBlank(
        message = "O nome é obrigatório."
    )
    @Size(
        min = 3,
        max = 100,
        message = "O nome deve ter entre 3 e 100 caracteres."
    )
    private String nome;


    @NotBlank(
        message = "O login é obrigatório."
    )
    @Size(
        min = 3,
        max = 80,
        message = "O login deve ter entre 3 e 80 caracteres."
    )
    @Pattern(
        regexp = "^[A-Za-z0-9._-]+$",
        message = "O login pode conter apenas letras, números, ponto, hífen e underline."
    )
    private String login;


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
     * PERFIL PRINCIPAL
     * ==================================================
     *
     * Representa a função principal do usuário.
     *
     * Exemplo:
     *
     * VENDEDOR
     *
     * Mesmo sendo VENDEDOR, o usuário poderá
     * possuir acesso também ao FINANCEIRO,
     * SUPERVISOR_PRODUCAO etc.
     */

    @NotNull(
        message = "Selecione o perfil principal do usuário."
    )
    private PerfilUsuario perfil;


    /*
     * ==================================================
     * ACESSOS
     * ==================================================
     *
     * Um mesmo usuário pode possuir vários
     * acessos simultaneamente.
     *
     * Exemplo:
     *
     * VENDEDOR
     * FINANCEIRO
     * SUPERVISOR_PRODUCAO
     *
     * A validação de pelo menos um acesso
     * será feita no serviço, pois o perfil
     * ADMIN possui tratamento especial e
     * recebe acesso total automaticamente.
     */

    private Set<PerfilUsuario> acessos =
            new LinkedHashSet<>();


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


    public Set<PerfilUsuario> getAcessos() {

        return acessos;
    }


    public void setAcessos(
            Set<PerfilUsuario> acessos) {


        this.acessos.clear();


        if (acessos != null) {

            this.acessos.addAll(
                acessos
            );
        }
    }
}