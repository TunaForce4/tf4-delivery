package com.tf4.delivery.spec;

import com.tf4.delivery.entity.Delivery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DeliverySpecifications {

    private DeliverySpecifications() {}

    public static Specification<Delivery> searchAllFields(String q) {
        return (root, query, cb) -> {
            // q가 비어있으면 항상 true 반환
            if (q == null || q.isBlank()) return cb.conjunction();

            final String qTrim = q.trim();
            final String like = "%" + qTrim.toLowerCase() + "%";

            List<Predicate> orList = new ArrayList<>();

            // 1) 문자열 컬럼 부분일치 (대소문자 무시)
            // 컬럼명은 엔티티 필드명과 일치시켜 주세요.
            orList.add(cb.like(cb.lower(root.get("status")), like));
            orList.add(cb.like(cb.lower(root.get("deliveryAddress")), like));
            orList.add(cb.like(cb.lower(root.get("receivedSlackId")), like));

            // 2) 숫자(정확 매치) — 수령인/담당자 등 정수 컬럼
            if (qTrim.chars().allMatch(Character::isDigit)) {
                try {
                    long n = Long.parseLong(qTrim);
                    orList.add(cb.equal(root.get("receivedUserId"), n));
                    orList.add(cb.equal(root.get("companyDeliveryAgentId"), n));
                } catch (NumberFormatException ignore) {}
            }

            // 3) 전화번호 정규화 검색 (필드명이 있으면 주석 해제해서 사용)
            // Expression<String> phoneNorm = cb.function(
            //     "regexp_replace", String.class,
            //     root.get("phoneNumber"), cb.literal("[^0-9]"), cb.literal(""), cb.literal("g")
            // );
            // String qNum = qTrim.replaceAll("[^0-9]", "");
            // if (!qNum.isEmpty()) {
            //     orList.add(cb.like(cb.lower(phoneNorm), "%" + qNum.toLowerCase() + "%"));
            // }

            // 4) UUID 정확 매치(인덱스 활용)
            tryParseUuid(qTrim).ifPresent(uuid -> {
                orList.add(cb.equal(root.get("deliveryId"), uuid));
                orList.add(cb.equal(root.get("orderId"), uuid));
                orList.add(cb.equal(root.get("departureHubId"), uuid));
                orList.add(cb.equal(root.get("arrivalHubId"), uuid));
            });

            // 5) UUID 문자열 LIKE (보조; 인덱스 못 탐 → 과도한 사용 주의)
            Expression<String> deliveryIdStr = cb.lower(cb.toString(root.get("deliveryId")));
            Expression<String> orderIdStr    = cb.lower(cb.toString(root.get("orderId")));
            Expression<String> depHubIdStr   = cb.lower(cb.toString(root.get("departureHubId")));
            Expression<String> arrHubIdStr   = cb.lower(cb.toString(root.get("arrivalHubId")));
            orList.add(cb.like(deliveryIdStr, like));
            orList.add(cb.like(orderIdStr, like));
            orList.add(cb.like(depHubIdStr, like));
            orList.add(cb.like(arrHubIdStr, like));

            // 6) (선택) 너무 짧은 검색어(예: 1자)는 폭검색 방지
            // if (qTrim.length() < 2 && tryParseUuid(qTrim).isEmpty() && !qTrim.chars().allMatch(Character::isDigit)) {
            //     return cb.disjunction(); // 결과 0건. 필요 시 400 에러로 바꿔도 됨.
            // }

            return cb.or(orList.toArray(new Predicate[0]));
        };
    }

    private static Optional<UUID> tryParseUuid(String s) {
        try { return Optional.of(UUID.fromString(s)); }
        catch (Exception e) { return Optional.empty(); }
    }
}
