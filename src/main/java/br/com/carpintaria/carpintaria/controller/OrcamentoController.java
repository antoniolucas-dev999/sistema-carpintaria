package br.com.carpintaria.carpintaria.controller;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.carpintaria.carpintaria.model.CriterioVenda;
import br.com.carpintaria.carpintaria.model.Orcamento;
import br.com.carpintaria.carpintaria.model.OrcamentoItem;
import br.com.carpintaria.carpintaria.model.Pedido;
import br.com.carpintaria.carpintaria.model.StatusOrcamento;
import br.com.carpintaria.carpintaria.service.ClienteService;
import br.com.carpintaria.carpintaria.service.OrcamentoService;
import br.com.carpintaria.carpintaria.service.UsuarioService;

@Controller
public class OrcamentoController {

    private final OrcamentoService orcamentoService;
    private final ClienteService clienteService;
    private final UsuarioService usuarioService;


    public OrcamentoController(
            OrcamentoService orcamentoService,
            ClienteService clienteService,
            UsuarioService usuarioService) {

        this.orcamentoService = orcamentoService;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
    }


    /*
     * ==================================================
     * LISTAR ORÇAMENTOS
     * ==================================================
     */

    @GetMapping("/orcamentos")
    public String listar(
            @RequestParam(required = false)
            String busca,
            Model model) {

        if (busca != null
                && !busca.isBlank()) {

            model.addAttribute(
                "orcamentos",
                orcamentoService.pesquisarPorCliente(
                    busca
                )
            );

        } else {

            model.addAttribute(
                "orcamentos",
                orcamentoService.listarTodos()
            );
        }

        model.addAttribute(
            "busca",
            busca
        );

        return "orcamentos/lista";
    }


    /*
     * ==================================================
     * NOVO ORÇAMENTO
     * ==================================================
     */

    @GetMapping("/orcamentos/novo")
    public String novo(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return "redirect:/login";
        }

        var orcamento =
                orcamentoService.criarRascunho(
                    authentication.getName()
                );

