-- Cria usuário admin padrão

INSERT INTO users (name, email, password, status, role, created_at, updated_at)
VALUES (
    'Padrinho Digital',
    'padrinhodigital@gmail.com',
    '$2a$10$PJ26jp63jSPZVzPETFLeKO6kFL9xvmV7qZuYSAFKgVQPCyiJzyKFu',
    'ATIVO',
    'ADMIN',
    NOW(),
    NOW()
)
ON CONFLICT (email) DO NOTHING;
