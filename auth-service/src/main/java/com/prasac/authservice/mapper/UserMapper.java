package com.prasac.authservice.mapper;

import com.prasac.authservice.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectById(Long id);

    User selectByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    int insert(User user);
}
