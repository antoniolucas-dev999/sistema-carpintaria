package br.com.carpintaria.carpintaria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.carpintaria.carpintaria.model.PedidoItem;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {

    List<PedidoItem> findByPedido_IdOrderByIdAsc(Long pedidoId);

    long countByPedido_Id(Long pedidoId);
}