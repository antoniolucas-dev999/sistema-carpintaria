package br.com.carpintaria.carpintaria.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.carpintaria.carpintaria.model.Orcamento;
import br.com.carpintaria.carpintaria.model.StatusOrcamento;

public interface OrcamentoRepository
        extends JpaRepository<Orcamento, Long> {

    /*
     * Buscar pelo número do orçamento
     */
    Optional<Orcamento> findByNumero(String numero);

    /*
     * Verificar se número já existe
     */
    boolean existsByNumero(String numero);

    /*
     * Pesquisar pelo nome do cliente
     */
    List<Orcamento>
        findByCliente_NomeRazaoSocialContainingIgnoreCase(
            String nome
        );

    /*
     * Listar por status
     */
    List<Orcamento> findByStatus(
        StatusOrcamento status
    );

    /*
     * Soma do valor de todos os orçamentos
     */
    @Query("""
        SELECT COALESCE(SUM(o.total), 0)
        FROM Orcamento o
    """)
    BigDecimal somarValorTotalOrcamentos();
}