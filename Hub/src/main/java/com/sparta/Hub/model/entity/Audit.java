package com.sparta.Hub.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Audit {

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    private String deletedBy;

    private LocalDateTime deletedAt;

    @Column(name = "is_deleted")
    private boolean deleted;

    public void updateCreatedByAndLastModifiedBy(String username) {
        createdBy = username;
        lastModifiedAt= LocalDateTime.now();
        lastModifiedBy = username;
    }

    public void updateDeleted(String username) {
        deletedBy = username;
        deletedAt = LocalDateTime.now();
        deleted = true;
    }
}