package com.tf4.delivery.spec;

import com.tf4.delivery.entity.DeliveryAgent;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DeliveryAgentSpecifications {

    private DeliveryAgentSpecifications() {}

    public static Specification<DeliveryAgent> searchAllFields(String q) {
        return (root, query, cb) -> {
            // q가 비어있으면 항상 true 반환
            Predicate notDeleted = cb.isNull(root.get("deletedAt"));

            if (q == null || q.isBlank()) {
                return notDeleted; // 전체 조회도 삭제건 제외
            }

            final String qTrim = q.trim();
            final String like = "%" + qTrim.toLowerCase() + "%";

            List<Predicate> orList = new ArrayList<>();

            // 문자열 컬럼 부분일치 (대소문자 무시)
            orList.add(cb.like(cb.lower(root.get("deliveryType")), like));


            // 숫자(정확 매치) — 수령인/담당자 등 정수 컬럼
            if (qTrim.chars().allMatch(Character::isDigit)) {
                try {
                    long n = Long.parseLong(qTrim);
                    orList.add(cb.equal(root.get("userId"), n));
                    orList.add(cb.equal(root.get("deliverySeq"), n));
                } catch (NumberFormatException ignore) {}
            }

            // UUID 정확 매치(인덱스 활용)
            tryParseUuid(qTrim).ifPresent(uuid -> {
                orList.add(cb.equal(root.get("hubId"), uuid));
            });

            // UUID 문자열 LIKE (보조; 인덱스 못 탐 → 과도한 사용 주의)
            Expression<String> hubId = cb.lower(cb.toString(root.get("hubId")));
            orList.add(cb.like(hubId, like));

            // (선택) 너무 짧은 검색어(예: 1자)는 폭검색 방지
            // if (qTrim.length() < 2 && tryParseUuid(qTrim).isEmpty() && !qTrim.chars().allMatch(Character::isDigit)) {
            //     return cb.disjunction(); // 결과 0건. 필요 시 400 에러로 바꿔도 됨.
            // }

            return cb.and(notDeleted, cb.or(orList.toArray(new Predicate[0])));
        };
    }

    private static Optional<UUID> tryParseUuid(String s) {
        try { return Optional.of(UUID.fromString(s)); }
        catch (Exception e) { return Optional.empty(); }
    }
}