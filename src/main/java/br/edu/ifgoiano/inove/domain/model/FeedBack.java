package br.edu.ifgoiano.inove.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_feedback")
@JsonIgnoreProperties({"student", "course"})
public class FeedBack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @JsonIgnoreProperties("feedbacks")
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonIgnoreProperties("feedbacks")
    private Course course;

    private String comment;
}
