ALTER TABLE tb_feedback ADD CONSTRAINT uk_tb_feedback_student_course UNIQUE (student_id, course_id);
