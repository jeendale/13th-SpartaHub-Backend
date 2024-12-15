package com.sparta.company.model.repository;

import com.sparta.company.domain.dto.response.CompanyResponseDto;
import com.sparta.company.model.entity.CompanyType;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyRepositoryCustom {
    Page<CompanyResponseDto> searchCompanies(String companyName, CompanyType companyType, UUID hubID, Pageable pageable);
}
