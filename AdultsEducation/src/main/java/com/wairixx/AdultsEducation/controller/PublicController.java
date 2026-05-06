package com.wairixx.AdultsEducation.controller;

import com.wairixx.AdultsEducation.model.dto.user.UserResponse;
import com.wairixx.AdultsEducation.facade.PublicFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Tag(name = "Public", description = "Public endpoints")
public class PublicController {

    private final PublicFacade publicFacade;

    @GetMapping("/teachers/{id}")
    @Operation(summary = "Get teacher public profile")
    public ResponseEntity<UserResponse> getTeacher(@PathVariable Long id) {
        return ResponseEntity.ok(publicFacade.getTeacherById(id));
    }
}