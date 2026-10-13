package br.com.carpintaria.carpintaria.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.carpintaria.carpintaria.dto.ResultadoCalculoProduto;
import br.com.carpintaria.carpintaria.model.Cliente;
import br.com.carpintaria.carpintaria.model.CriterioVenda;
import br.com.carpintaria.carpintaria.model.Orcamento;
import br.com.carpintaria.carpintaria.model.OrcamentoItem;
import br.com.carpintaria.carpintaria.model.Pedido;
import br.com.carpintaria.carpintaria.model.StatusOrcamento;
import br.com.carpintaria.carpintaria.model.Usuario;
import br.com.carpintaria.carpintaria.repository.OrcamentoItemRepository;
import br.com.carpintaria.carpintaria.repository.OrcamentoRepository;

@Service
public class OrcamentoService {

    private final OrcamentoRepository orcamentoRepository;
    private final OrcamentoItemRepository orcamentoItemRepository;
    private final ClienteService clienteService;
    private final UsuarioService usuarioService;
    private final CalculoProdutoService calculoProdutoService;
    private final PedidoService pedidoService;

    public OrcamentoService(
            OrcamentoRepository orcamentoRepository,
            OrcamentoItemRepository orcamentoItemRepository,
            ClienteService clienteService,
            UsuarioService usuarioService,
            CalculoProdutoService calculoProdutoService,
            PedidoService pedidoService) {

        this.orcamentoRepository = orcamentoRepository;
        this.orcamentoItemRepository = orcamentoItemRepository;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
        this.calculoProdutoService = calculoProdutoService;
        this.pedidoService = pedidoService;
    }


    /*
     * ==================================================
     * CRIAR RASCUNHO
     * ==================================================
     */

    @Transactional
    public Orcamento criarRascunho(
            String loginVendedor) {

        if (loginVendedor == null
                || loginVendedor.isBlank()) {

            throw new IllegalArgumentException(
                "Não foi possível identificar o vendedor."
            );
        }

        Usuario vendedor =
                usuarioService.buscarPorLogin(
                    loginVendedor.trim()
                );

        Orcamento orcamento =
                criarOrcamentoBase(
                    vendedor
                );

        return salvarNovoOrcamento(
            orcamento
        );
    }


    /*
     * ==================================================
     * CRIAR ORÇAMENTO
     * ==================================================
     */

    @Transactional
    public Orcamento criar(
            Long clienteId,
            Long vendedorId,
            String obra,
            String tipoMadeira,
            String observacoes) {

        if (vendedorId == null) {

            throw new IllegalArgumentException(
                "Informe o vendedor."
            );
        }

        Usuario vendedor =
                usuarioService.buscarPorId(
                    vendedorId
                );

        Cliente cliente = null;

        if (clienteId != null) {

            cliente =
                    clienteService.buscarPorId(
                        clienteId
                    );
        }

        Orcamento orcamento =
                criarOrcamentoBase(
                    vendedor
                );

        orcamento.setCliente(
            cliente
        );

        orcamento.setObra(
            normalizarTexto(
                obra
            )
        );

        orcamento.setTipoMadeira(
            normalizarTexto(
                tipoMadeira
            )
        );

        orcamento.setObservacoes(
            normalizarTexto(
                observacoes
            )
        );

        return salvarNovoOrcamento(
            orcamento
        );
    }


    /*
     * ==================================================
     * CRIAR BASE
     * ==================================================
     */

    private Orcamento criarOrcamentoBase(
            Usuario vendedor) {

        Orcamento orcamento =
                new Orcamento();

        orcamento.setVendedor(
            vendedor
        );

        orcamento.setStatus(
            StatusOrcamento.RASCUNHO
        );

        orcamento.setSubtotal(
            BigDecimal.ZERO
        );

        orcamento.setPercentualFrete(
            new BigDecimal("5.00")
        );

        orcamento.setValorFrete(
            BigDecimal.ZERO
        );

        orcamento.setPercentualDesconto(
            BigDecimal.ZERO
        );

        orcamento.setValorDesconto(
            BigDecimal.ZERO
        );

        orcamento.setTotal(
            BigDecimal.ZERO
        );

        return orcamento;
    }


