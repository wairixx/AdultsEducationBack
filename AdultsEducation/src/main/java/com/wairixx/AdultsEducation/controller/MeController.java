package com.wairixx.AdultsEducation.controller;

import com.wairixx.AdultsEducation.facade.MeFacade;
import com.wairixx.AdultsEducation.model.dto.user.ChangePasswordRequest;
import com.wairixx.AdultsEducation.model.dto.user.UpdateMeRequest;
import com.wairixx.AdultsEducation.model.dto.user.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Me", description = "Current user profile")
public class MeController {

    private final MeFacade meFacade;

    @GetMapping
    @Operation(summary = "Get my profile")
    public ResponseEntity<UserResponse> getMyProfile() {
        return ResponseEntity.ok(meFacade.getMyProfile());
    }

    @PutMapping
    @Operation(summary = "Update my profile")
    public ResponseEntity<UserResponse> updateMyProfile(@Valid @RequestBody UpdateMeRequest request) {
        return ResponseEntity.ok(meFacade.updateMyProfile(request));
    }

    @PutMapping("/password")
    @Operation(summary = "Change my password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        meFacade.changeMyPassword(request);
        return ResponseEntity.noContent().build();
    }
}