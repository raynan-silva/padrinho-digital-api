-- 1. Remove a restrição CHECK existente
ALTER TABLE sponsorship_history
DROP CONSTRAINT sponsorship_history_status_check;

-- 2. Adiciona a nova restrição com o status 'ENCERRADO'
ALTER TABLE sponsorship_history
ADD CONSTRAINT sponsorship_history_status_check
CHECK (status IN ('ATIVO', 'PAUSADO', 'CANCELADO', 'ENCERRADO'));