package br.com.carpintaria.carpintaria.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "pedido_itens")
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(nullable = false, length = 200)
    private String produto;

    @Column(length = 500)
    private String descricao;

    @Column(length = 100)
    private String divisoria;

    @Column(length = 100)
    private String ambiente;

    @Column(length = 100)
    private String acionamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidade_medida", nullable = false, length = 20)
    private UnidadeMedida unidadeMedida;

    @Column(name = "quantidade_pedida", nullable = false, precision = 14, scale = 4)
    private BigDecimal quantidadePedida;

    @Column(name = "percentual_tolerancia", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentualTolerancia = new BigDecimal("10.00");

    @Column(precision = 12, scale = 4)
    private BigDecimal comprimento;

    @Column(precision = 12, scale = 4)
    private BigDecimal largura;

    @Column(precision = 12, scale = 4)
    private BigDecimal espessura;

    @Column(length = 500)
    private String observacoes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDivisoria() {
        return divisoria;
    }

    public void setDivisoria(String divisoria) {
        this.divisoria = divisoria;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    public String getAcionamento() {
        return acionamento;
    }

    public void setAcionamento(String acionamento) {
        this.acionamento = acionamento;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public BigDecimal getQuantidadePedida() {
        return quantidadePedida;
    }

    public void setQuantidadePedida(BigDecimal quantidadePedida) {
        this.quantidadePedida = quantidadePedida;
    }

    public BigDecimal getPercentualTolerancia() {
        return percentualTolerancia;
    }

    public void setPercentualTolerancia(BigDecimal percentualTolerancia) {
        this.percentualTolerancia = percentualTolerancia;
    }

    public BigDecimal getComprimento() {
        return comprimento;
    }

    public void setComprimento(BigDecimal comprimento) {
        this.comprimento = comprimento;
    }

    public BigDecimal getLargura() {
        return largura;
    }

    public void setLargura(BigDecimal largura) {
        this.largura = largura;
    }

    public BigDecimal getEspessura() {
        return espessura;
    }

    public void setEspessura(BigDecimal espessura) {
        this.espessura = espessura;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }


    /*
     * ==================================================
     * DADOS COMERCIAIS (cópia fiel do item do orçamento)
     * ==================================================
     */

    @Column(name = "orcamento_item_id")
    private Long orcamentoItemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "criterio_venda", length = 10)
    private CriterioVenda criterioVenda;

    @Column(name = "quantidade_comercial")
    private Integer quantidadeComercial;

    @Column(name = "medida_calculada", precision = 14, scale = 6)
    private BigDecimal medidaCalculada;

    @Column(name = "preco_unitario", precision = 12, scale = 2)
    private BigDecimal precoUnitario;

    @Column(name = "valor_total", precision = 12, scale = 2)
    private BigDecimal valorTotal;


    public Long getOrcamentoItemId() {
        return orcamentoItemId;
    }

    public void setOrcamentoItemId(Long orcamentoItemId) {
        this.orcamentoItemId = orcamentoItemId;
    }

    public CriterioVenda getCriterioVenda() {
        return criterioVenda;
    }

    public void setCriterioVenda(CriterioVenda criterioVenda) {
        this.criterioVenda = criterioVenda;
    }

    public Integer getQuantidadeComercial() {
        return quantidadeComercial;
    }

    public void setQuantidadeComercial(Integer quantidadeComercial) {
        this.quantidadeComercial = quantidadeComercial;
    }

    public BigDecimal getMedidaCalculada() {
        return medidaCalculada;
    }

    public void setMedidaCalculada(BigDecimal medidaCalculada) {
        this.medidaCalculada = medidaCalculada;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}