        return "redirect:/orcamentos/"
                + orcamento.getId();
    }


    /*
     * ==================================================
     * CRIAR ORÇAMENTO
     * ==================================================
     */

    @PostMapping("/orcamentos/criar")
    public String criar(
            @RequestParam
            Long clienteId,

            @RequestParam
            Long vendedorId,

            @RequestParam(required = false)
            String obra,

            @RequestParam(required = false)
            String tipoMadeira,

            @RequestParam(required = false)
            String observacoes) {

        var orcamento =
                orcamentoService.criar(
                    clienteId,
                    vendedorId,
                    obra,
                    tipoMadeira,
                    observacoes
                );

        return "redirect:/orcamentos/"
                + orcamento.getId();
    }


    /*
     * ==================================================
     * VISUALIZAR ORÇAMENTO
     * ==================================================
     */

    @GetMapping("/orcamentos/{id}")
    public String visualizar(
            @PathVariable
            Long id,
            Model model) {

        var orcamento =
                orcamentoService.buscarPorId(
                    id
                );

        model.addAttribute(
            "orcamento",
            orcamento
        );

        model.addAttribute(
            "clientes",
            clienteService.listarTodos()
        );

        model.addAttribute(
            "criteriosVenda",
            CriterioVenda.values()
        );

        model.addAttribute(
            "statusOrcamento",
            StatusOrcamento.values()
        );

        return "orcamentos/detalhes";
    }


    /*
     * ==================================================
     * ATUALIZAR DADOS GERAIS
     * ==================================================
     */

    @PostMapping(
        "/orcamentos/{id}/dados-gerais"
    )
    public String atualizarDadosGerais(
            @PathVariable
            Long id,

            @RequestParam(required = false)
            Long clienteId,

            @RequestParam(required = false)
            String obra,

            @RequestParam(required = false)
            String tipoMadeira,

            @RequestParam(required = false)
            String observacoes,

            RedirectAttributes redirectAttributes) {

        try {

            orcamentoService.atualizarDadosGerais(
                id,
                clienteId,
                obra,
                tipoMadeira,
                observacoes
            );

            redirectAttributes.addFlashAttribute(
                "sucesso",
                "Dados do orçamento salvos com sucesso."
            );

        } catch (
                IllegalArgumentException
                | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                "erro",
                e.getMessage()
            );
        }

        return "redirect:/orcamentos/"
                + id;
    }


    /*
     * ==================================================
     * ADICIONAR ITEM
     * ==================================================
     */

    @PostMapping(
        "/orcamentos/{id}/itens/adicionar"
    )
    public String adicionarItem(
            @PathVariable
            Long id,

            @RequestParam
            String produto,

            @RequestParam(required = false)
            String divisoria,

            @RequestParam(required = false)
            String ambiente,

            @RequestParam(required = false)
            String acionamento,

            @RequestParam
            CriterioVenda criterioVenda,

            @RequestParam
            Integer quantidade,

            @RequestParam(required = false)
            String comprimento,

            @RequestParam(required = false)
            String largura,

            @RequestParam(required = false)
            String espessura,

            @RequestParam
            String precoUnitario) {

        BigDecimal comprimentoDecimal =
                converterDecimalOpcional(
                    comprimento
                );

        BigDecimal larguraDecimal =
                converterDecimalOpcional(
                    largura
                );

        BigDecimal espessuraDecimal =
                converterDecimalOpcional(
                    espessura
                );

        BigDecimal precoDecimal =
                converterDecimalObrigatorio(
                    precoUnitario,
                    "Preço"
                );

        orcamentoService.adicionarItem(
            id,
            produto,
            divisoria,
            ambiente,
            acionamento,
            criterioVenda,
            quantidade,
            comprimentoDecimal,
            larguraDecimal,
            espessuraDecimal,
            precoDecimal
        );

        return "redirect:/orcamentos/"
                + id;
    }


    /*
     * ==================================================
     * ADICIONAR ITEM VIA AJAX
     * ==================================================
     */

    @PostMapping(
        "/orcamentos/{id}/itens/adicionar-ajax"
    )
    @ResponseBody
    public ResponseEntity<Map<String, Object>>
            adicionarItemAjax(

            @PathVariable
            Long id,

            @RequestParam
            String produto,

            @RequestParam(required = false)
            String divisoria,

            @RequestParam(required = false)
            String ambiente,

            @RequestParam(required = false)
            String acionamento,

            @RequestParam
            CriterioVenda criterioVenda,

            @RequestParam
            Integer quantidade,

            @RequestParam(required = false)
            String comprimento,

            @RequestParam(required = false)
            String largura,

            @RequestParam(required = false)
            String espessura,

            @RequestParam
            String precoUnitario) {

        Map<String, Object> resposta =
                new LinkedHashMap<>();

        try {

            BigDecimal comprimentoDecimal =
                    converterDecimalOpcional(
                        comprimento
                    );

            BigDecimal larguraDecimal =
                    converterDecimalOpcional(
                        largura
                    );

            BigDecimal espessuraDecimal =
                    converterDecimalOpcional(
                        espessura
                    );

            BigDecimal precoDecimal =
                    converterDecimalObrigatorio(
                        precoUnitario,
                        "Preço"
                    );

            orcamentoService.adicionarItem(
                id,
                produto,
                divisoria,
                ambiente,
                acionamento,
                criterioVenda,
                quantidade,
                comprimentoDecimal,
                larguraDecimal,
                espessuraDecimal,
                precoDecimal
            );

            var orcamento =
                    orcamentoService.buscarPorId(
                        id
                    );

            OrcamentoItem novoItem =
                    orcamento
                        .getItens()
                        .stream()
                        .filter(item ->
                            item.getId() != null
                        )
                        .max(
                            (item1, item2) ->
                                item1
                                    .getId()
                                    .compareTo(
                                        item2.getId()
                                    )
                        )
                        .orElseThrow(() ->
                            new IllegalStateException(
                                "O item foi salvo, mas não foi possível localizá-lo."
                            )
                        );

            Map<String, Object> dadosItem =
                    new LinkedHashMap<>();

            dadosItem.put(
                "id",
                novoItem.getId()
            );

            dadosItem.put(
                "produto",
                novoItem.getProduto()
            );

            dadosItem.put(
                "divisoria",
                novoItem.getDivisoria()
            );

            dadosItem.put(
                "ambiente",
                novoItem.getAmbiente()
            );

            dadosItem.put(
                "acionamento",
                novoItem.getAcionamento()
            );

            dadosItem.put(
                "criterioVenda",
                novoItem
                    .getCriterioVenda()
                    .name()
            );

            dadosItem.put(
                "criterioDescricao",
                novoItem
                    .getCriterioVenda()
                    .getDescricao()
            );

            dadosItem.put(
                "quantidade",
                novoItem.getQuantidade()
            );

            dadosItem.put(
                "comprimento",
                novoItem.getComprimento()
            );

            dadosItem.put(
                "largura",
                novoItem.getLargura()
            );

            dadosItem.put(
                "espessura",
                novoItem.getEspessura()
            );

            dadosItem.put(
                "medidaCalculada",
                novoItem.getMedidaCalculada()
            );

            dadosItem.put(
                "precoUnitario",
                novoItem.getPrecoUnitario()
            );

            dadosItem.put(
                "valorTotal",
                novoItem.getValorTotal()
            );

            resposta.put(
                "sucesso",
                true
            );

            resposta.put(
                "mensagem",
                "Item adicionado com sucesso."
            );

            resposta.put(
                "item",
                dadosItem
            );

            resposta.put(
                "totais",
                montarTotais(
                    orcamento
                )
            );

            return ResponseEntity.ok(
                resposta
            );

        } catch (
                IllegalArgumentException
                | IllegalStateException e) {

            resposta.put(
                "sucesso",
                false
            );

            resposta.put(
                "mensagem",
                e.getMessage()
            );

            return ResponseEntity
                .status(
                    HttpStatus.BAD_REQUEST
                )
                .body(
                    resposta
                );
        }
    }


    /*
     * ==================================================
     * EDITAR ITEM
     * ==================================================
     */

    @GetMapping(
        "/orcamentos/{orcamentoId}/itens/{itemId}/editar"
    )
    public String editarItem(
            @PathVariable
            Long orcamentoId,

            @PathVariable
            Long itemId,

            Model model) {

        var orcamento =
                orcamentoService.buscarPorId(
                    orcamentoId
                );

        var item =
                orcamentoService.buscarItemPorId(
                    itemId
                );

        if (item.getOrcamento() == null
                || !item.getOrcamento()
                    .getId()
                    .equals(
                        orcamentoId
                    )) {

            throw new RuntimeException(
                "O item não pertence a este orçamento."
            );
        }

        model.addAttribute(
            "orcamento",
            orcamento
        );

        model.addAttribute(
            "item",
            item
        );

        model.addAttribute(
            "criteriosVenda",
            CriterioVenda.values()
        );

        return "orcamentos/editar-item";
    }


    /*
     * ==================================================
     * SALVAR EDIÇÃO DO ITEM
     * ==================================================
     */

    @PostMapping(
        "/orcamentos/{orcamentoId}/itens/{itemId}/editar"
    )
    public String salvarEdicaoItem(
            @PathVariable
            Long orcamentoId,

            @PathVariable
            Long itemId,

            @RequestParam
            String produto,

            @RequestParam(required = false)
            String divisoria,

            @RequestParam(required = false)
            String ambiente,

            @RequestParam(required = false)
            String acionamento,

            @RequestParam
            CriterioVenda criterioVenda,

            @RequestParam
            Integer quantidade,

            @RequestParam(required = false)
            String comprimento,

            @RequestParam(required = false)
            String largura,

            @RequestParam(required = false)
            String espessura,

            @RequestParam
            String precoUnitario) {

        BigDecimal comprimentoDecimal =
                converterDecimalOpcional(
                    comprimento
                );

        BigDecimal larguraDecimal =
                converterDecimalOpcional(
                    largura
                );

        BigDecimal espessuraDecimal =
                converterDecimalOpcional(
                    espessura
                );

        BigDecimal precoDecimal =
                converterDecimalObrigatorio(
                    precoUnitario,
                    "Preço"
                );

        orcamentoService.editarItem(
            orcamentoId,
            itemId,
            produto,
            divisoria,
            ambiente,
            acionamento,
            criterioVenda,
            quantidade,
            comprimentoDecimal,
            larguraDecimal,
            espessuraDecimal,
            precoDecimal
        );

        return "redirect:/orcamentos/"
                + orcamentoId;
    }


    /*
     * ==================================================
     * REMOVER ITEM
     * ==================================================
     */

    @PostMapping(
        "/orcamentos/{orcamentoId}/itens/{itemId}/remover"
    )
    public String removerItem(
            @PathVariable
            Long orcamentoId,

            @PathVariable
            Long itemId) {

        orcamentoService.removerItem(
            orcamentoId,
            itemId
        );

        return "redirect:/orcamentos/"
                + orcamentoId;
    }


    /*
     * ==================================================
     * ALTERAR FRETE
     * ==================================================
     */

    @PostMapping(
        "/orcamentos/{id}/frete"
    )
    public String alterarFrete(
            @PathVariable
            Long id,

            @RequestParam(required = false)
            String percentualFrete) {

        BigDecimal percentual =
                converterDecimalOpcional(
                    percentualFrete
                );

        orcamentoService
            .alterarPercentualFrete(
                id,
                percentual
            );

        return "redirect:/orcamentos/"
                + id;
    }


    /*
     * ==================================================
     * ALTERAR DESCONTO
     * ==================================================
     */

    @PostMapping(
        "/orcamentos/{id}/desconto"
    )
    public String alterarDesconto(
            @PathVariable
            Long id,

            @RequestParam(required = false)
            String percentualDesconto) {

        BigDecimal percentual =
                converterDecimalOpcional(
                    percentualDesconto
                );

        orcamentoService
            .alterarPercentualDesconto(
                id,
                percentual
            );

        return "redirect:/orcamentos/"
                + id;
    }


    /*
     * ==================================================
     * ALTERAR FRETE VIA AJAX
     * ==================================================
     */

    @PostMapping(
        "/orcamentos/{id}/frete-ajax"
    )
    @ResponseBody
    public ResponseEntity<Map<String, Object>>
            alterarFreteAjax(

            @PathVariable
            Long id,

            @RequestParam(required = false)
            String percentualFrete) {

        Map<String, Object> resposta =
                new LinkedHashMap<>();

        try {

            BigDecimal percentual =
                    converterDecimalOpcional(
                        percentualFrete
                    );

            Orcamento orcamento =
                    orcamentoService
                        .alterarPercentualFrete(
                            id,
                            percentual
                        );

            resposta.put(
                "sucesso",
                true
            );

            resposta.put(
                "mensagem",
                "Frete atualizado com sucesso."
            );

            resposta.put(
                "totais",
                montarTotais(
                    orcamento
                )
            );

            return ResponseEntity.ok(
                resposta
            );

        } catch (
                IllegalArgumentException
                | IllegalStateException e) {

            resposta.put(
                "sucesso",
                false
            );

            resposta.put(
                "mensagem",
                e.getMessage()
            );

            return ResponseEntity
                .status(
                    HttpStatus.BAD_REQUEST
                )
                .body(
                    resposta
                );
        }
    }


    /*
     * ==================================================
     * ALTERAR DESCONTO VIA AJAX
     * ==================================================
     */

    @PostMapping(
        "/orcamentos/{id}/desconto-ajax"
    )
    @ResponseBody
    public ResponseEntity<Map<String, Object>>
            alterarDescontoAjax(

            @PathVariable
            Long id,

            @RequestParam(required = false)
            String percentualDesconto) {

        Map<String, Object> resposta =
                new LinkedHashMap<>();

        try {

            BigDecimal percentual =
                    converterDecimalOpcional(
                        percentualDesconto
                    );

            Orcamento orcamento =
                    orcamentoService
                        .alterarPercentualDesconto(
                            id,
                            percentual
                        );

            resposta.put(
                "sucesso",
                true
            );

            resposta.put(
                "mensagem",
                "Desconto atualizado com sucesso."
            );

            resposta.put(
                "totais",
                montarTotais(
                    orcamento
                )
            );

            return ResponseEntity.ok(
                resposta
            );

        } catch (
                IllegalArgumentException
                | IllegalStateException e) {

            resposta.put(
                "sucesso",
                false
            );

            resposta.put(
                "mensagem",
                e.getMessage()
            );

            return ResponseEntity
                .status(
                    HttpStatus.BAD_REQUEST
                )
                .body(
                    resposta
                );
        }
    }


    /*
     * ==================================================
     * ALTERAR STATUS / FORMALIZAR / EXCLUIR
     * ==================================================
     *
     * CONFIRMADO:
     *
     * Formaliza o orçamento como pedido,
     * cria o pedido em AGUARDANDO_FINANCEIRO
     * e abre a tela do pedido criado.
     *
     *
     * CANCELADO:
     *
     * Na nova regra significa excluir definitivamente
     * o orçamento, desde que ainda não exista pedido
     * vinculado.
     * ==================================================
     */

    @PostMapping(
        "/orcamentos/{id}/status"
    )
    public String alterarStatus(
            @PathVariable
            Long id,

            @RequestParam
            StatusOrcamento status,

            RedirectAttributes redirectAttributes) {

        try {

            /*
             * ==========================================
             * CONFIRMAR ORÇAMENTO
             * ==========================================
             */
            if (status == StatusOrcamento.CONFIRMADO) {

                Pedido pedido =
                        orcamentoService
                            .formalizarComoPedido(
                                id
                            );

                redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Orçamento formalizado com sucesso como pedido "
                    + pedido.getNumero()
                    + "."
                );

                return "redirect:/pedidos/"
                        + pedido.getId();
            }


            /*
             * ==========================================
             * CANCELAR ORÇAMENTO
             * ==========================================
             *
             * Cancelar agora significa excluir.
             */
            if (status == StatusOrcamento.CANCELADO) {

                orcamentoService.excluir(
                    id
                );

                redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Orçamento excluído com sucesso."
                );

                return "redirect:/orcamentos";
            }


            /*
             * Outros status não podem ser definidos
             * manualmente por este botão.
             */
            redirectAttributes.addFlashAttribute(
                "erro",
                "Esta alteração de status não é permitida."
            );

            return "redirect:/orcamentos/"
                    + id;


        } catch (
                IllegalArgumentException
                | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                "erro",
                e.getMessage()
            );

            return "redirect:/orcamentos/"
                    + id;
        }
    }


    /*
     * ==================================================
     * IMPRIMIR ORÇAMENTO
     * ==================================================
     */

    /*
     * Exclusão definitiva: somente orçamento em RASCUNHO
     * e sem pedido vinculado (regra aplicada no service).
     */
    @PostMapping(
        "/orcamentos/{id}/excluir"
    )
    public String excluir(
            @PathVariable
            Long id,
            RedirectAttributes redirectAttributes) {

        try {

            orcamentoService.excluir(
                id
            );

            redirectAttributes.addFlashAttribute(
                "sucesso",
                "Orçamento excluído com sucesso."
            );

            return "redirect:/orcamentos";

        } catch (
                IllegalArgumentException
                | IllegalStateException e) {

            redirectAttributes.addFlashAttribute(
                "erro",
                e.getMessage()
            );

            return "redirect:/orcamentos/"
                    + id;
        }
    }


    @GetMapping(
        "/orcamentos/{id}/imprimir"
    )
    public String imprimir(
            @PathVariable
            Long id,
            Model model) {

        var orcamento =
                orcamentoService.buscarPorId(
                    id
                );

        boolean mostrarAmbiente =
                orcamento
                    .getItens()
                    .stream()
                    .anyMatch(item ->
                        item.getAmbiente() != null
                        && !item.getAmbiente().isBlank()
                    );

        boolean mostrarAcionamento =
                orcamento
                    .getItens()
                    .stream()
                    .anyMatch(item ->
                        item.getAcionamento() != null
                        && !item.getAcionamento().isBlank()
                    );

        int totalColunas = 10;

        if (mostrarAmbiente) {
            totalColunas++;
        }

        if (mostrarAcionamento) {
            totalColunas++;
        }

        String classeColunas =
                "cols-" + totalColunas;

        model.addAttribute(
            "orcamento",
            orcamento
        );

        model.addAttribute(
            "mostrarAmbiente",
            mostrarAmbiente
        );

        model.addAttribute(
            "mostrarAcionamento",
            mostrarAcionamento
        );

        model.addAttribute(
            "totalColunas",
            totalColunas
        );

        model.addAttribute(
            "classeColunas",
            classeColunas
        );

        return "orcamentos/imprimir";
    }


    /*
     * ==================================================
     * MONTAR TOTAIS PARA AJAX
     * ==================================================
     */

    private Map<String, Object> montarTotais(
            Orcamento orcamento) {

        Map<String, Object> totais =
                new LinkedHashMap<>();

        totais.put(
            "subtotal",
            orcamento.getSubtotal()
        );

        totais.put(
            "percentualFrete",
            orcamento.getPercentualFrete()
        );

        totais.put(
            "valorFrete",
            orcamento.getValorFrete()
        );

        totais.put(
            "percentualDesconto",
            orcamento.getPercentualDesconto()
        );

        totais.put(
            "valorDesconto",
            orcamento.getValorDesconto()
        );

        totais.put(
            "total",
            orcamento.getTotal()
        );

        return totais;
    }


    /*
     * ==================================================
     * CONVERTER DECIMAL OBRIGATÓRIO
     * ==================================================
     */

    private BigDecimal converterDecimalObrigatorio(
            String valor,
            String nomeCampo) {

        if (valor == null
                || valor.isBlank()) {

            throw new IllegalArgumentException(
                nomeCampo
                + " é obrigatório."
            );
        }

        try {

            return converterTextoParaDecimal(
                valor
            );

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                nomeCampo
                + " possui um valor inválido."
            );
        }
    }


    /*
     * ==================================================
     * CONVERTER DECIMAL OPCIONAL
     * ==================================================
     */

    private BigDecimal converterDecimalOpcional(
            String valor) {

        if (valor == null
                || valor.isBlank()) {

            return null;
        }

        try {

            return converterTextoParaDecimal(
                valor
            );

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                "Valor decimal inválido: "
                + valor
            );
        }
    }


    /*
     * ==================================================
     * CONVERTER TEXTO PARA DECIMAL
     * ==================================================
     */

    private BigDecimal converterTextoParaDecimal(
            String valor) {

        String valorNormalizado =
                valor
                    .trim()
                    .replace(
                        " ",
                        ""
                    );

        if (valorNormalizado.contains(",")) {

            valorNormalizado =
                    valorNormalizado
                        .replace(
                            ".",
                            ""
                        );

            valorNormalizado =
                    valorNormalizado
                        .replace(
                            ",",
                            "."
                        );
        }

        return new BigDecimal(
            valorNormalizado
        );
    }
}