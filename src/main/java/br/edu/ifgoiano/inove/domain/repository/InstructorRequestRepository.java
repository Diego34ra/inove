package br.edu.ifgoiano.inove.domain.repository;

import br.edu.ifgoiano.inove.domain.model.InstructorRequest;
import br.edu.ifgoiano.inove.domain.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstructorRequestRepository extends JpaRepository<InstructorRequest, Long> {
    Optional<InstructorRequest> findByToken(String token);
    boolean existsByEmailAndStatus(String email, RequestStatus status);
}
