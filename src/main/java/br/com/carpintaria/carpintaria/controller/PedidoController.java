package br.com.carpintaria.carpintaria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.carpintaria.carpintaria.model.Pedido;
import br.com.carpintaria.carpintaria.service.ClienteService;
import br.com.carpintaria.carpintaria.service.PedidoService;
import br.com.carpintaria.carpintaria.service.UsuarioService;

@Controller
public class PedidoController {

    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private final UsuarioService usuarioService;


    public PedidoController(
            PedidoService pedidoService,
            ClienteService clienteService,
            UsuarioService usuarioService) {

        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
        this.usuarioService = usuarioService;
    }


    /*
     * ==================================================
     * LISTA DE PEDIDOS
     * ==================================================
     */

    @GetMapping("/pedidos")
    public String listar(
            Model model) {

        model.addAttribute(
            "pedidos",
            pedidoService.listarTodos()
        );

        return "pedidos/lista";
    }


    /*
     * ==================================================
     * VISUALIZAR PEDIDO
     * ==================================================
     */

    @GetMapping("/pedidos/{id}")
    public String visualizar(
            @PathVariable
            Long id,

            Model model) {

        Pedido pedido =
                pedidoService.buscarPorId(
                    id
                );

        model.addAttribute(
            "pedido",
            pedido
        );

        model.addAttribute(
            "itens",
            pedidoService.listarItens(
                id
            )
        );

        return "pedidos/detalhes";
    }


    /*
     * ==================================================
     * NOVO PEDIDO
     * ==================================================
     */

    @GetMapping("/pedidos/novo")
    public String novo(
            Model model) {

        model.addAttribute(
            "clientes",
            clienteService.listarTodos()
        );

        model.addAttribute(
            "vendedores",
            usuarioService.listarTodos()
        );

        return "pedidos/novo";
    }


    /*
     * ==================================================
     * CRIAR PEDIDO MANUALMENTE
     * ==================================================
     */

    @PostMapping("/pedidos/criar")
    public String criar(
            @RequestParam
            Long clienteId,

            @RequestParam
            Long vendedorId,

            @RequestParam(required = false)
            String obra,

            @RequestParam(required = false)
            String observacoes) {

        Pedido pedido =
                pedidoService.criar(
                    clienteId,
                    vendedorId,
                    obra,
                    observacoes
                );

        return "redirect:/pedidos/"
                + pedido.getId();
    }
}