package br.com.carpintaria.carpintaria.model;

public enum StatusPedido {

    RASCUNHO("Rascunho"),
    AGUARDANDO_FINANCEIRO("Aguardando financeiro"),
    FINANCEIRO_LIBERADO("Financeiro liberado"),
    FINANCEIRO_REPROVADO("Financeiro reprovado"),
    EM_ANALISE_TECNICA("Em análise técnica"),
    PENDENCIA_TECNICA("Pendência técnica"),
    LIBERADO_PRODUCAO("Liberado para produção"),
    EM_PRODUCAO("Em produção"),
    PRODUCAO_PARCIAL("Produção parcial"),
    PRODUCAO_CONCLUIDA("Produção concluída"),
    ENTREGA_PARCIAL("Entrega parcial"),
    ENTREGUE("Entregue"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}