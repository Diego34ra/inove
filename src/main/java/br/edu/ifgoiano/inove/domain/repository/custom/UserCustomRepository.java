package br.edu.ifgoiano.inove.domain.repository.custom;

import br.edu.ifgoiano.inove.controller.dto.response.user.UserResponseDTO;
import br.edu.ifgoiano.inove.domain.model.UserRole;
import br.edu.ifgoiano.inove.domain.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


public interface UserCustomRepository{

    Page<UserResponseDTO> findByRole(UserRole userRole, Pageable pageable);
}
