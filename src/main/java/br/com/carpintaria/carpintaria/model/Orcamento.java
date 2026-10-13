package br.com.carpintaria.carpintaria.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "orcamentos")
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(
        unique = true,
        length = 30
    )
    private String numero;


    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = true
    )
    private Cliente cliente;


    @ManyToOne(
        fetch = FetchType.LAZY,
        optional = false
    )
    private Usuario vendedor;


    @Column(
        nullable = false
    )
    private LocalDate dataEmissao =
            LocalDate.now();


    @Column(
        length = 150
    )
    private String obra;


    @Column(
        length = 100
    )
    private String tipoMadeira;


    @Column(
        length = 1000
    )
    private String observacoes;


    @Column(
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal subtotal =
            BigDecimal.ZERO;


    /*
     * Todo novo orçamento começa
     * com frete padrão de 5%.
     */
    @Column(
        precision = 5,
        scale = 2
    )
    private BigDecimal percentualFrete =
            new BigDecimal("5.00");


    @Column(
        precision = 12,
        scale = 2
    )
    private BigDecimal valorFrete =
            BigDecimal.ZERO;


    @Column(
        precision = 5,
        scale = 2
    )
    private BigDecimal percentualDesconto =
            BigDecimal.ZERO;


    @Column(
        precision = 12,
        scale = 2
    )
    private BigDecimal valorDesconto =
            BigDecimal.ZERO;


    @Column(
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal total =
            BigDecimal.ZERO;


    @Enumerated(EnumType.STRING)
    @Column(
        nullable = false,
        length = 30
    )
    private StatusOrcamento status =
            StatusOrcamento.RASCUNHO;


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


    @Column(
        name = "confirmado_em"
    )
    private OffsetDateTime confirmadoEm;


    @OneToMany(
        mappedBy = "orcamento",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<OrcamentoItem> itens =
            new ArrayList<>();


    public Orcamento() {
    }


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


        if (dataEmissao == null) {

            dataEmissao =
                    LocalDate.now();
        }


        if (status == null) {

            status =
                    StatusOrcamento.RASCUNHO;
        }


        if (subtotal == null) {

            subtotal =
                    BigDecimal.ZERO;
        }


        /*
         * Se nenhum percentual tiver sido
         * informado em um novo orçamento,
         * assume 5%.
         */
        if (percentualFrete == null) {

            percentualFrete =
                    new BigDecimal("5.00");
        }


        if (valorFrete == null) {

            valorFrete =
                    BigDecimal.ZERO;
        }


        if (percentualDesconto == null) {

            percentualDesconto =
                    BigDecimal.ZERO;
        }


        if (valorDesconto == null) {

            valorDesconto =
                    BigDecimal.ZERO;
        }


        if (total == null) {

            total =
                    BigDecimal.ZERO;
        }
    }


    @PreUpdate
    public void preUpdate() {

        atualizadoEm =
                OffsetDateTime.now();
    }


    public void adicionarItem(
            OrcamentoItem item) {

        if (item == null) {

            return;
        }

        itens.add(
            item
        );

        item.setOrcamento(
            this
        );
    }


    public void removerItem(
            OrcamentoItem item) {

        if (item == null) {

            return;
        }

        itens.remove(
            item
        );

        item.setOrcamento(
            null
        );
    }


    public boolean estaEmRascunho() {

        return status
                == StatusOrcamento.RASCUNHO;
    }


    public boolean estaConfirmado() {

        return status
                == StatusOrcamento.CONFIRMADO;
    }


    public boolean foiConvertidoEmPedido() {

        return status
                == StatusOrcamento.CONVERTIDO_EM_PEDIDO;
    }


    public boolean estaCancelado() {

        return status
                == StatusOrcamento.CANCELADO;
    }


    public boolean podeSerEditado() {

        return estaEmRascunho();
    }


    public Long getId() {

        return id;
    }


    public void setId(
            Long id) {

        this.id =
                id;
    }


    public String getNumero() {

        return numero;
    }


    public void setNumero(
            String numero) {

        this.numero =
                numero;
    }


    public Cliente getCliente() {

        return cliente;
    }


    public void setCliente(
            Cliente cliente) {

        this.cliente =
                cliente;
    }


    public Usuario getVendedor() {

        return vendedor;
    }


    public void setVendedor(
            Usuario vendedor) {

        this.vendedor =
                vendedor;
    }


    public LocalDate getDataEmissao() {

        return dataEmissao;
    }


    public void setDataEmissao(
            LocalDate dataEmissao) {

        this.dataEmissao =
                dataEmissao;
    }


    public String getObra() {

        return obra;
    }


    public void setObra(
            String obra) {

        this.obra =
                obra;
    }


    public String getTipoMadeira() {

        return tipoMadeira;
    }


    public void setTipoMadeira(
            String tipoMadeira) {

        this.tipoMadeira =
                tipoMadeira;
    }


    public String getObservacoes() {

        return observacoes;
    }


    public void setObservacoes(
            String observacoes) {

        this.observacoes =
                observacoes;
    }


    public BigDecimal getSubtotal() {

        return subtotal;
    }


    public void setSubtotal(
            BigDecimal subtotal) {

        this.subtotal =
                subtotal;
    }


    public BigDecimal getPercentualFrete() {

        return percentualFrete;
    }


    public void setPercentualFrete(
            BigDecimal percentualFrete) {

        this.percentualFrete =
                percentualFrete;
    }


    public BigDecimal getValorFrete() {

        return valorFrete;
    }


    public void setValorFrete(
            BigDecimal valorFrete) {

        this.valorFrete =
                valorFrete;
    }


    public BigDecimal getPercentualDesconto() {

        return percentualDesconto;
    }


    public void setPercentualDesconto(
            BigDecimal percentualDesconto) {

        this.percentualDesconto =
                percentualDesconto;
    }


    public BigDecimal getValorDesconto() {

        return valorDesconto;
    }


    public void setValorDesconto(
            BigDecimal valorDesconto) {

        this.valorDesconto =
                valorDesconto;
    }


    public BigDecimal getTotal() {

        return total;
    }


    public void setTotal(
            BigDecimal total) {

        this.total =
                total;
    }


    public StatusOrcamento getStatus() {

        return status;
    }


    public void setStatus(
            StatusOrcamento status) {

        this.status =
                status;
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


    public OffsetDateTime getConfirmadoEm() {

        return confirmadoEm;
    }


    public void setConfirmadoEm(
            OffsetDateTime confirmadoEm) {

        this.confirmadoEm =
                confirmadoEm;
    }


    public List<OrcamentoItem> getItens() {

        return itens;
    }


    public void setItens(
            List<OrcamentoItem> itens) {

        this.itens.clear();

        if (itens != null) {

            for (OrcamentoItem item :
                    itens) {

                adicionarItem(
                    item
                );
            }
        }
    }
}