CREATE TABLE ong (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    registration_date DATE NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(10) NOT NULL CHECK (status IN ('ATIVO', 'INATIVO', 'PENDENTE')),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE address (
    id BIGSERIAL PRIMARY KEY,
    street VARCHAR(450) NOT NULL,
    number VARCHAR(10),
    neighborhood VARCHAR(200) NOT NULL,
    city VARCHAR(200) NOT NULL,
    uf VARCHAR(2) NOT NULL,
    complement VARCHAR(200),
    cep VARCHAR(8),
    ong_id BIGINT REFERENCES ong(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE manager (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT REFERENCES users(id),
    ong_id BIGINT REFERENCES ong(id)
);

CREATE TABLE godfather (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT REFERENCES users(id)
);
