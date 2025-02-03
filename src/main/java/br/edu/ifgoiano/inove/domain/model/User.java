package br.edu.ifgoiano.inove.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Getter
@Setter
@Table(name = "tb_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String cpf;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private Date birthDate;

    private String motivation;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    @ManyToMany
    @JoinTable(name = "tb_student_course",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<Course> student_courses;

    @ManyToMany
    @JoinTable(name = "tb_admin_course",
            joinColumns = @JoinColumn(name = "admin_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<Course> admin_courses;

    @ManyToMany
    @JoinTable(name = "tb_instructor_course",
            joinColumns = @JoinColumn(name = "instructor_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id"))
    private List<Course> instructor_courses;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserCompletedContent> userCompletedContents = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.role == UserRole.ADMINISTRATOR)
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_STUDENT"));
        else if(this.role == UserRole.INSTRUCTOR)
            return List.of(new SimpleGrantedAuthority("ROLE_INSTRUCTOR"), new SimpleGrantedAuthority("ROLE_STUDENT"));
        else return List.of(new SimpleGrantedAuthority("ROLE_STUDENT"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
