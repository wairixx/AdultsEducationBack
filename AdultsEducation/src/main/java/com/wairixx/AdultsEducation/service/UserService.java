package com.wairixx.AdultsEducation.service;

import com.wairixx.AdultsEducation.model.dto.user.UpdateUserRequest;
import com.wairixx.AdultsEducation.model.dto.user.UserFilter;
import com.wairixx.AdultsEducation.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    User getById(Long id);
    Page<User> getAll(UserFilter filter, Pageable pageable);
    User update(Long id, UpdateUserRequest request);
    void delete(Long id);
}