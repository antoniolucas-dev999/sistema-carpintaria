package br.com.carpintaria.carpintaria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.carpintaria.carpintaria.model.Cliente;
import br.com.carpintaria.carpintaria.service.ClienteService;

@Controller
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /*
     * LISTAR CLIENTES
     */
    @GetMapping("/clientes")
    public String listarClientes(
            @RequestParam(required = false) String busca,
            Model model) {

        if (busca != null && !busca.isBlank()) {

            model.addAttribute(
                "clientes",
                clienteService.pesquisarPorNome(busca)
            );

        } else {

            model.addAttribute(
                "clientes",
                clienteService.listarTodos()
            );
        }

        model.addAttribute("busca", busca);

        return "clientes/lista";
    }

    /*
     * ABRIR TELA DE NOVO CLIENTE
     */
    @GetMapping("/clientes/novo")
    public String novoCliente(Model model) {

        model.addAttribute(
            "cliente",
            new Cliente()
        );

        return "clientes/form";
    }

    /*
     * SALVAR CLIENTE
     */
    @PostMapping("/clientes/salvar")
    public String salvarCliente(Cliente cliente) {

        clienteService.cadastrar(cliente);

        return "redirect:/clientes";
    }

    /*
     * ABRIR TELA DE EDIÇÃO
     */
    @GetMapping("/clientes/editar/{id}")
    public String editarCliente(
            @PathVariable Long id,
            Model model) {

        Cliente cliente =
            clienteService.buscarPorId(id);

        model.addAttribute(
            "cliente",
            cliente
        );

        return "clientes/form";
    }

    /*
     * ATUALIZAR CLIENTE
     */
    @PostMapping("/clientes/atualizar/{id}")
    public String atualizarCliente(
            @PathVariable Long id,
            Cliente cliente) {

        clienteService.atualizar(
            id,
            cliente
        );

        return "redirect:/clientes";
    }

    /*
     * EXCLUIR CLIENTE
     */
    @GetMapping("/clientes/excluir/{id}")
    public String excluirCliente(
            @PathVariable Long id) {

        clienteService.excluir(id);

        return "redirect:/clientes";
    }
}