package com.wairixx.AdultsEducation.controller;

import com.wairixx.AdultsEducation.facade.UserFacade;
import com.wairixx.AdultsEducation.model.dto.user.UpdateUserRequest;
import com.wairixx.AdultsEducation.model.dto.user.UserFilter;
import com.wairixx.AdultsEducation.model.dto.user.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Users", description = "User management (ADMIN only)")
public class UserController {

    private final UserFacade userFacade;

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userFacade.getById(id));
    }

    @GetMapping
    @Operation(summary = "List users with filters")
    public ResponseEntity<Page<UserResponse>> getAll(
            @ParameterObject UserFilter filter,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(userFacade.getAll(filter, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user (email, role, active, etc.)")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userFacade.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userFacade.delete(id);
        return ResponseEntity.noContent().build();
    }
}