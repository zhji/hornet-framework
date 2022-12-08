package com.hornetmall.framework.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditingEntity {

    @CreatedBy
    @Column(name = "create_by", length = 20, nullable = false, updatable = false, columnDefinition = "BIGINT(20) COMMENT '公共字段——记录创建人，-1表示系统自动录入'")
    private Long createdBy;

    @CreatedDate
    @Column(name = "create_time", updatable = false, columnDefinition = "DATETIME COMMENT '公共字段——记录创建时间'")
    private LocalDateTime createTime = LocalDateTime.now();

    @LastModifiedBy
    @Column(name = "last_update_by", length = 20, columnDefinition = "BIGINT(20) COMMENT '公共字段——记录最后一次人'")
    private Long lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_update_time", columnDefinition = "DATETIME COMMENT '公共字段——记录最后一次修改时间'")
    private LocalDateTime lastModifiedTime = LocalDateTime.now();
}
