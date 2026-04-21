package com.prasac.userservice.service;

import com.prasac.userservice.entity.User;
import com.prasac.userservice.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;

    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

    public User createUser(User user) {
        user.setActive(true);
        userMapper.insert(user);
        return user;
    }

    public User updateUser(User user) {
        userMapper.update(user);
        return user;
    }

    public boolean deleteUser(Long id) {
        return userMapper.deleteById(id) > 0;
    }
}