    /*
     * ==================================================
     * SALVAR NOVO ORÇAMENTO
     * ==================================================
     */

    private Orcamento salvarNovoOrcamento(
            Orcamento orcamento) {

        Orcamento orcamentoSalvo =
                orcamentoRepository.save(
                    orcamento
                );

        String numero =
                String.format(
                    "ORC-%06d",
                    orcamentoSalvo.getId()
                );

        orcamentoSalvo.setNumero(
            numero
        );

        return orcamentoRepository.save(
            orcamentoSalvo
        );
    }


    /*
     * ==================================================
     * LISTAR
     * ==================================================
     */

    @Transactional(readOnly = true)
    public List<Orcamento> listarTodos() {

        return orcamentoRepository.findAll();
    }


    /*
     * ==================================================
     * BUSCAR POR ID
     * ==================================================
     */

    @Transactional(readOnly = true)
    public Orcamento buscarPorId(
            Long id) {

        return orcamentoRepository
                .findById(
                    id
                )
                .orElseThrow(() ->
                    new RuntimeException(
                        "Orçamento não encontrado."
                    )
                );
    }


    /*
     * ==================================================
     * PESQUISAR POR CLIENTE
     * ==================================================
     */

    @Transactional(readOnly = true)
    public List<Orcamento> pesquisarPorCliente(
            String nome) {

        if (nome == null
                || nome.isBlank()) {

            return listarTodos();
        }

        return orcamentoRepository
                .findByCliente_NomeRazaoSocialContainingIgnoreCase(
                    nome.trim()
                );
    }


    /*
     * ==================================================
     * BUSCAR ITEM
     * ==================================================
     */

    @Transactional(readOnly = true)
    public OrcamentoItem buscarItemPorId(
            Long itemId) {

        return orcamentoItemRepository
                .findById(
                    itemId
                )
                .orElseThrow(() ->
                    new RuntimeException(
                        "Item do orçamento não encontrado."
                    )
                );
    }


    /*
     * ==================================================
     * ATUALIZAR DADOS GERAIS
     * ==================================================
     */

    @Transactional
    public Orcamento atualizarDadosGerais(
            Long orcamentoId,
            Long clienteId,
            String obra,
            String tipoMadeira,
            String observacoes) {

        Orcamento orcamento =
                buscarPorId(
                    orcamentoId
                );

        validarOrcamentoEditavel(
            orcamento
        );

        Cliente cliente = null;

        if (clienteId != null) {

            cliente =
                    clienteService.buscarPorId(
                        clienteId
                    );
        }

        orcamento.setCliente(
            cliente
        );

        orcamento.setObra(
            normalizarTexto(
                obra
            )
        );

        orcamento.setTipoMadeira(
            normalizarTexto(
                tipoMadeira
            )
        );

        orcamento.setObservacoes(
            normalizarTexto(
                observacoes
            )
        );

        marcarAtualizacao(
            orcamento
        );

        return orcamentoRepository.save(
            orcamento
        );
    }


    /*
     * ==================================================
     * ADICIONAR ITEM
     * ==================================================
     */

