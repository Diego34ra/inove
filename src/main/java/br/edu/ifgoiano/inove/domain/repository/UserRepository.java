package br.edu.ifgoiano.inove.domain.repository;

import br.edu.ifgoiano.inove.controller.dto.response.user.UserResponseDTO;
import br.edu.ifgoiano.inove.domain.model.User;
import br.edu.ifgoiano.inove.domain.model.UserRole;
import br.edu.ifgoiano.inove.domain.repository.custom.UserCustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long>, UserCustomRepository {
    @Query(nativeQuery = true, value = "SELECT * FROM tb_user WHERE role = :role")
    List<User> findByRole(String role);

    UserDetails findByEmail(String login);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

}
