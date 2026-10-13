package br.com.carpintaria.carpintaria.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.carpintaria.carpintaria.model.OrcamentoItem;

public interface OrcamentoItemRepository
        extends JpaRepository<OrcamentoItem, Long> {

    /*
     * Listar os itens pertencentes a um orçamento
     */
    List<OrcamentoItem> findByOrcamento_Id(Long orcamentoId);
}