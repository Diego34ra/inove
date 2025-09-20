package br.edu.ifgoiano.inove.domain.repository.custom.implementation;

import br.edu.ifgoiano.inove.controller.dto.response.user.UserResponseDTO;
import br.edu.ifgoiano.inove.domain.generic.GenericoRepositoryImplementation;
import br.edu.ifgoiano.inove.domain.model.User;
import br.edu.ifgoiano.inove.domain.model.UserRole;
import br.edu.ifgoiano.inove.domain.repository.custom.UserCustomRepository;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class UserCustomRepositoryImpl extends GenericoRepositoryImplementation implements UserCustomRepository{

    @Override
    public Page<UserResponseDTO> findByRole(UserRole userRole, Pageable pageable) {
        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<UserResponseDTO> cq = cb.createQuery(UserResponseDTO.class);

        Root<User> userRoot = cq.from(User.class);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(userRoot.get("role"), userRole));

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<UserResponseDTO> query = getSession().createQuery(cq);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
    }
}
