/*
 * =========================================================
 * REMOVE A RESTRIÇÃO ANTIGA DOS CRITÉRIOS DE VENDA
 * =========================================================
 *
 * A restrição antiga permitia apenas:
 *
 * ML
 * M2
 * M3
 *
 * A V9 já criou a nova restrição permitindo também:
 *
 * UNIDADE
 * PECA
 * CONJUNTO
 * =========================================================
 */

ALTER TABLE orcamento_itens
DROP CONSTRAINT IF EXISTS orcamento_itens_criterio_venda_check;