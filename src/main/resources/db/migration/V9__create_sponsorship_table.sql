CREATE TABLE sponsorship (
    id BIGSERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL REFERENCES pet(id),
    godfather_id BIGINT NOT NULL REFERENCES godfather(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_godfather_pet UNIQUE (pet_id, godfather_id)
);

CREATE TABLE sponsorship_history (
    id BIGSERIAL PRIMARY KEY,
    sponsorship_id BIGINT NOT NULL REFERENCES sponsorship(id),
    status VARCHAR(10) NOT NULL CHECK (status IN ('ATIVO', 'PAUSADO', 'CANCELADO')),
    monthly_amount DECIMAL NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);