    @Transactional
    public Orcamento adicionarItem(
            Long orcamentoId,
            String produto,
            String divisoria,
            String ambiente,
            String acionamento,
            CriterioVenda criterioVenda,
            Integer quantidade,
            BigDecimal comprimento,
            BigDecimal largura,
            BigDecimal espessura,
            BigDecimal precoUnitario) {

        Orcamento orcamento =
                buscarPorId(
                    orcamentoId
                );

        validarOrcamentoEditavel(
            orcamento
        );

        validarNomeProduto(
            produto
        );

        ResultadoCalculoProduto resultado =
                calculoProdutoService.calcular(
                    criterioVenda,
                    comprimento,
                    largura,
                    espessura,
                    quantidade,
                    precoUnitario
                );

        OrcamentoItem item =
                new OrcamentoItem();

        item.setProduto(
            produto.trim()
        );

        item.setDivisoria(
            normalizarTexto(
                divisoria
            )
        );

        item.setAmbiente(
            normalizarTexto(
                ambiente
            )
        );

        item.setAcionamento(
            normalizarTexto(
                acionamento
            )
        );

        item.setCriterioVenda(
            criterioVenda
        );

        item.setQuantidade(
            quantidade
        );

        item.setComprimento(
            comprimento
        );

        item.setLargura(
            largura
        );

        item.setEspessura(
            espessura
        );

        item.setPrecoUnitario(
            precoUnitario
        );

        item.setMedidaCalculada(
            resultado.medidaCalculada()
        );

        item.setValorTotal(
            resultado.valorTotal()
        );

        orcamento.adicionarItem(
            item
        );

        recalcularTotais(
            orcamento
        );

        marcarAtualizacao(
            orcamento
        );

        return orcamentoRepository.save(
            orcamento
        );
    }


    /*
     * ==================================================
     * EDITAR ITEM
     * ==================================================
     */

    @Transactional
    public Orcamento editarItem(
            Long orcamentoId,
            Long itemId,
            String produto,
            String divisoria,
            String ambiente,
            String acionamento,
            CriterioVenda criterioVenda,
            Integer quantidade,
            BigDecimal comprimento,
            BigDecimal largura,
            BigDecimal espessura,
            BigDecimal precoUnitario) {

        Orcamento orcamento =
                buscarPorId(
                    orcamentoId
                );

        validarOrcamentoEditavel(
            orcamento
        );

        OrcamentoItem item =
                buscarItemPorId(
                    itemId
                );

        validarItemDoOrcamento(
            orcamentoId,
            item
        );

        validarNomeProduto(
            produto
        );

        ResultadoCalculoProduto resultado =
                calculoProdutoService.calcular(
                    criterioVenda,
                    comprimento,
                    largura,
                    espessura,
                    quantidade,
                    precoUnitario
                );

        item.setProduto(
            produto.trim()
        );

        item.setDivisoria(
            normalizarTexto(
                divisoria
            )
        );

        item.setAmbiente(
            normalizarTexto(
                ambiente
            )
        );

        item.setAcionamento(
            normalizarTexto(
                acionamento
            )
        );

        item.setCriterioVenda(
            criterioVenda
        );

        item.setQuantidade(
            quantidade
        );

        item.setComprimento(
            comprimento
        );

        item.setLargura(
            largura
        );

        item.setEspessura(
            espessura
        );

        item.setPrecoUnitario(
            precoUnitario
        );

        item.setMedidaCalculada(
            resultado.medidaCalculada()
        );

        item.setValorTotal(
            resultado.valorTotal()
        );

        orcamentoItemRepository.save(
            item
        );

        recalcularTotais(
            orcamento
        );

        marcarAtualizacao(
            orcamento
        );

        return orcamentoRepository.save(
            orcamento
        );
    }


    /*
     * ==================================================
     * REMOVER ITEM
     * ==================================================
     */

    @Transactional
    public Orcamento removerItem(
            Long orcamentoId,
            Long itemId) {

        Orcamento orcamento =
                buscarPorId(
                    orcamentoId
                );

        validarOrcamentoEditavel(
            orcamento
        );

        OrcamentoItem item =
                buscarItemPorId(
                    itemId
                );

        validarItemDoOrcamento(
            orcamentoId,
            item
        );

        orcamento.removerItem(
            item
        );

        recalcularTotais(
            orcamento
        );

        marcarAtualizacao(
            orcamento
        );

        return orcamentoRepository.save(
            orcamento
        );
    }


    /*
     * ==================================================
     * ALTERAR FRETE
     * ==================================================
     */

    @Transactional
    public Orcamento alterarPercentualFrete(
            Long orcamentoId,
            BigDecimal percentualFrete) {

        Orcamento orcamento =
                buscarPorId(
                    orcamentoId
                );

        validarOrcamentoEditavel(
            orcamento
        );

        percentualFrete =
                normalizarPercentual(
                    percentualFrete,
                    "frete"
                );

        orcamento.setPercentualFrete(
            percentualFrete
        );

        recalcularTotais(
            orcamento
        );

        marcarAtualizacao(
            orcamento
        );

        return orcamentoRepository.save(
            orcamento
        );
    }


