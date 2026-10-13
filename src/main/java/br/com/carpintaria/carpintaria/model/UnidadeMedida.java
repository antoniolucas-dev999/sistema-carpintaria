package br.com.carpintaria.carpintaria.model;

public enum UnidadeMedida {

    M2("m²"),
    ML("Metro linear"),
    M3("m³"),
    UN("Unidade"),
    PC("Peça"),
    CJ("Conjunto");

    private final String descricao;

    UnidadeMedida(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}