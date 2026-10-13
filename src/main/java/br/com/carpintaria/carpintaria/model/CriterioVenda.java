package br.com.carpintaria.carpintaria.model;


public enum CriterioVenda {


    /*
     * Venda por comprimento.
     */
    ML("Metro Linear"),


    /*
     * Venda por área.
     */
    M2("Metro Quadrado"),


    /*
     * Venda por volume.
     */
    M3("Metro Cúbico"),


    /*
     * Venda por unidade.
     *
     * Exemplo:
     * 5 unidades.
     */
    UNIDADE("Unidade"),


    /*
     * Venda por peça.
     *
     * Exemplo:
     * 3 peças.
     */
    PECA("Peça"),


    /*
     * Venda por conjunto.
     *
     * Exemplo:
     * 2 conjuntos.
     */
    CONJUNTO("Conjunto");


    private final String descricao;


    CriterioVenda(
            String descricao) {

        this.descricao =
                descricao;
    }


    public String getDescricao() {

        return descricao;
    }
}