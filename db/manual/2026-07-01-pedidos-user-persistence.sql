ALTER TABLE pedidos
    ADD COLUMN nombre_destinatario VARCHAR(255) NULL,
    ADD COLUMN direccion_destino VARCHAR(255) NULL,
    ADD COLUMN ciudad_destino VARCHAR(255) NULL,
    ADD COLUMN region_destino VARCHAR(255) NULL,
    ADD COLUMN telefono_contacto VARCHAR(255) NULL;

-- cliente_id ya existia y ahora se utiliza como referencia canonica
-- del usuario autenticado derivado desde el claim uid del JWT.
