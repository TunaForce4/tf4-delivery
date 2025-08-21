package com.tf4.delivery.spec;

import com.tf4.delivery.entity.DeliveryRouteLeg;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DeliveryRouteLegSpecifications {

    private DeliveryRouteLegSpecifications() {}
    private static final double EPS = 1e-6;

    public static Specification<DeliveryRouteLeg> searchAllFields(String q) {
        return (root, query, cb) -> {
            // q가 비어있으면 항상 true 반환
            Predicate notDeleted = cb.isNull(root.get("deletedAt"));

            if (q == null || q.isBlank()) {
                return notDeleted; // 전체 조회도 삭제건 제외
            }

            final String qTrim = q.trim();
            final String like = "%" + qTrim.toLowerCase() + "%";

            List<Predicate> orList = new ArrayList<>();

            // 1) 문자열 컬럼 부분일치 (대소문자 무시)thod parameter 'routeLegId': Failed to convert value of type 'java.lang.String' to required type 'java.util.
            orList.add(cb.like(cb.lower(root.get("status")), like));

            // 2) 숫자(정확 매치) — 수령인/담당자 등 정수 컬럼
            if (qTrim.chars().allMatch(Character::isDigit)) {
                try {
                    long n = Long.parseLong(qTrim);
                    orList.add(cb.equal(root.get("hubDeliveryAgentId"), n));
                } catch (NumberFormatException ignore) {}
            }
            // 3) 실수/소수 매치
            tryParseBigDecimal(qTrim).ifPresent(dec -> {
                // 예) 거리(km), 시간(시간 단위) 같은 부동소수
                double v = dec.doubleValue();
                for (String f : List.of("estimatedDistanceKm",
                                        "estimatedTimeMin",
                                        "actualDistanceKm",
                                        "actualTimeMin")) {
                    Expression<Double> expr = root.get(f).as(Double.class);
                    orList.add(cb.between(expr, v - EPS, v + EPS));
                }
            });

            // 4) 날짜(yyyy-MM-dd) → 하루 범위 검색
            tryParseLocalDate(qTrim).ifPresent(d -> {
                var start = d.atStartOfDay();
                var endExclusive = d.plusDays(1).atStartOfDay();

                // createdAt
                orList.add(cb.and(
                        cb.greaterThanOrEqualTo(root.get("createdAt"), start),
                        cb.lessThan(root.get("createdAt"), endExclusive)
                ));
                // updatedAt
                orList.add(cb.and(
                        cb.greaterThanOrEqualTo(root.get("updatedAt"), start),
                        cb.lessThan(root.get("updatedAt"), endExclusive)
                ));
            });

            // (선택) 날짜시간(yyyy-MM-ddTHH:mm) 정확 매치 근사
            tryParseLocalDateTime(qTrim).ifPresent(dt -> {
                var start = dt.minusSeconds(1);
                var end = dt.plusSeconds(1);
                orList.add(cb.between(root.get("createdAt"), start, end));
                orList.add(cb.between(root.get("updatedAt"), start, end));
            });


            // 5) UUID 정확 매치(인덱스 활용)
            tryParseUuid(qTrim).ifPresent(uuid -> {
                orList.add(cb.equal(root.get("routeLegId"), uuid));
                orList.add(cb.equal(root.get("deliveryId"), uuid));
                orList.add(cb.equal(root.get("departureHubId"), uuid));
                orList.add(cb.equal(root.get("arrivalHubId"), uuid));
            });

            // 6) UUID 문자열 LIKE (보조; 인덱스 못 탐 → 과도한 사용 주의)
            Expression<String> routeLegIdStr = cb.lower(cb.toString(root.get("routeLegId")));
            Expression<String> deliveryIdStr    = cb.lower(cb.toString(root.get("deliveryId")));
            Expression<String> depHubIdStr   = cb.lower(cb.toString(root.get("departureHubId")));
            Expression<String> arrHubIdStr   = cb.lower(cb.toString(root.get("arrivalHubId")));
            orList.add(cb.like(routeLegIdStr, like));
            orList.add(cb.like(deliveryIdStr, like));
            orList.add(cb.like(depHubIdStr, like));
            orList.add(cb.like(arrHubIdStr, like));

            // 7) (선택) 너무 짧은 검색어(예: 1자)는 폭검색 방지
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

    private static Optional<java.math.BigDecimal> tryParseBigDecimal(String s) {
        try {
            return s.matches("[-+]?\\d+(\\.\\d+)?") ? Optional.of(new java.math.BigDecimal(s)) : Optional.empty();
        } catch (Exception e) { return Optional.empty(); }
    }

    private static Optional<java.time.LocalDate> tryParseLocalDate(String s) {
        try { return Optional.of(java.time.LocalDate.parse(s)); }
        catch (Exception e) { return Optional.empty(); }
    }
    private static Optional<java.time.LocalDateTime> tryParseLocalDateTime(String s) {
        try { return Optional.of(java.time.LocalDateTime.parse(s)); } // expects 'yyyy-MM-ddTHH:mm[:ss]'
        catch (Exception e) { return Optional.empty(); }
    }
}