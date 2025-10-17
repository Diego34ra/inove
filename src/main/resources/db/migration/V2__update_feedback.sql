DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'uk_tb_feedback_student_course'
    ) THEN
        ALTER TABLE tb_feedback ADD CONSTRAINT uk_tb_feedback_student_course UNIQUE (student_id, course_id);
    END IF;
END $$;

