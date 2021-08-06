package com.nutritiondesigner.userservice.model.audit;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseTimeEntity {
    @CreatedDate
    @Column(name = "CREATED_DATE",nullable = false, updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Column(name = "MODIFIED_DATE", nullable = false)
    private LocalDateTime lastModifiedDate;
}
