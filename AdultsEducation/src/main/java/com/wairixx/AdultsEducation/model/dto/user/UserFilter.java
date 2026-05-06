package com.wairixx.AdultsEducation.model.dto.user;
import com.wairixx.AdultsEducation.model.enums.Role;
public record UserFilter(String email, Role role, Boolean active) {}