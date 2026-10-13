ALTER TABLE usuarios
    ADD COLUMN login VARCHAR(80);


UPDATE usuarios
SET login = LOWER(email)
WHERE login IS NULL;


ALTER TABLE usuarios
    ALTER COLUMN login SET NOT NULL;


ALTER TABLE usuarios
    ADD CONSTRAINT uk_usuarios_login
        UNIQUE (login);


ALTER TABLE usuarios
    ALTER COLUMN email DROP NOT NULL;