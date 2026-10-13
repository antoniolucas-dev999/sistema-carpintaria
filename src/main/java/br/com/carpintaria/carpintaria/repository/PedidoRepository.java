package br.com.carpintaria.carpintaria.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.carpintaria.carpintaria.model.Pedido;
import br.com.carpintaria.carpintaria.model.StatusPedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Optional<Pedido> findByNumero(String numero);

    boolean existsByNumero(String numero);

    Optional<Pedido> findByOrcamento_Id(Long orcamentoId);

    boolean existsByOrcamento_Id(Long orcamentoId);

    List<Pedido> findByStatus(StatusPedido status);

    List<Pedido> findByCliente_NomeRazaoSocialContainingIgnoreCase(String nome);

    List<Pedido> findAllByOrderByDataPedidoDescIdDesc();
}