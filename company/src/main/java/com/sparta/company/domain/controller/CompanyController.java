package com.sparta.company.domain.controller;

import com.sparta.company.domain.dto.request.CompanyRequestDto;
import com.sparta.company.domain.dto.request.UpdateCompanyRequestDto;
import com.sparta.company.domain.dto.response.CompanyIdResponseDto;
import com.sparta.company.domain.dto.response.CompanyResponseDto;
import com.sparta.company.domain.service.CompanyService;
import com.sparta.company.model.entity.CompanyType;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<CompanyIdResponseDto> createCompany(
            @RequestBody CompanyRequestDto requestDto,
            @RequestHeader("X-User-Username") String requestUsername,
            @RequestHeader("X-User-Role") String requestRole) {

        CompanyIdResponseDto responseDto = companyService.createCompany(requestDto, requestUsername, requestRole);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDto> getCompany(@PathVariable UUID companyId) {

        CompanyResponseDto responseDto = companyService.getCompany(companyId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping
    public ResponseEntity<PagedModel<CompanyResponseDto>> getCompanies(
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) CompanyType companyType,
            @RequestParam(required = false) UUID hubId,
            Pageable pageable) {

        Page<CompanyResponseDto> responseDtos = companyService.getCompanies(companyName, companyType,hubId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(new PagedModel<>(responseDtos));
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyIdResponseDto> updateCompany(
            @PathVariable UUID companyId,
            @RequestBody UpdateCompanyRequestDto requestDto,
            @RequestHeader("X-User-Username") String requestUsername,
            @RequestHeader("X-User-Role") String requestRole) {

        CompanyIdResponseDto responseDto = companyService.updateCompany(
                companyId, requestDto, requestUsername, requestRole);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<CompanyIdResponseDto> deleteCompany(
            @PathVariable UUID companyId,
            @RequestHeader("X-User-Username") String requestUsername,
            @RequestHeader("X-User-Role") String requestRole) {

        CompanyIdResponseDto responseDto = companyService.deleteCompany(companyId, requestUsername, requestRole);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
