CREATE TABLE cost (
    id BIGSERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL REFERENCES pet(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE cost_history (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    monthly_amount DECIMAL NOT NULL,
    cost_id BIGINT NOT NULL REFERENCES cost(id),
    start_date DATE NOT NULL,
    end_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

