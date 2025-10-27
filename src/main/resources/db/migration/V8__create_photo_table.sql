CREATE TABLE photo (
    id BIGSERIAL PRIMARY KEY,
    photo TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE pet_has_photo (
    pet_id BIGINT REFERENCES pet(id),
    photo_id BIGINT REFERENCES photo(id),
    CONSTRAINT pet_photo_unique UNIQUE (pet_id, photo_id)
);

CREATE TABLE ong_has_photo (
    ong_id BIGINT REFERENCES ong(id),
    photo_id BIGINT REFERENCES photo(id),
    CONSTRAINT ong_photo_unique UNIQUE (ong_id, photo_id)
);

ALTER TABLE users
ADD COLUMN photo TEXT;
