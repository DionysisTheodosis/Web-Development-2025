package gr.aegean.icsd.autorepair.car;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CarSpecifications {

    public static Specification<Car> withFilters(
            Long ownerId,
            Long mechanicId,
            String ownerUsername,
            String ownerTaxNumber,
            String brand,
            String model,
            String serialNumber,
            CarType carType,
            FuelType fuelType,
            LocalDate productionDateFrom,
            LocalDate productionDateTo,
            String keyword
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (ownerId != null) {
                predicates.add(cb.equal(root.get("owner").get("id"), ownerId));
            }

            if (mechanicId != null) {
                Join<Object, Object> appointmentJoin = root.join("appointments");
                predicates.add(cb.equal(appointmentJoin.get("assignedMechanic").get("id"), mechanicId));
                if (query != null) {
                    query.distinct(true);
                }
            }

            if (ownerUsername != null) {
                predicates.add(cb.equal(root.get("owner").get("username"), ownerUsername));
            }

            if (ownerTaxNumber != null) {
                predicates.add(cb.equal(root.get("owner").get("taxNumber"), ownerTaxNumber));
            }

            if (isNotBlank(brand)) {
                predicates.add(cb.equal(root.get("brand"), brand));
            }

            if (isNotBlank(model)) {

                predicates.add(cb.equal(root.get("model"), model));
            }

            if (isNotBlank(serialNumber)) {
                predicates.add(cb.equal(root.get("serialNumber"), serialNumber));
            }

            if (carType != null) {
                predicates.add(cb.equal(root.get("carType"), carType));
            }

            if (fuelType != null) {
                predicates.add(cb.equal(root.get("fuelType"), fuelType));
            }

            if (productionDateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("productionDate"), productionDateFrom));
            }
            if (productionDateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("productionDate"), productionDateTo));
            }

            if (isNotBlank(keyword)) {
                addKeywordSearchPredicates(root, cb, predicates, keyword);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }

    private static void addKeywordSearchPredicates(
            Root<Car> root, CriteriaBuilder cb, List<Predicate> predicates, String keyword
    ) {
        String keywordLower = "%" + keyword.toLowerCase() + "%";

        Predicate brandLike = cb.like(cb.lower(root.get("brand")), keywordLower);
        Predicate modelLike = cb.like(cb.lower(root.get("model")), keywordLower);
        Predicate serialLike = cb.like(cb.lower(root.get("serialNumber")), keywordLower);

        Predicate ownerLastNameLike = cb.like(
                cb.lower(root.get("owner").get("lastName")),
                keywordLower
        );

        Predicate ownerTaxNumberLike = cb.like(
                cb.lower(root.get("owner").get("taxNumber")),
                keywordLower
        );

        Predicate ownerUsernameLike = cb.like(
                cb.lower(root.get("owner").get("username")),
                keywordLower
        );

        predicates.add(cb.or(
                brandLike,
                modelLike,
                serialLike,
                ownerLastNameLike,
                ownerTaxNumberLike,
                ownerUsernameLike
        ));
    }
}