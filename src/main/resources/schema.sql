CREATE TABLE IF NOT EXISTS atendimento (
    id TEXT PRIMARY KEY,
    cpf TEXT NOT NULL,
    status TEXT NOT NULL,
    classificacao_risco TEXT,
    prescricao TEXT
);

CREATE TABLE IF NOT EXISTS medicao (
    id TEXT PRIMARY KEY,
    atendimento_id TEXT NOT NULL REFERENCES atendimento (id),
    temperatura REAL NOT NULL,
    frequencia_cardiaca INTEGER NOT NULL,
    data_hora TEXT NOT NULL
);
