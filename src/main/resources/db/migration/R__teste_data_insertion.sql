-- LIMPAR TABELAS (ordem importa por causa das FKs)
TRUNCATE TABLE tb_admin_course       RESTART IDENTITY CASCADE;
TRUNCATE TABLE tb_instructor_course  RESTART IDENTITY CASCADE;
TRUNCATE TABLE tb_student_course     RESTART IDENTITY CASCADE;
TRUNCATE TABLE tb_feedback           RESTART IDENTITY CASCADE;
TRUNCATE TABLE tb_content            RESTART IDENTITY CASCADE;
TRUNCATE TABLE tb_section            RESTART IDENTITY CASCADE;
TRUNCATE TABLE tb_user               RESTART IDENTITY CASCADE;
TRUNCATE TABLE tb_school             RESTART IDENTITY CASCADE;
TRUNCATE TABLE tb_course             RESTART IDENTITY CASCADE;

-- ========================================
-- POPULAR DADOS INICIAIS
-- ========================================

-- Escolas
INSERT INTO tb_school (name, city, federative_unit)
VALUES
 ('Escola Técnica Central', 'São Paulo', 'SP'),
 ('Colégio Futuro', 'Rio de Janeiro', 'RJ');

-- Cursos
INSERT INTO tb_course (name, description)
VALUES
 ('Java para Iniciantes', 'Curso básico de Java e Spring'),
 ('Banco de Dados', 'Curso introdutório de SQL e modelagem');

-- Usuários (Admins, Instrutores, Estudantes)
INSERT INTO tb_user (name, email, cpf, password, role, birth_date, school_id)
VALUES
 ('Maria Admin', 'maria.admin@email.com', '11111111111', '$2a$10$z59MkvFkWYiWw677cC2rnefD.Zj6iwc1v1Ua6XQoEkh5aFF4ZBIn.', 'ADMINISTRATOR', '1990-01-01', 1),
 ('João Instrutor', 'joao.instrutor@email.com', '22222222222', '$2a$10$z59MkvFkWYiWw677cC2rnefD.Zj6iwc1v1Ua6XQoEkh5aFF4ZBIn.', 'INSTRUCTOR', '1985-05-10', 1),
 ('Ana Estudante', 'ana.estudante@email.com', '33333333333', '$2a$10$z59MkvFkWYiWw677cC2rnefD.Zj6iwc1v1Ua6XQoEkh5aFF4ZBIn.', 'STUDENT', '2000-09-20', 2);

-- Seções
INSERT INTO tb_section (title, description, course_id)
VALUES
 ('Introdução ao Java', 'Fundamentos da linguagem', 1),
 ('Modelagem Relacional', 'Conceitos de bancos de dados', 2);

-- Conteúdos
INSERT INTO tb_content (title, description, section_id, theorical_content_text, video_title, video_url)
VALUES
 ('Primeiro Programa', 'Como rodar seu primeiro Hello World', 1, 'Exemplo de código Java básico', 'Aula 1 - Hello World', 'https://youtube.com/hello'),
 ('Normalização', 'Explicação sobre formas normais', 2, 'Texto sobre 1FN, 2FN e 3FN', 'Aula 1 - Normalização', 'https://youtube.com/normalizacao');

-- Feedback
INSERT INTO tb_feedback (comment, course_id, student_id)
VALUES
 ('Gostei muito do curso!', 1, 3);

-- Relações
INSERT INTO tb_admin_course (admin_id, course_id)
VALUES (1, 1), (1, 2);

INSERT INTO tb_instructor_course (instructor_id, course_id)
VALUES (2, 1), (2, 2);

INSERT INTO tb_student_course (student_id, course_id)
VALUES (3, 1), (3, 2);
