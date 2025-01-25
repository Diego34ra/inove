package br.edu.ifgoiano.inove.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tb_school")
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String city;
    private String email;
    private String password;
    private String federativeUnit;

    @OneToMany(mappedBy = "school")
    private List<User> student;
}
