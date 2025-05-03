package gr.aegean.icsd.autorepair.user;

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
            String username,String lastName,String taxNumber
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (username != null) {
                predicates.add(cb.equal(root.get("username"),username));
            }
            if (lastName != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("lastName"), lastName));
            }
            if (taxNumber != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("taxNumber"), taxNumber));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    private static void addKeywordSearchPredicates(Root<User> root, CriteriaBuilder cb, List<Predicate> predicates, String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            String keywordLower = keyword.toLowerCase() + "%";
            predicates.add(cb.like(cb.lower(root.get("username")), keywordLower));
            predicates.add(cb.like(cb.lower(root.get("lastName")), keywordLower));
            predicates.add(cb.like(cb.lower(root.get("taxNumber")), keywordLower));
        }
    }

    public static Specification<User> withKeywordSearch(String keyword) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            addKeywordSearchPredicates(root, cb, predicates, keyword);
            if (predicates.isEmpty()) {
                return cb.conjunction(); // Επιστρέφουμε conjunction αν δεν υπάρχουν άλλα predicates
            }
            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<User> withKeywordSearchInactiveUsers(String keyword) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            addKeywordSearchPredicates(root, cb, predicates, keyword);
            predicates.add(cb.equal(root.get("active"), false));
            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }
}

