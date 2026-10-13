package br.com.carpintaria.carpintaria.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "orcamento_itens")
public class OrcamentoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /*
     * ORÇAMENTO
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Orcamento orcamento;

    @Column(nullable = false, length = 200)
    private String produto;

        /*
    * DIVISÓRIA / SEÇÃO
    */
    @Column(length = 100)
    private String divisoria;


    /*
     * CAMPOS OPCIONAIS
     */
    @Column(length = 100)
    private String ambiente;

    @Column(length = 100)
    private String acionamento;


    /*
     * CRITÉRIO DE CÁLCULO
     *
     * ML
     * M2
     * M3
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CriterioVenda criterioVenda;


    /*
     * QUANTIDADE
     */
    @Column(nullable = false)
    private Integer quantidade;


    /*
     * MEDIDAS
     */
    @Column(precision = 12, scale = 4)
    private BigDecimal comprimento;

    @Column(precision = 12, scale = 4)
    private BigDecimal largura;

    @Column(precision = 12, scale = 4)
    private BigDecimal espessura;


    /*
     * RESULTADO DO CÁLCULO
     */
    @Column(
        nullable = false,
        precision = 14,
        scale = 6
    )
    private BigDecimal medidaCalculada;


    /*
     * PREÇO POR:
     *
     * ML
     * M²
     * M³
     */
    @Column(
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal precoUnitario;


    /*
     * VALOR TOTAL DO ITEM
     */
    @Column(
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal valorTotal;


    public OrcamentoItem() {
    }

        public String getDivisoria() {
        return divisoria;
    }


    public void setDivisoria(String divisoria) {
        this.divisoria = divisoria;
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Orcamento getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(
            Orcamento orcamento) {

        this.orcamento = orcamento;
    }


    public String getProduto() {
        return produto;
    }

    public void setProduto(
            String produto) {

        this.produto = produto;
    }


    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(
            String ambiente) {

        this.ambiente = ambiente;
    }


    public String getAcionamento() {
        return acionamento;
    }

    public void setAcionamento(
            String acionamento) {

        this.acionamento = acionamento;
    }


    public CriterioVenda getCriterioVenda() {
        return criterioVenda;
    }

    public void setCriterioVenda(
            CriterioVenda criterioVenda) {

        this.criterioVenda = criterioVenda;
    }


    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(
            Integer quantidade) {

        this.quantidade = quantidade;
    }


    public BigDecimal getComprimento() {
        return comprimento;
    }

    public void setComprimento(
            BigDecimal comprimento) {

        this.comprimento = comprimento;
    }


    public BigDecimal getLargura() {
        return largura;
    }

    public void setLargura(
            BigDecimal largura) {

        this.largura = largura;
    }


    public BigDecimal getEspessura() {
        return espessura;
    }

    public void setEspessura(
            BigDecimal espessura) {

        this.espessura = espessura;
    }


    public BigDecimal getMedidaCalculada() {
        return medidaCalculada;
    }

    public void setMedidaCalculada(
            BigDecimal medidaCalculada) {

        this.medidaCalculada =
                medidaCalculada;
    }


    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(
            BigDecimal precoUnitario) {

        this.precoUnitario =
                precoUnitario;
    }


    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(
            BigDecimal valorTotal) {

        this.valorTotal =
                valorTotal;
    }
}