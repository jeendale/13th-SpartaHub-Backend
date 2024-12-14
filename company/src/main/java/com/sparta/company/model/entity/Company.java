package com.sparta.company.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_company")
public class Company extends Audit{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID companyId;

    @Column(nullable = false)
    private UUID hubId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String companyName;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String companyAddress;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private CompanyType companyType;

    public void updateCompany(UUID hubId, String username, String companyName, String companyAddress, CompanyType companyType) {
        this.hubId = hubId;
        this.username = username;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyType = companyType;
    }
}
