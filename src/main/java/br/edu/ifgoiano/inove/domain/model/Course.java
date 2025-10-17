package br.edu.ifgoiano.inove.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tb_course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private LocalDateTime creationDate;

    private LocalDateTime lastUpdateDate;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "course")
    @JsonIgnoreProperties("course")
    private List<FeedBack> feedbacks;

    @ManyToMany
    @JoinTable(name = "tb_student_course",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<User> student;

    @ManyToMany(mappedBy = "admin_courses", fetch = FetchType.LAZY)
    private Set<User> admins = new HashSet<>();

    @ManyToMany(mappedBy = "instructor_courses", fetch = FetchType.LAZY)
    private Set<User> instructors;

    @OneToMany(mappedBy = "course")
    private List<FeedBack> feedBacks;

    @OneToMany(mappedBy = "course")
    private List<Section> sections;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserCompletedContent> userCompletedContents = new HashSet<>();
}
