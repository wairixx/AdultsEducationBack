package com.wairixx.AdultsEducation.facade.impl;

import com.wairixx.AdultsEducation.facade.UserFacade;
import com.wairixx.AdultsEducation.model.dto.user.UpdateUserRequest;
import com.wairixx.AdultsEducation.model.dto.user.UserFilter;
import com.wairixx.AdultsEducation.model.dto.user.UserResponse;
import com.wairixx.AdultsEducation.service.UserService;
import com.wairixx.AdultsEducation.util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserService userService;
    private final UserMapper userMapper;

    @Override public UserResponse getById(Long id) { return userMapper.toResponse(userService.getById(id)); }

    @Override
    public Page<UserResponse> getAll(UserFilter filter, Pageable pageable) {
        return userService.getAll(filter, pageable).map(userMapper::toResponse);
    }

    @Override
    public UserResponse update(Long id, UpdateUserRequest request) {
        return userMapper.toResponse(userService.update(id, request));
    }

    @Override public void delete(Long id) { userService.delete(id); }
}