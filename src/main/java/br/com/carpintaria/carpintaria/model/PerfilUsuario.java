package br.com.carpintaria.carpintaria.model;

public enum PerfilUsuario {

    ADMIN("Administrador"),

    VENDEDOR("Vendedor"),

    FINANCEIRO("Financeiro"),

    SUPERVISOR_PRODUCAO("Supervisor de Produção"),

    GERENTE_PRODUCAO("Gerente de Produção"),

    DIRETOR("Diretor");


    private final String descricao;


    PerfilUsuario(String descricao) {
        this.descricao = descricao;
    }


    public String getDescricao() {
        return descricao;
    }
}