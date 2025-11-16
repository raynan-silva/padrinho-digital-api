-- Tabela para definir os Níveis
CREATE TABLE godfather_level (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    icon_url TEXT, -- URL para um ícone do nível
    level_order INT NOT NULL UNIQUE, -- Ordem (1, 2, 3...)
    required_amount DECIMAL NOT NULL DEFAULT 0 -- Valor total doado para alcançar
);

-- Tabela para definir os Selos (Conquistas)
CREATE TABLE seal (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    icon_url TEXT NOT NULL,
    trigger_metric VARCHAR(50) NOT NULL, -- Ex: "DONATION_COUNT", "SPONSORSHIP_COUNT"
    trigger_value DECIMAL NOT NULL -- Ex: 5 (para 5 doações), 1000 (para R$ 1000)
);

-- MODIFICAÇÃO: Adicionar o nível ao Padrinho
ALTER TABLE godfather
ADD COLUMN current_level_id BIGINT REFERENCES godfather_level(id);

-- Tabela de Junção: Quais selos um padrinho conquistou
CREATE TABLE godfather_seal (
    id BIGSERIAL PRIMARY KEY,
    godfather_id BIGINT NOT NULL REFERENCES godfather(id),
    seal_id BIGINT NOT NULL REFERENCES seal(id),
    conquered_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(godfather_id, seal_id)
);

---
--- SEED DATA (Dados Iniciais)
---

-- Níveis
INSERT INTO godfather_level (name, icon_url, level_order, required_amount) VALUES
('Padrinho Iniciante', 'url/icon/iniciante.png', 1, 0),
('Padrinho Bronze', 'url/icon/bronze.png', 2, 250),  -- R$ 250 doados
('Padrinho Prata', 'url/icon/prata.png', 3, 750),   -- R$ 750 doados
('Padrinho Ouro', 'url/icon/ouro.png', 4, 1500), -- R$ 1500 doados
('Padrinho Platina', 'url/icon/platina.png', 5, 5000); -- R$ 5000 doados

-- Selos
-- trigger_metric:
-- 'DEFAULT' = Dado no cadastro
-- 'DONATION_COUNT' = Número de doações
-- 'DONATION_TOTAL_AMOUNT' = Valor total doado
-- 'SPONSORSHIP_ACTIVE_COUNT' = Qtd de apadrinhamentos ATIVOS
INSERT INTO seal (name, description, icon_url, trigger_metric, trigger_value) VALUES
('Coração de Ouro', 'Você se cadastrou para fazer a diferença!', 'url/icon/selo_cadastro.png', 'DEFAULT', 0),
('Primeiro Passo', 'Você fez sua primeira doação!', 'url/icon/selo_primeira_doacao.png', 'DONATION_COUNT', 1),
('Anjo da Guarda', 'Você fez seu primeiro apadrinhamento!', 'url/icon/selo_primeiro_padrinho.png', 'SPONSORSHIP_ACTIVE_COUNT', 1),
('Amigo Fiel', 'Você atingiu 5 apadrinhamentos ativos!', 'url/icon/selo_amigo_fiel.png', 'SPONSORSHIP_ACTIVE_COUNT', 5),
('Mão Aberta', 'Você doou mais de R$ 500!', 'url/icon/selo_mao_aberta.png', 'DONATION_TOTAL_AMOUNT', 500),
('Benfeitor', 'Você doou mais de R$ 1000!', 'url/icon/selo_benfeitor.png', 'DONATION_TOTAL_AMOUNT', 1000);