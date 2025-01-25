package br.edu.ifgoiano.inove.controller.dto.request.section;

import br.edu.ifgoiano.inove.domain.model.Content;
import br.edu.ifgoiano.inove.domain.model.Course;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SectionRequestDTO {

    private Long id;

    private String title;

    private String description;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER, mappedBy = "section")
    private List<Content> contents;
}