    /*
     * ==================================================
     * ALTERAR DESCONTO
     * ==================================================
     */

    @Transactional
    public Orcamento alterarPercentualDesconto(
            Long orcamentoId,
            BigDecimal percentualDesconto) {

        Orcamento orcamento =
                buscarPorId(
                    orcamentoId
                );

        validarOrcamentoEditavel(
            orcamento
        );

        percentualDesconto =
                normalizarPercentual(
                    percentualDesconto,
                    "desconto"
                );

        orcamento.setPercentualDesconto(
            percentualDesconto
        );

        recalcularTotais(
            orcamento
        );

        marcarAtualizacao(
            orcamento
        );

        return orcamentoRepository.save(
            orcamento
        );
    }


    /*
     * ==================================================
     * FORMALIZAR ORÇAMENTO COMO PEDIDO
     * ==================================================
     */

    @Transactional
    public Pedido formalizarComoPedido(
            Long orcamentoId) {

        Orcamento orcamento =
                buscarPorId(
                    orcamentoId
                );

        if (pedidoService.existePedidoParaOrcamento(
                orcamentoId
            )) {

            Pedido pedidoExistente =
                    pedidoService.buscarPorOrcamentoId(
                        orcamentoId
                    );

            if (!orcamento.foiConvertidoEmPedido()) {

                orcamento.setStatus(
                    StatusOrcamento.CONVERTIDO_EM_PEDIDO
                );

                if (orcamento.getConfirmadoEm() == null) {

                    orcamento.setConfirmadoEm(
                        OffsetDateTime.now()
                    );
                }

                marcarAtualizacao(
                    orcamento
                );

                orcamentoRepository.save(
                    orcamento
                );
            }

            return pedidoExistente;
        }

        if (orcamento.foiConvertidoEmPedido()) {

            throw new IllegalStateException(
                "Este orçamento está marcado como convertido, mas nenhum pedido vinculado foi encontrado."
            );
        }

        if (orcamento.estaCancelado()) {

            throw new IllegalStateException(
                "Este orçamento está cancelado e não pode ser convertido em pedido."
            );
        }

        if (!orcamento.estaEmRascunho()
                && !orcamento.estaConfirmado()) {

            throw new IllegalStateException(
                "Este orçamento não pode ser formalizado como pedido no status atual."
            );
        }

        if (orcamento.getCliente() == null) {

            throw new IllegalArgumentException(
                "Selecione o cliente antes de confirmar o orçamento."
            );
        }

        if (orcamento.getItens() == null
                || orcamento.getItens().isEmpty()) {

            throw new IllegalArgumentException(
                "Adicione pelo menos um item antes de confirmar o orçamento."
            );
        }

        recalcularTotais(
            orcamento
        );

        Pedido pedido =
                pedidoService.criarAPartirDoOrcamento(
                    orcamento
                );

        orcamento.setStatus(
            StatusOrcamento.CONVERTIDO_EM_PEDIDO
        );

        orcamento.setConfirmadoEm(
            OffsetDateTime.now()
        );

        marcarAtualizacao(
            orcamento
        );

        orcamentoRepository.save(
            orcamento
        );

        return pedido;
    }


    /*
     * ==================================================
     * CONFIRMAR
     * ==================================================
     */

    @Transactional
    public Orcamento confirmar(
            Long orcamentoId) {

        formalizarComoPedido(
            orcamentoId
        );

        return buscarPorId(
            orcamentoId
        );
    }


    /*
     * ==================================================
     * EXCLUIR ORÇAMENTO
     * ==================================================
     *
     * REGRA DEFINITIVA:
     *
     * SOMENTE orçamento em RASCUNHO pode ser excluído.
     *
     * Depois de formalizado como pedido, o orçamento
     * passa a fazer parte do histórico e nunca poderá
     * ser excluído por esta operação.
     * ==================================================
     */

