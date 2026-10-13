/*
 * =========================================================
 * PRESERVAÇÃO DOS DADOS COMERCIAIS NO PEDIDO
 * =========================================================
 *
 * Quando o orçamento vira pedido, o pedido passa a guardar
 * uma "fotografia" dos valores da venda:
 *
 *  - subtotal, frete, desconto e total
 *  - tipo de madeira
 *  - em cada item: critério de venda, quantidade, medida
 *    calculada, preço unitário e valor total
 *
 * Assim o histórico comercial original continua disponível
 * mesmo que a produção use outros campos técnicos depois.
 *
 * Esta migration NÃO altera nenhuma migration anterior.
 * =========================================================
 */


/*
 * ---------------------------------------------------------
 * PEDIDOS: dados comerciais
 * ---------------------------------------------------------
 */

ALTER TABLE pedidos
    ADD COLUMN tipo_madeira VARCHAR(100);

ALTER TABLE pedidos
    ADD COLUMN subtotal NUMERIC(12,2) NOT NULL DEFAULT 0;

ALTER TABLE pedidos
    ADD COLUMN percentual_frete NUMERIC(5,2) NOT NULL DEFAULT 0;

ALTER TABLE pedidos
    ADD COLUMN valor_frete NUMERIC(12,2) NOT NULL DEFAULT 0;

ALTER TABLE pedidos
    ADD COLUMN percentual_desconto NUMERIC(5,2) NOT NULL DEFAULT 0;

ALTER TABLE pedidos
    ADD COLUMN valor_desconto NUMERIC(12,2) NOT NULL DEFAULT 0;

ALTER TABLE pedidos
    ADD COLUMN total NUMERIC(12,2) NOT NULL DEFAULT 0;


/*
 * ---------------------------------------------------------
 * PEDIDO_ITENS: dados comerciais
 * ---------------------------------------------------------
 *
 * Colunas opcionais, porque um pedido criado manualmente
 * (sem orçamento) não possui esses dados.
 */

ALTER TABLE pedido_itens
    ADD COLUMN orcamento_item_id BIGINT;

ALTER TABLE pedido_itens
    ADD COLUMN criterio_venda VARCHAR(10);

ALTER TABLE pedido_itens
    ADD COLUMN quantidade_comercial INTEGER;

ALTER TABLE pedido_itens
    ADD COLUMN medida_calculada NUMERIC(14,6);

ALTER TABLE pedido_itens
    ADD COLUMN preco_unitario NUMERIC(12,2);

ALTER TABLE pedido_itens
    ADD COLUMN valor_total NUMERIC(12,2);

ALTER TABLE pedido_itens
    ADD CONSTRAINT fk_pedido_itens_orcamento_item
    FOREIGN KEY (orcamento_item_id)
    REFERENCES orcamento_itens(id);


/*
 * ---------------------------------------------------------
 * PREENCHER PEDIDOS JÁ EXISTENTES
 * ---------------------------------------------------------
 */

UPDATE pedidos p
SET tipo_madeira        = o.tipo_madeira,
    subtotal            = COALESCE(o.subtotal, 0),
    percentual_frete    = COALESCE(o.percentual_frete, 0),
    valor_frete         = COALESCE(o.valor_frete, 0),
    percentual_desconto = COALESCE(o.percentual_desconto, 0),
    valor_desconto      = COALESCE(o.valor_desconto, 0),
    total               = COALESCE(o.total, 0)
FROM orcamentos o
WHERE o.id = p.orcamento_id;


/*
 * Itens já existentes: o vínculo é feito pela ordem de criação,
 * somente quando o pedido e o orçamento têm a mesma quantidade
 * de itens (para nunca associar item errado).
 */

UPDATE pedido_itens t
SET orcamento_item_id    = o.id,
    criterio_venda       = o.criterio_venda,
    quantidade_comercial = o.quantidade,
    medida_calculada     = o.medida_calculada,
    preco_unitario       = o.preco_unitario,
    valor_total          = o.valor_total
FROM (
        SELECT id,
               pedido_id,
               ROW_NUMBER() OVER (
                   PARTITION BY pedido_id
                   ORDER BY id
               ) AS rn
        FROM pedido_itens
     ) pi
JOIN pedidos p
    ON p.id = pi.pedido_id
JOIN (
        SELECT id,
               orcamento_id,
               criterio_venda,
               quantidade,
               medida_calculada,
               preco_unitario,
               valor_total,
               ROW_NUMBER() OVER (
                   PARTITION BY orcamento_id
                   ORDER BY id
               ) AS rn
        FROM orcamento_itens
     ) o
    ON o.orcamento_id = p.orcamento_id
   AND o.rn = pi.rn
WHERE t.id = pi.id
  AND (
        SELECT COUNT(*)
        FROM pedido_itens x
        WHERE x.pedido_id = p.id
      )
    = (
        SELECT COUNT(*)
        FROM orcamento_itens y
        WHERE y.orcamento_id = p.orcamento_id
      );


/*
 * ---------------------------------------------------------
 * UM ORÇAMENTO SÓ PODE GERAR UM PEDIDO
 * ---------------------------------------------------------
 *
 * Protege contra duplicidade mesmo com dois cliques
 * simultâneos em "Confirmar orçamento".
 */

CREATE UNIQUE INDEX ux_pedidos_orcamento
    ON pedidos(orcamento_id)
    WHERE orcamento_id IS NOT NULL;
