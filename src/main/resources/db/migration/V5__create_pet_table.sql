CREATE TABLE pet (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    birth_date DATE,
    status TEXT NOT NULL CHECK (status IN ('APADRINHAVEL', 'ADOTADO', 'INDISPONIVEL')),
    breed VARCHAR(50) NOT NULL,
    weight DECIMAL NOT NULL,
    date_of_admission DATE NOT NULL,
    gender TEXT NOT NULL CHECK (gender IN ('MACHO', 'FEMEA')),
    profile TEXT NOT NULL,
    ong_id BIGINT REFERENCES ong(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);