    @Transactional
    public void excluir(
            Long orcamentoId) {

        Orcamento orcamento =
                buscarPorId(
                    orcamentoId
                );


        /*
         * Somente RASCUNHO pode ser excluído.
         */
        if (!orcamento.estaEmRascunho()) {

            throw new IllegalStateException(
                "Somente orçamentos em rascunho podem ser excluídos."
            );
        }


        /*
         * Proteção adicional contra inconsistência:
         *
         * Mesmo que o status esteja em RASCUNHO,
         * se existir um pedido relacionado, nunca
         * permitimos a exclusão.
         */
        if (pedidoService.existePedidoParaOrcamento(
                orcamentoId
            )) {

            throw new IllegalStateException(
                "Este orçamento possui um pedido vinculado e não pode ser excluído."
            );
        }


        orcamentoRepository.delete(
            orcamento
        );
    }


    /*
     * ==================================================
     * CANCELAR
     * ==================================================
     *
     * Mantido temporariamente para compatibilidade
     * com a tela atual.
     *
     * O nome visual será alterado para:
     *
     * EXCLUIR ORÇAMENTO
     * ==================================================
     */

    @Transactional
    public Orcamento cancelar(
            Long orcamentoId) {

        Orcamento orcamento =
                buscarPorId(
                    orcamentoId
                );

        excluir(
            orcamentoId
        );

        return orcamento;
    }


    /*
     * ==================================================
     * ALTERAR STATUS
     * ==================================================
     */

    @Transactional
    public Orcamento alterarStatus(
            Long id,
            StatusOrcamento status) {

        if (status == null) {

            throw new IllegalArgumentException(
                "Informe o status do orçamento."
            );
        }

        if (status == StatusOrcamento.CONFIRMADO) {

            return confirmar(
                id
            );
        }

        if (status == StatusOrcamento.CANCELADO) {

            return cancelar(
                id
            );
        }

        Orcamento orcamento =
                buscarPorId(
                    id
                );

        if (status == orcamento.getStatus()) {

            return orcamento;
        }

        if (status == StatusOrcamento.RASCUNHO) {

            throw new IllegalStateException(
                "Não é permitido alterar manualmente o orçamento para rascunho."
            );
        }

        if (status
                == StatusOrcamento.CONVERTIDO_EM_PEDIDO) {

            throw new IllegalStateException(
                "O orçamento somente pode ser marcado como convertido quando um pedido for realmente gerado."
            );
        }

        throw new IllegalStateException(
            "Transição de status não permitida."
        );
    }


    /*
     * ==================================================
     * VALIDAR SE PODE EDITAR
     * ==================================================
     */

    private void validarOrcamentoEditavel(
            Orcamento orcamento) {

        if (orcamento == null) {

            throw new IllegalArgumentException(
                "Orçamento inválido."
            );
        }

        if (orcamento.foiConvertidoEmPedido()) {

            throw new IllegalStateException(
                "Este orçamento já foi formalizado como pedido e não pode mais ser alterado."
            );
        }

        if (orcamento.estaCancelado()) {

            throw new IllegalStateException(
                "Este orçamento está cancelado e não pode mais ser alterado."
            );
        }

        if (!orcamento.estaEmRascunho()) {

            throw new IllegalStateException(
                "Este orçamento não está mais em rascunho e não pode ser alterado."
            );
        }
    }


    /*
     * ==================================================
     * VALIDAR ITEM DO ORÇAMENTO
     * ==================================================
     */

    private void validarItemDoOrcamento(
            Long orcamentoId,
            OrcamentoItem item) {

        if (item.getOrcamento() == null
                || item.getOrcamento().getId() == null
                || !item.getOrcamento()
                    .getId()
                    .equals(
                        orcamentoId
                    )) {

            throw new IllegalArgumentException(
                "O item não pertence a este orçamento."
            );
        }
    }


    /*
     * ==================================================
     * MARCAR ATUALIZAÇÃO
     * ==================================================
     */

    private void marcarAtualizacao(
            Orcamento orcamento) {

        orcamento.setAtualizadoEm(
            OffsetDateTime.now()
        );
    }


