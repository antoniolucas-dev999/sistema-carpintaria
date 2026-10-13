package br.com.carpintaria.carpintaria.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.carpintaria.carpintaria.model.Cliente;
import br.com.carpintaria.carpintaria.model.CriterioVenda;
import br.com.carpintaria.carpintaria.model.Orcamento;
import br.com.carpintaria.carpintaria.model.OrcamentoItem;
import br.com.carpintaria.carpintaria.model.Pedido;
import br.com.carpintaria.carpintaria.model.PedidoItem;
import br.com.carpintaria.carpintaria.model.StatusPedido;
import br.com.carpintaria.carpintaria.model.UnidadeMedida;
import br.com.carpintaria.carpintaria.model.Usuario;
import br.com.carpintaria.carpintaria.repository.PedidoItemRepository;
import br.com.carpintaria.carpintaria.repository.PedidoRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final ClienteService clienteService;
    private final UsuarioService usuarioService;

    public PedidoService(
            PedidoRepository pedidoRepository,
            PedidoItemRepository pedidoItemRepository,
            ClienteService clienteService,
            UsuarioService usuarioService) {

        this.pedidoRepository = pedidoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
    }


    /*
     * ==================================================
     * CRIAR PEDIDO MANUALMENTE
     * ==================================================
     */

    @Transactional
    public Pedido criar(
            Long clienteId,
            Long vendedorId,
            String obra,
            String observacoes) {

        Cliente cliente =
                clienteService.buscarPorId(
                    clienteId
                );

        Usuario vendedor =
                usuarioService.buscarPorId(
                    vendedorId
                );

        Pedido pedido =
                new Pedido();

        pedido.setCliente(
            cliente
        );

        pedido.setVendedor(
            vendedor
        );

        pedido.setObra(
            normalizarTexto(
                obra
            )
        );

        pedido.setObservacoes(
            normalizarTexto(
                observacoes
            )
        );

        pedido.setStatus(
            StatusPedido.RASCUNHO
        );


        /*
         * PRIMEIRO SALVAMENTO
         *
         * Gera o ID do pedido.
         */
        pedido =
                pedidoRepository.save(
                    pedido
                );


        /*
         * NÚMERO DO PEDIDO
         *
         * Exemplo:
         * PED-000001
         */
        String numero =
                String.format(
                    "PED-%06d",
                    pedido.getId()
                );

        pedido.setNumero(
            numero
        );


        return pedidoRepository.save(
            pedido
        );
    }


    /*
     * ==================================================
     * CRIAR PEDIDO A PARTIR DE ORÇAMENTO
     * ==================================================
     *
     * Esta operação formaliza um orçamento como pedido.
     *
     * O pedido nasce diretamente em:
     *
     * AGUARDANDO_FINANCEIRO
     *
     * O orçamento original continua armazenado e fica
     * vinculado ao pedido para manter todo o histórico
     * comercial.
     * ==================================================
     */

    @Transactional
    public Pedido criarAPartirDoOrcamento(
            Orcamento orcamento) {

        if (orcamento == null) {

            throw new IllegalArgumentException(
                "Orçamento inválido."
            );
        }


        if (orcamento.getId() == null) {

            throw new IllegalArgumentException(
                "O orçamento precisa estar salvo antes de ser convertido em pedido."
            );
        }


        /*
         * PROTEÇÃO CONTRA DUPLICIDADE
         *
         * Se este orçamento já tiver gerado um pedido,
         * devolvemos o pedido existente.
         */
        var pedidoExistente =
                pedidoRepository.findByOrcamento_Id(
                    orcamento.getId()
                );

        if (pedidoExistente.isPresent()) {

            return pedidoExistente.get();
        }


        /*
         * VALIDAÇÕES
         */
        if (orcamento.getCliente() == null) {

            throw new IllegalArgumentException(
                "Selecione o cliente antes de confirmar o orçamento."
            );
        }


        if (orcamento.getVendedor() == null) {

            throw new IllegalStateException(
                "O orçamento não possui vendedor associado."
            );
        }


        if (orcamento.getItens() == null
                || orcamento.getItens().isEmpty()) {

            throw new IllegalArgumentException(
                "Adicione pelo menos um item antes de confirmar o orçamento."
            );
        }


        /*
         * CRIAR O PEDIDO
         */
        Pedido pedido =
                new Pedido();

        pedido.setCliente(
            orcamento.getCliente()
        );

        pedido.setVendedor(
            orcamento.getVendedor()
        );

        pedido.setOrcamento(
            orcamento
        );

        pedido.setObra(
            normalizarTexto(
                orcamento.getObra()
            )
        );

        pedido.setObservacoes(
            normalizarTexto(
                orcamento.getObservacoes()
            )
        );

        pedido.setTipoMadeira(
            normalizarTexto(
                orcamento.getTipoMadeira()
            )
        );

        pedido.setSubtotal(
            valorOuZero(orcamento.getSubtotal())
        );

        pedido.setPercentualFrete(
            valorOuZero(orcamento.getPercentualFrete())
        );

        pedido.setValorFrete(
            valorOuZero(orcamento.getValorFrete())
        );

        pedido.setPercentualDesconto(
            valorOuZero(orcamento.getPercentualDesconto())
        );

        pedido.setValorDesconto(
            valorOuZero(orcamento.getValorDesconto())
        );

        pedido.setTotal(
            valorOuZero(orcamento.getTotal())
        );

        pedido.setStatus(
            StatusPedido.AGUARDANDO_FINANCEIRO
        );


        /*
         * PRIMEIRO SALVAMENTO
         *
         * Gera o ID do pedido.
         */
        pedido =
                pedidoRepository.save(
                    pedido
                );


        /*
         * GERAR NÚMERO DO PEDIDO
         *
         * Exemplo:
         * PED-000001
         */
        String numero =
                String.format(
                    "PED-%06d",
                    pedido.getId()
                );

        pedido.setNumero(
            numero
        );

        pedido =
                pedidoRepository.save(
                    pedido
                );


        /*
         * COPIAR ITENS DO ORÇAMENTO
         */
        List<PedidoItem> itensPedido =
                new ArrayList<>();

        for (OrcamentoItem itemOrcamento :
                orcamento.getItens()) {

            PedidoItem itemPedido =
                    criarItemDoPedido(
                        pedido,
                        itemOrcamento
                    );

            itensPedido.add(
                itemPedido
            );
        }


        pedidoItemRepository.saveAll(
            itensPedido
        );


        return pedido;
    }


    /*
     * ==================================================
     * CRIAR ITEM DO PEDIDO A PARTIR DO ITEM DO ORÇAMENTO
     * ==================================================
     */

    private PedidoItem criarItemDoPedido(
            Pedido pedido,
            OrcamentoItem itemOrcamento) {

        if (itemOrcamento == null) {

            throw new IllegalArgumentException(
                "Foi encontrado um item inválido no orçamento."
            );
        }


        PedidoItem itemPedido =
                new PedidoItem();

        itemPedido.setPedido(
            pedido
        );

        itemPedido.setProduto(
            itemOrcamento.getProduto()
        );

        itemPedido.setDivisoria(
            itemOrcamento.getDivisoria()
        );

        itemPedido.setAmbiente(
            itemOrcamento.getAmbiente()
        );

        itemPedido.setAcionamento(
            itemOrcamento.getAcionamento()
        );

        itemPedido.setUnidadeMedida(
            mapearUnidadeMedida(
                itemOrcamento.getCriterioVenda()
            )
        );

        itemPedido.setQuantidadePedida(
            calcularQuantidadePedida(
                itemOrcamento
            )
        );

        /*
         * REGRA COMERCIAL
         *
         * Até 10% de tolerância máxima para
         * produção/entrega.
         *
         * Esta tolerância NÃO é usada na comparação
         * futura da medição em obra.
         */
        itemPedido.setPercentualTolerancia(
            new BigDecimal("10.00")
        );

        itemPedido.setOrcamentoItemId(
            itemOrcamento.getId()
        );

        itemPedido.setCriterioVenda(
            itemOrcamento.getCriterioVenda()
        );

        itemPedido.setQuantidadeComercial(
            itemOrcamento.getQuantidade()
        );

        itemPedido.setMedidaCalculada(
            itemOrcamento.getMedidaCalculada()
        );

        itemPedido.setPrecoUnitario(
            itemOrcamento.getPrecoUnitario()
        );

        itemPedido.setValorTotal(
            itemOrcamento.getValorTotal()
        );

        itemPedido.setComprimento(
            itemOrcamento.getComprimento()
        );

        itemPedido.setLargura(
            itemOrcamento.getLargura()
        );

        itemPedido.setEspessura(
            itemOrcamento.getEspessura()
        );


        return itemPedido;
    }


    /*
     * ==================================================
     * MAPEAR CRITÉRIO DE VENDA
     * PARA UNIDADE DO PEDIDO
     * ==================================================
     */

    private UnidadeMedida mapearUnidadeMedida(
            CriterioVenda criterioVenda) {

        if (criterioVenda == null) {

            throw new IllegalArgumentException(
                "O item do orçamento não possui critério de venda."
            );
        }


        return switch (criterioVenda) {

            case M2 ->
                UnidadeMedida.M2;

            case ML ->
                UnidadeMedida.ML;

            case M3 ->
                UnidadeMedida.M3;

            case UNIDADE ->
                UnidadeMedida.UN;

            case PECA ->
                UnidadeMedida.PC;

            case CONJUNTO ->
                UnidadeMedida.CJ;
        };
    }


    /*
     * ==================================================
     * CALCULAR QUANTIDADE PEDIDA
     * ==================================================
     *
     * ML / M2 / M3:
     *
     * quantidadePedida recebe a medida total vendida.
     *
     * Exemplo:
     *
     * 10 peças x 2,50 m
     * = 25,0000 ML
     *
     *
     * UNIDADE / PEÇA / CONJUNTO:
     *
     * quantidadePedida recebe a quantidade de peças.
     * ==================================================
     */

    private BigDecimal calcularQuantidadePedida(
            OrcamentoItem itemOrcamento) {

        CriterioVenda criterio =
                itemOrcamento.getCriterioVenda();


        if (criterio == null) {

            throw new IllegalArgumentException(
                "O item do orçamento não possui critério de venda."
            );
        }


        if (criterio == CriterioVenda.ML
                || criterio == CriterioVenda.M2
                || criterio == CriterioVenda.M3) {

            BigDecimal medidaCalculada =
                    itemOrcamento.getMedidaCalculada();

            if (medidaCalculada == null) {

                throw new IllegalStateException(
                    "O item do orçamento não possui medida calculada."
                );
            }


            if (medidaCalculada.compareTo(
                    BigDecimal.ZERO
                ) <= 0) {

                throw new IllegalStateException(
                    "A medida calculada do item deve ser maior que zero."
                );
            }


            return medidaCalculada.setScale(
                4,
                RoundingMode.HALF_UP
            );
        }


        Integer quantidade =
                itemOrcamento.getQuantidade();


        if (quantidade == null
                || quantidade <= 0) {

            throw new IllegalStateException(
                "A quantidade do item deve ser maior que zero."
            );
        }


        return BigDecimal
                .valueOf(
                    quantidade.longValue()
                )
                .setScale(
                    4,
                    RoundingMode.UNNECESSARY
                );
    }


    /*
     * ==================================================
     * LISTAR TODOS
     * ==================================================
     */

    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {

        return pedidoRepository
                .findAllByOrderByDataPedidoDescIdDesc();
    }


    /*
     * ==================================================
     * BUSCAR POR ID
     * ==================================================
     */

    @Transactional(readOnly = true)
    public Pedido buscarPorId(
            Long id) {

        return pedidoRepository
                .findById(
                    id
                )
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Pedido não encontrado."
                    )
                );
    }


    /*
     * ==================================================
     * BUSCAR PEDIDO PELO ORÇAMENTO
     * ==================================================
     */

    @Transactional(readOnly = true)
    public Pedido buscarPorOrcamentoId(
            Long orcamentoId) {

        return pedidoRepository
                .findByOrcamento_Id(
                    orcamentoId
                )
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Nenhum pedido foi encontrado para este orçamento."
                    )
                );
    }


    /*
     * ==================================================
     * VERIFICAR SE ORÇAMENTO JÁ POSSUI PEDIDO
     * ==================================================
     */

    @Transactional(readOnly = true)
    public boolean existePedidoParaOrcamento(
            Long orcamentoId) {

        if (orcamentoId == null) {
            return false;
        }

        return pedidoRepository
                .existsByOrcamento_Id(
                    orcamentoId
                );
    }


    /*
     * ==================================================
     * LISTAR ITENS DO PEDIDO
     * ==================================================
     */

    @Transactional(readOnly = true)
    public List<PedidoItem> listarItens(
            Long pedidoId) {

        return pedidoItemRepository
                .findByPedido_IdOrderByIdAsc(
                    pedidoId
                );
    }


    /*
     * ==================================================
     * SALVAR PEDIDO
     * ==================================================
     */

    @Transactional
    public Pedido salvar(
            Pedido pedido) {

        return pedidoRepository.save(
            pedido
        );
    }


    /*
     * ==================================================
     * NORMALIZAR TEXTO OPCIONAL
     * ==================================================
     */

    private BigDecimal valorOuZero(
            BigDecimal valor) {

        return valor == null
                ? BigDecimal.ZERO
                : valor;
    }


    private String normalizarTexto(
            String texto) {

        if (texto == null) {
            return null;
        }

        String textoNormalizado =
                texto.trim();

        if (textoNormalizado.isEmpty()) {
            return null;
        }

        return textoNormalizado;
    }
}