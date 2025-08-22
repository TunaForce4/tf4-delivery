package com.tf4.delivery.entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "p_logistics_manages")
public class LogisticsManage extends Timestamped {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "current_agent_seq", nullable = false)
    private BigInteger currentAgentSeq;

    @Builder
    public LogisticsManage(Long id, BigInteger currentAgentSeq) {
        this.id = id;
        this.currentAgentSeq = currentAgentSeq;
    }
}
