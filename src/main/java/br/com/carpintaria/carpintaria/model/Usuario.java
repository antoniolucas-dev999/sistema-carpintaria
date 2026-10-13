package br.com.carpintaria.carpintaria.model;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;


@Entity
@Table(name = "usuarios")
public class Usuario {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(
        nullable = false,
        length = 100
    )
    private String nome;


    /*
     * ==================================================
     * LOGIN
     * ==================================================
     */

    @Column(
        nullable = false,
        unique = true,
        length = 80
    )
    private String login;


    /*
     * ==================================================
     * E-MAIL
     * ==================================================
     *
     * O e-mail não é utilizado como login.
     *
     * Ele é opcional.
     */

    @Column(
        unique = true,
        length = 150
    )
    private String email;


    /*
     * ==================================================
     * SENHA
     * ==================================================
     */

    @Column(
        nullable = false,
        length = 255
    )
    private String senha;


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
     * FINANCEIRO
     * SUPERVISOR_PRODUCAO
     *
     * O perfil principal NÃO limita sozinho
     * os acessos do usuário.
     *
     * Os acessos adicionais ficam na coleção
     * "acessos".
     */

    @Enumerated(EnumType.STRING)
    @Column(
        nullable = false,
        length = 40
    )
    private PerfilUsuario perfil =
            PerfilUsuario.VENDEDOR;


    /*
     * ==================================================
     * ACESSOS DO USUÁRIO
     * ==================================================
     *
     * Um usuário pode possuir vários acessos.
     *
     * Exemplo:
     *
     * VENDEDOR
     * SUPERVISOR_PRODUCAO
     *
     * ao mesmo tempo.
     *
     * Os dados são armazenados na tabela:
     *
     * usuario_acessos
     *
     * O ADMIN não depende desta coleção.
     * O perfil ADMIN possui acesso total
     * automaticamente.
     */

    @ElementCollection(
        fetch = FetchType.EAGER
    )
    @CollectionTable(
        name = "usuario_acessos",
        joinColumns = @JoinColumn(
            name = "usuario_id"
        )
    )
    @Enumerated(EnumType.STRING)
    @Column(
        name = "acesso",
        nullable = false,
        length = 40
    )
    private Set<PerfilUsuario> acessos =
            new LinkedHashSet<>();


    /*
     * ==================================================
     * STATUS
     * ==================================================
     */

    @Column(
        name = "ativo"
    )
    private Boolean ativo = false;


    /*
     * ==================================================
     * TROCA OBRIGATÓRIA DE SENHA
     * ==================================================
     */

    @Column(
        name = "troca_senha_obrigatoria",
        nullable = false
    )
    private Boolean trocaSenhaObrigatoria = false;


    /*
     * ==================================================
     * FOTO DE PERFIL
     * ==================================================
     */

    @Column(
        name = "foto_perfil_path",
        length = 500
    )
    private String fotoPerfilPath;


    /*
     * ==================================================
     * DATAS
     * ==================================================
     */

    @Column(
        name = "criado_em",
        nullable = false
    )
    private OffsetDateTime criadoEm;


    @Column(
        name = "atualizado_em",
        nullable = false
    )
    private OffsetDateTime atualizadoEm;


    /*
     * ==================================================
     * CONSTRUTOR
     * ==================================================
     */

    public Usuario() {
    }


    /*
     * ==================================================
     * EVENTOS JPA
     * ==================================================
     */

    @PrePersist
    public void prePersist() {

        OffsetDateTime agora =
                OffsetDateTime.now();


        if (criadoEm == null) {

            criadoEm =
                    agora;
        }


        atualizadoEm =
                agora;
    }


    @PreUpdate
    public void preUpdate() {

        atualizadoEm =
                OffsetDateTime.now();
    }


    /*
     * ==================================================
     * GETTERS E SETTERS
     * ==================================================
     */

    public Long getId() {

        return id;
    }


    public void setId(
            Long id) {

        this.id =
                id;
    }


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


    public String getSenha() {

        return senha;
    }


    public void setSenha(
            String senha) {

        this.senha =
                senha;
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


    public Boolean getAtivo() {

        return ativo;
    }


    public void setAtivo(
            Boolean ativo) {

        this.ativo =
                ativo;
    }


    public Boolean getTrocaSenhaObrigatoria() {

        return trocaSenhaObrigatoria;
    }


    public void setTrocaSenhaObrigatoria(
            Boolean trocaSenhaObrigatoria) {

        this.trocaSenhaObrigatoria =
                trocaSenhaObrigatoria;
    }


    public String getFotoPerfilPath() {

        return fotoPerfilPath;
    }


    public void setFotoPerfilPath(
            String fotoPerfilPath) {

        this.fotoPerfilPath =
                fotoPerfilPath;
    }


    public OffsetDateTime getCriadoEm() {

        return criadoEm;
    }


    public void setCriadoEm(
            OffsetDateTime criadoEm) {

        this.criadoEm =
                criadoEm;
    }


    public OffsetDateTime getAtualizadoEm() {

        return atualizadoEm;
    }


    public void setAtualizadoEm(
            OffsetDateTime atualizadoEm) {

        this.atualizadoEm =
                atualizadoEm;
    }


    /*
     * ==================================================
     * MÉTODOS AUXILIARES
     * ==================================================
     */

    public boolean estaAtivo() {

        return ativo == null
                || Boolean.TRUE.equals(
                    ativo
                );
    }


    public boolean precisaTrocarSenha() {

        return Boolean.TRUE.equals(
            trocaSenhaObrigatoria
        );
    }


    public boolean possuiFotoPerfil() {

        return fotoPerfilPath != null
                && !fotoPerfilPath.isBlank();
    }


    /*
     * ==================================================
     * VERIFICAR ACESSO
     * ==================================================
     *
     * ADMIN:
     *
     * possui acesso total automaticamente.
     *
     * Usuários comuns:
     *
     * precisam possuir o acesso na tabela
     * usuario_acessos.
     */

    public boolean possuiAcesso(
            PerfilUsuario acesso) {


        if (acesso == null) {

            return false;
        }


        /*
         * Somente quem possui perfil principal
         * ADMIN é considerado administrador.
         *
         * Colocar ADMIN na coleção de acessos
         * não transforma alguém em administrador.
         */

        if (acesso == PerfilUsuario.ADMIN) {

            return perfil
                    == PerfilUsuario.ADMIN;
        }


        /*
         * Administrador possui todos
         * os acessos operacionais.
         */

        if (perfil == PerfilUsuario.ADMIN) {

            return true;
        }


        return acessos != null
                && acessos.contains(
                    acesso
                );
    }
}