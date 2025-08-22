package com.tf4.delivery.repository.jpa;

import com.tf4.delivery.entity.LogisticsManage;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LogisticsManageRepository extends JpaRepository<LogisticsManage, Long>,
        JpaSpecificationExecutor<LogisticsManage>{
    LogisticsManage save(LogisticsManage logisticsManage);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select lm from LogisticsManage lm where lm.id = :id")
    Optional<LogisticsManage> lockById(@Param("id") Long id);

}
