package com.user_service.dto.filter;

import com.user_service.entity.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> withFilters(UserFilterDto filter) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (filter.getUsername() != null && !filter.getUsername().isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("username")),
                                "%" + filter.getUsername().toLowerCase() + "%"));
            }

            if (filter.getFirstname() != null && !filter.getFirstname().isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("firstname")),
                                "%" + filter.getFirstname().toLowerCase() + "%"));
            }

            if (filter.getLastname() != null && !filter.getLastname().isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("lastname")),
                                "%" + filter.getLastname().toLowerCase() + "%"));
            }

            if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("email")),
                                "%" + filter.getEmail().toLowerCase() + "%"));
            }

            if (filter.getRole() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("role"), filter.getRole()));
            }

            if (filter.getIsActive() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("isActive"), filter.getIsActive()));
            }

            return predicate;
        };
    }


}
