package com.sparta.user.domain.controller.swagger;

import com.sparta.user.domain.dto.request.UpdateUserRequestDto;
import com.sparta.user.domain.dto.response.UserResponseDto;
import com.sparta.user.domain.dto.response.UsernameResponseDto;
import com.sparta.user.model.entity.UserRoleEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "User", description = "User API")
public interface UserControllerDocs {

    @Operation(summary = "회원조회", description = "회원정보 조회를 위한 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 조회 성공", content = @Content(schema = @Schema(implementation = UserResponseDto.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = {
                            @Content(mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(name = "회원이 존재하지 않습니다", value = "{\"errorMessage\":\"해당 유저를 찾을 수 없습니다.\"}"),
                                            @ExampleObject(name = "삭제된 회원입니다", value = "{\"errorMessage\":\"탈퇴한 회원입니다.\"}"),
                                            @ExampleObject(name = "접근 권한이 없습니다", value = "{\"errorMessage\":\"접근 권한이 없습니다.\"}")
                                    })
                    })

    })
    @GetMapping("/{username}")
    ResponseEntity<UserResponseDto> getUser(
            @RequestHeader(value = "X-User-Username") String requestUsername,
            @RequestHeader(value = "X-User-Role") UserRoleEnum requestRole,
            @PathVariable String username);


    @Operation(summary = "회원검색", description = "회원정보 검색을 위한 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 검색 성공", content = @Content(schema = @Schema(implementation = PagedModel.class))),
    })
    @GetMapping
    ResponseEntity<PagedModel<UserResponseDto>> searchUsers(
            @RequestHeader("X-User-Role") UserRoleEnum requestRole,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @PageableDefault(sort = "createdAt") Pageable pageable);

    @Operation(summary = "회원수정", description = "회원정보 수정을 위한 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 수정 성공", content = @Content(schema = @Schema(implementation = UsernameResponseDto.class))),
    })
    @PutMapping("/{username}")
    ResponseEntity<UsernameResponseDto> updateUser(
            @Valid @RequestBody UpdateUserRequestDto requestDto,
            @RequestHeader("X-User-Role") UserRoleEnum requestRole,
            @PathVariable String username);

    @Operation(summary = "회원삭제", description = "회원정보 삭제를 위한 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 삭제 성공", content = @Content(schema = @Schema(implementation = UsernameResponseDto.class))),
    })
    @DeleteMapping("/{username}")
    ResponseEntity<UsernameResponseDto> deleteUser(
            @RequestHeader("X-User-Role") UserRoleEnum requestRole,
            @PathVariable String username);
}
