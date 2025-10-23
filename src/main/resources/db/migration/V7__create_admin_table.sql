CREATE TABLE admin (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id)
);