    /*
     * ==================================================
     * RECALCULAR TOTAIS
     * ==================================================
     */

    private void recalcularTotais(
            Orcamento orcamento) {

        BigDecimal subtotal =
                orcamento
                    .getItens()
                    .stream()
                    .map(
                        OrcamentoItem::getValorTotal
                    )
                    .filter(
                        valor -> valor != null
                    )
                    .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                    )
                    .setScale(
                        2,
                        RoundingMode.HALF_UP
                    );

        orcamento.setSubtotal(
            subtotal
        );

        BigDecimal percentualFrete =
                orcamento.getPercentualFrete();

        if (percentualFrete == null) {

            percentualFrete =
                    new BigDecimal("5.00");

            orcamento.setPercentualFrete(
                percentualFrete
            );
        }

        BigDecimal valorFrete =
                subtotal
                    .multiply(
                        percentualFrete
                    )
                    .divide(
                        BigDecimal.valueOf(
                            100
                        ),
                        2,
                        RoundingMode.HALF_UP
                    );

        orcamento.setValorFrete(
            valorFrete
        );

        BigDecimal totalAntesDesconto =
                subtotal
                    .add(
                        valorFrete
                    )
                    .setScale(
                        2,
                        RoundingMode.HALF_UP
                    );

        BigDecimal percentualDesconto =
                orcamento.getPercentualDesconto();

        if (percentualDesconto == null) {

            percentualDesconto =
                    BigDecimal.ZERO;

            orcamento.setPercentualDesconto(
                percentualDesconto
            );
        }

        BigDecimal valorDesconto =
                totalAntesDesconto
                    .multiply(
                        percentualDesconto
                    )
                    .divide(
                        BigDecimal.valueOf(
                            100
                        ),
                        2,
                        RoundingMode.HALF_UP
                    );

        orcamento.setValorDesconto(
            valorDesconto
        );

        BigDecimal total =
                totalAntesDesconto
                    .subtract(
                        valorDesconto
                    )
                    .setScale(
                        2,
                        RoundingMode.HALF_UP
                    );

        orcamento.setTotal(
            total
        );
    }


    /*
     * ==================================================
     * NORMALIZAR PERCENTUAL
     * ==================================================
     */

    private BigDecimal normalizarPercentual(
            BigDecimal percentual,
            String nomeCampo) {

        if (percentual == null) {

            return BigDecimal.ZERO;
        }

        if (percentual.compareTo(
                BigDecimal.ZERO
            ) < 0) {

            throw new IllegalArgumentException(
                "O percentual do "
                + nomeCampo
                + " não pode ser negativo."
            );
        }

        if (percentual.compareTo(
                BigDecimal.valueOf(
                    100
                )
            ) > 0) {

            throw new IllegalArgumentException(
                "O percentual do "
                + nomeCampo
                + " não pode ser maior que 100%."
            );
        }

        return percentual;
    }


    /*
     * ==================================================
     * VALIDAR PRODUTO
     * ==================================================
     */

    private void validarNomeProduto(
            String produto) {

        if (produto == null
                || produto.isBlank()) {

            throw new IllegalArgumentException(
                "Informe a descrição do item."
            );
        }
    }


    /*
     * ==================================================
     * NORMALIZAR TEXTO
     * ==================================================
     */

    private String normalizarTexto(
            String texto) {

        if (texto == null
                || texto.isBlank()) {

            return null;
        }

        return texto.trim();
    }


    /*
     * ==================================================
     * CONTAR ORÇAMENTOS
     * ==================================================
     */

    @Transactional(readOnly = true)
    public long contarOrcamentos() {

        return orcamentoRepository.count();
    }


    /*
     * ==================================================
     * TOTAL ORÇADO
     * ==================================================
     */

    @Transactional(readOnly = true)
    public BigDecimal calcularValorTotalOrcado() {

        BigDecimal total =
                orcamentoRepository
                    .somarValorTotalOrcamentos();

        if (total == null) {

            return BigDecimal.ZERO;
        }

        return total;
    }
}