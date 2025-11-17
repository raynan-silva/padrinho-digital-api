-- Armazena uma conversa única entre dois usuários
CREATE TABLE chat_conversation (
    id BIGSERIAL PRIMARY KEY,
    user_a_id BIGINT NOT NULL REFERENCES users(id),
    user_b_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    -- Garante que não haja conversas duplicadas (A-B é o mesmo que B-A)
    CONSTRAINT uk_users UNIQUE (user_a_id, user_b_id)
);

-- Armazena cada mensagem
CREATE TABLE chat_message (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL REFERENCES chat_conversation(id),
    sender_id BIGINT NOT NULL REFERENCES users(id), -- Quem enviou
    receiver_id BIGINT NOT NULL REFERENCES users(id), -- Quem deve receber
    content TEXT NOT NULL,
    type_content VARCHAR(20) NOT NULL DEFAULT 'TEXTO',
    status VARCHAR(10) NOT NULL DEFAULT 'ENVIADA',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT check_type_content CHECK (type_content IN ('TEXTO', 'IMAGEM')),
    CONSTRAINT check_status CHECK (status IN ('ENVIADA', 'ENTREGUE', 'LIDA'))
);

