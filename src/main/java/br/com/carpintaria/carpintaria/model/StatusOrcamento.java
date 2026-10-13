package br.com.carpintaria.carpintaria.model;


public enum StatusOrcamento {


    /*
     * ==================================================
     * RASCUNHO
     * ==================================================
     *
     * O orçamento ainda está sendo preenchido.
     *
     * Pode ser alterado livremente.
     */

    RASCUNHO(
        "Rascunho"
    ),


    /*
     * ==================================================
     * CONFIRMADO
     * ==================================================
     *
     * O vendedor concluiu o orçamento.
     *
     * A proposta comercial está pronta
     * para posteriormente gerar um pedido.
     */

    CONFIRMADO(
        "Confirmado"
    ),


    /*
     * ==================================================
     * CONVERTIDO EM PEDIDO
     * ==================================================
     *
     * O orçamento já originou um pedido.
     *
     * Depois disso, o fluxo operacional
     * continuará pelo Pedido.
     */

    CONVERTIDO_EM_PEDIDO(
        "Convertido em pedido"
    ),


    /*
     * ==================================================
     * CANCELADO
     * ==================================================
     *
     * Orçamento cancelado e mantido apenas
     * para histórico.
     */

    CANCELADO(
        "Cancelado"
    );


    private final String descricao;


    StatusOrcamento(
            String descricao) {

        this.descricao =
                descricao;
    }


    public String getDescricao() {

        return descricao;
    }
}