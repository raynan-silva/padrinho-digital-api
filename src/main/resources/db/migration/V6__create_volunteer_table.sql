CREATE TABLE volunteer (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    ong_id BIGINT REFERENCES ong(id)
);