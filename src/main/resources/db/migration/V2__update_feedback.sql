ALTER TABLE tb_feedback ADD CONSTRAINT uk_tb_feedback_student_course UNIQUE (student_id, course_id);
ALTER TABLE tb_user     ADD CONSTRAINT uk_tb_user_cpf      UNIQUE (cpf);
ALTER TABLE tb_user     ADD CONSTRAINT uk_tb_user_email    UNIQUE (email);
