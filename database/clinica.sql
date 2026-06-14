CREATE DATABASE IF NOT EXISTS clinica CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE clinica;

CREATE TABLE IF NOT EXISTS especialidade (
    id   INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS medico (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    nome             VARCHAR(100) NOT NULL,
    email            VARCHAR(100) NOT NULL UNIQUE,
    telefone         VARCHAR(20),
    numero_cedula    VARCHAR(20)  NOT NULL UNIQUE,
    especialidade_id INT,
    FOREIGN KEY (especialidade_id) REFERENCES especialidade(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS paciente (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    nome             VARCHAR(100) NOT NULL,
    email            VARCHAR(100),
    telefone         VARCHAR(20),
    data_nascimento  DATE,
    numero_utente    VARCHAR(20)  NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS consulta (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    paciente_id  INT NOT NULL,
    medico_id    INT NOT NULL,
    data_hora    DATETIME NOT NULL,
    estado       ENUM('MARCADA','REALIZADA','CANCELADA') DEFAULT 'MARCADA',
    notas        TEXT,
    FOREIGN KEY (paciente_id) REFERENCES paciente(id) ON DELETE CASCADE,
    FOREIGN KEY (medico_id)   REFERENCES medico(id)   ON DELETE CASCADE
);

INSERT INTO especialidade (nome) VALUES ('Clínica Geral'),('Pediatria'),('Cardiologia'),('Ortopedia'),('Dermatologia');

INSERT INTO medico (nome, email, telefone, numero_cedula, especialidade_id) VALUES
    ('Dr. António Silva','antonio.silva@clinica.pt','253000001','C-10001',1),
    ('Dra. Maria Santos','maria.santos@clinica.pt','253000002','C-10002',2),
    ('Dr. João Ferreira','joao.ferreira@clinica.pt','253000003','C-10003',3);

INSERT INTO paciente (nome, email, telefone, data_nascimento, numero_utente) VALUES
    ('Carlos Oliveira','carlos@email.pt','910000001','1985-03-15','U-20001'),
    ('Ana Rodrigues','ana@email.pt','910000002','1992-07-22','U-20002'),
    ('Rui Costa','rui@email.pt','910000003','1978-11-05','U-20003');
