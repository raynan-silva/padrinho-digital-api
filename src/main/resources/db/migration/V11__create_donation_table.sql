CREATE TABLE donation_campaign (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(300) NOT NULL,
    description TEXT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    value_target DECIMAL NOT NULL,
    amount_collected DECIMAL NOT NULL DEFAULT 0,
    status VARCHAR(10) NOT NULL DEFAULT 'ATIVA',
    photo TEXT NOT NULL,
    ong_id BIGINT NOT NULL REFERENCES ong(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT check_status CHECK (status IN ('ATIVA', 'PAUSADA', 'CANCELADA', 'CONCLUIDA'))
);

CREATE TABLE donation (
    id BIGSERIAL PRIMARY KEY,
    amount DECIMAL NOT NULL,
    date DATE NOT NULL DEFAULT NOW(),
    godfather_id BIGINT NOT NULL REFERENCES godfather(id),
    donation_campaign_id BIGINT NOT NULL REFERENCES donation_campaign(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);