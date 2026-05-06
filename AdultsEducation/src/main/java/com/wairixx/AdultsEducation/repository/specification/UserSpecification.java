package com.wairixx.AdultsEducation.repository.specification;

import com.wairixx.AdultsEducation.model.dto.user.UserFilter;
import com.wairixx.AdultsEducation.model.entity.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static com.wairixx.AdultsEducation.repository.specification.SpecificationUtils.*;

public final class UserSpecification {
    private UserSpecification() {}

    public static Specification<User> withFilter(UserFilter f) {
        if (f == null) return alwaysTrue();
        List<Specification<User>> specs = new ArrayList<>();
        addIfNotNull(specs, likeIgnoreCase("email", f.email()));
        if (f.role() != null) specs.add((r, q, cb) -> cb.equal(r.get("role"), f.role()));
        if (f.active() != null) specs.add((r, q, cb) -> cb.equal(r.get("active"), f.active()));
        return specs.isEmpty() ? alwaysTrue() : Specification.allOf(specs);
    }
}