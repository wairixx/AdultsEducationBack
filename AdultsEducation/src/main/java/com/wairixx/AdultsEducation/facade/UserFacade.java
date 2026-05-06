package com.wairixx.AdultsEducation.facade;

import com.wairixx.AdultsEducation.model.dto.user.UpdateUserRequest;
import com.wairixx.AdultsEducation.model.dto.user.UserFilter;
import com.wairixx.AdultsEducation.model.dto.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserFacade {
    UserResponse getById(Long id);
    Page<UserResponse> getAll(UserFilter filter, Pageable pageable);
    UserResponse update(Long id, UpdateUserRequest request);
    void delete(Long id);
}