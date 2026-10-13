/*
 * =========================================================
 * NOVOS CRITÉRIOS DE VENDA DOS ITENS DO ORÇAMENTO
 * =========================================================
 *
 * Antes:
 *
 * ML  = Metro Linear
 * M2  = Metro Quadrado
 * M3  = Metro Cúbico
 *
 * Agora também:
 *
 * UNIDADE
 * PECA
 * CONJUNTO
 *
 * Esses novos critérios não dependem de medidas
 * como comprimento, largura ou espessura.
 * =========================================================
 */


ALTER TABLE orcamento_itens
DROP CONSTRAINT IF EXISTS ck_orcamento_itens_criterio_venda;


ALTER TABLE orcamento_itens
ADD CONSTRAINT ck_orcamento_itens_criterio_venda
CHECK (
    criterio_venda IN (
        'ML',
        'M2',
        'M3',
        'UNIDADE',
        'PECA',
        'CONJUNTO'
    )
);