package com.tf4.delivery.repository.jpa;


import com.tf4.delivery.entity.DeliveryRouteLeg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeliveryRouteLegRepository extends JpaRepository<DeliveryRouteLeg, UUID>,
                                                JpaSpecificationExecutor<DeliveryRouteLeg> {
    DeliveryRouteLeg save(DeliveryRouteLeg deliveryRouteLeg);
}