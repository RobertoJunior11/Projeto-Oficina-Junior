-- Script de inicialização do banco de dados para o sistema de oficina
-- Atualizado para refletir a estrutura atual das entidades

-- =============================================
-- SEQUENCES
-- =============================================

CREATE SEQUENCE SQ_PROPRIETARIO
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE SQ_VEICULO
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE SQ_REVISAO
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- =============================================
-- TABELAS
-- =============================================

-- Tabela de Proprietários
CREATE TABLE TB_PROPRIETARIO (
    ID BIGINT PRIMARY KEY DEFAULT nextval('SQ_PROPRIETARIO'),
    NM_PROPRIETARIO VARCHAR(255) NOT NULL,
    DS_EMAIL VARCHAR(255) NOT NULL,
    NR_TELEFONE VARCHAR(255) NOT NULL,
    DT_CRIACAO TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    DT_ATUALIZACAO TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Veículos
CREATE TABLE TB_VEICULO (
    ID BIGINT PRIMARY KEY DEFAULT nextval('SQ_VEICULO'),
    NM_VEICULO VARCHAR(255) NOT NULL,
    NM_MARCA VARCHAR(255) NOT NULL,
    ANO INTEGER NOT NULL,
    ID_PROPRIETARIO BIGINT NOT NULL,
    DT_CRIACAO TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    DT_ATUALIZACAO TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ID_PROPRIETARIO) REFERENCES TB_PROPRIETARIO(ID)
);

-- Tabela de Revisões
CREATE TABLE TB_REVISAO (
    ID BIGINT PRIMARY KEY DEFAULT nextval('SQ_REVISAO'),
    DS_PROBLEMA VARCHAR(500),
    VL_SERVICO DECIMAL(10,2),
    FL_CONCLUIDA BOOLEAN NOT NULL DEFAULT FALSE,
    ID_VEICULO BIGINT NOT NULL,
    DT_CRIACAO TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    DT_ATUALIZACAO TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ID_VEICULO) REFERENCES TB_VEICULO(ID)
);

-- Tabela de Tipos de Revisão (ElementCollection)
CREATE TABLE TB_REVISAO_TIPOS (
    ID_REVISAO BIGINT NOT NULL,
    TP_REVISAO VARCHAR(255) NOT NULL,
    PRIMARY KEY (ID_REVISAO, TP_REVISAO),
    FOREIGN KEY (ID_REVISAO) REFERENCES TB_REVISAO(ID)
);

-- =============================================
-- TRIGGERS PARA ATUALIZAÇÃO AUTOMÁTICA DE TIMESTAMPS
-- =============================================

CREATE OR REPLACE FUNCTION atualiza_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.DT_ATUALIZACAO = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Triggers para cada tabela
CREATE TRIGGER trg_atualiza_timestamp_proprietario
    BEFORE UPDATE ON TB_PROPRIETARIO
    FOR EACH ROW
    EXECUTE FUNCTION atualiza_timestamp();

CREATE TRIGGER trg_atualiza_timestamp_veiculo
    BEFORE UPDATE ON TB_VEICULO
    FOR EACH ROW
    EXECUTE FUNCTION atualiza_timestamp();

CREATE TRIGGER trg_atualiza_timestamp_revisao
    BEFORE UPDATE ON TB_REVISAO
    FOR EACH ROW
    EXECUTE FUNCTION atualiza_timestamp();

-- =============================================
-- DADOS DE TESTE
-- =============================================

-- Inserir proprietários de teste
INSERT INTO TB_PROPRIETARIO (NM_PROPRIETARIO, DS_EMAIL, NR_TELEFONE) VALUES 
('João Silva', 'joao.silva@email.com', '(11) 99999-1111'),
('Maria Santos', 'maria.santos@email.com', '(11) 99999-2222'),
('Pedro Oliveira', 'pedro.oliveira@email.com', '(11) 99999-3333');

-- Inserir veículos de teste
INSERT INTO TB_VEICULO (NM_VEICULO, NM_MARCA, ANO, ID_PROPRIETARIO) VALUES 
('Civic', 'Honda', 2020, 1),
('Corolla', 'Toyota', 2019, 1),
('Golf', 'Volkswagen', 2021, 2),
('Fiesta', 'Ford', 2018, 3);

-- Inserir revisões de teste
INSERT INTO TB_REVISAO (DS_PROBLEMA, VL_SERVICO, FL_CONCLUIDA, ID_VEICULO) VALUES
('Revisão completa do motor', 270.00, TRUE, 1),
('Troca de óleo e filtros', 120.00, TRUE, 2),
('Alinhamento e balanceamento', 150.00, FALSE, 3);

-- Inserir tipos de revisão de teste
INSERT INTO TB_REVISAO_TIPOS (ID_REVISAO, TP_REVISAO) VALUES 
(1, 'TROCA_OLEO'),
(1, 'ALINHAMENTO'),
(2, 'TROCA_OLEO'),
(3, 'ALINHAMENTO');

-- =============================================
-- ÍNDICES PARA PERFORMANCE
-- =============================================

CREATE INDEX idx_veiculo_proprietario ON TB_VEICULO(ID_PROPRIETARIO);
CREATE INDEX idx_revisao_veiculo ON TB_REVISAO(ID_VEICULO);
CREATE INDEX idx_revisao_tipos_revisao ON TB_REVISAO_TIPOS(ID_REVISAO);
CREATE INDEX idx_proprietario_email ON TB_PROPRIETARIO(DS_EMAIL);

