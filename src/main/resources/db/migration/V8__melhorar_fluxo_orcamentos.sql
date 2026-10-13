/*
 * =========================================================
 * NOVO FLUXO DOS ORÇAMENTOS
 * =========================================================
 *
 * Objetivos:
 *
 * 1. Permitir criar um orçamento RASCUNHO
 *    antes da seleção do cliente.
 *
 * 2. Simplificar o fluxo comercial para:
 *
 *    RASCUNHO
 *    CONFIRMADO
 *    CONVERTIDO_EM_PEDIDO
 *    CANCELADO
 *
 * 3. Registrar quando o orçamento foi criado,
 *    alterado e confirmado.
 * =========================================================
 */


/*
 * =========================================================
 * AJUSTAR STATUS ANTIGOS
 * =========================================================
 */

UPDATE orcamentos
SET status = 'CONFIRMADO'
WHERE status IN (
    'EMITIDO',
    'APROVADO'
);


/*
 * Orçamentos anteriormente reprovados
 * passam a ser considerados cancelados.
 */

UPDATE orcamentos
SET status = 'CANCELADO'
WHERE status = 'REPROVADO';


/*
 * =========================================================
 * TROCAR A REGRA DE STATUS
 * =========================================================
 */

ALTER TABLE orcamentos
DROP CONSTRAINT IF EXISTS ck_orcamentos_status;


ALTER TABLE orcamentos
ADD CONSTRAINT ck_orcamentos_status
CHECK (
    status IN (
        'RASCUNHO',
        'CONFIRMADO',
        'CONVERTIDO_EM_PEDIDO',
        'CANCELADO'
    )
);


/*
 * =========================================================
 * CLIENTE OPCIONAL ENQUANTO FOR RASCUNHO
 * =========================================================
 *
 * Isso permite:
 *
 * Novo orçamento
 * → rascunho criado imediatamente
 * → cliente escolhido depois.
 */

ALTER TABLE orcamentos
ALTER COLUMN cliente_id DROP NOT NULL;


/*
 * Porém um orçamento que sair de RASCUNHO
 * obrigatoriamente precisa possuir cliente.
 */

ALTER TABLE orcamentos
ADD CONSTRAINT ck_orcamento_cliente_apos_rascunho
CHECK (
    status = 'RASCUNHO'
    OR cliente_id IS NOT NULL
);


/*
 * =========================================================
 * DATAS DE CONTROLE
 * =========================================================
 */

ALTER TABLE orcamentos
ADD COLUMN criado_em TIMESTAMP WITH TIME ZONE
NOT NULL DEFAULT CURRENT_TIMESTAMP;


ALTER TABLE orcamentos
ADD COLUMN atualizado_em TIMESTAMP WITH TIME ZONE
NOT NULL DEFAULT CURRENT_TIMESTAMP;


ALTER TABLE orcamentos
ADD COLUMN confirmado_em TIMESTAMP WITH TIME ZONE;


/*
 * =========================================================
 * ÍNDICES
 * =========================================================
 */

CREATE INDEX idx_orcamentos_status
ON orcamentos(status);


CREATE INDEX idx_orcamentos_vendedor
ON orcamentos(vendedor_id);