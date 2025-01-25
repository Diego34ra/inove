package br.edu.ifgoiano.inove.domain.repository;

import br.edu.ifgoiano.inove.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM tb_user WHERE role = :role")
    List<User> findByRole(String role);

    UserDetails findByEmail(String login);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);
}
