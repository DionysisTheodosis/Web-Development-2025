package gr.aegean.icsd.autorepair.user;

import gr.aegean.icsd.autorepair.user.customer.Customer;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSpecifications {

    public static Specification<User> withFilters(
            String username,
            String lastName,
            String taxNumber,
            String keyword,
            UserRole role
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role));
            }

            boolean hasSpecificFilters = false;

            if (isNotBlank(username)) {
                predicates.add(cb.equal(root.get("username"), username));
                hasSpecificFilters = true;
            }

            if (isNotBlank(lastName)) {
                predicates.add(cb.equal(root.get("lastName"), lastName));
                hasSpecificFilters = true;
            }

            if (isNotBlank(taxNumber)) {
                predicates.add(cb.equal(
                        cb.treat(root, Customer.class).get("taxNumber"),
                        taxNumber
                ));
                hasSpecificFilters = true;
            }

            if (!hasSpecificFilters && isNotBlank(keyword)) {
                addKeywordSearchPredicates(root, cb, predicates, keyword);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }



    private static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }

    private static void addKeywordSearchPredicates(
            Root<User> root, CriteriaBuilder cb, List<Predicate> predicates, String keyword
    ) {
        if (isNotBlank(keyword)) {
            String keywordLower = "%" + keyword.toLowerCase() + "%";

            Predicate usernameLike = cb.like(cb.lower(root.get("username")), keywordLower);
            Predicate lastNameLike = cb.like(cb.lower(root.get("lastName")), keywordLower);


            Predicate taxNumberLike = cb.like(
                    cb.lower(cb.treat(root, Customer.class).get("taxNumber")),
                    keywordLower
            );

            // Combine with OR
            predicates.add(cb.or(usernameLike, lastNameLike, taxNumberLike));
        }
    }


    public static Specification<User> withKeywordSearch(String keyword) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            addKeywordSearchPredicates(root, cb, predicates, keyword);
            return predicates.isEmpty()
                    ? cb.conjunction()
                    : cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<User> withKeywordSearchInactiveUsers(String keyword) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            addKeywordSearchPredicates(root, cb, predicates, keyword);
            predicates.add(cb.equal(root.get("active"), false));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}