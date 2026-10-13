CREATE TABLE usuario_acessos (

    usuario_id BIGINT NOT NULL,

    acesso VARCHAR(40) NOT NULL,

    CONSTRAINT pk_usuario_acessos
        PRIMARY KEY (usuario_id, acesso),

    CONSTRAINT fk_usuario_acessos_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
        ON DELETE CASCADE
);


/*
 * ==================================================
 * PRESERVAR OS ACESSOS ATUAIS
 * ==================================================
 *
 * Todo usuário comum recebe inicialmente
 * o acesso correspondente ao seu perfil atual.
 *
 * O ADMIN não precisa ser inserido aqui,
 * pois continuará tendo acesso total
 * automaticamente.
 */

INSERT INTO usuario_acessos (
    usuario_id,
    acesso
)
SELECT
    id,
    perfil
FROM usuarios
WHERE perfil IS NOT NULL
  AND perfil <> 'ADMIN'
ON CONFLICT DO NOTHING;