package com.prasac.userservice.mapper;

import com.prasac.userservice.entity.User;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface UserMapper {
    User selectById(Long id);

    User selectByUsername(String username);

    List<User> selectAll();

    int insert(User user);

    int update(User user);

    int deleteById(Long id);
}
