package com.prasac.userservice.service;

import com.prasac.userservice.dto.UserRequest;
import com.prasac.userservice.entity.User;
import com.prasac.userservice.exception.ResourceNotFoundException;
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

    public User createUser(UserRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .active(request.getActive() != null ? request.getActive() : Boolean.TRUE)
                .build();
        userMapper.insert(user);
        return user;
    }

    public User updateUser(Long id, UserRequest request) {
        User existingUser = userMapper.selectById(id);
        if (existingUser == null) {
            throw new ResourceNotFoundException("User not found");
        }

        existingUser.setUsername(request.getUsername());
        existingUser.setEmail(request.getEmail());
        existingUser.setFullName(request.getFullName());
        existingUser.setPhone(request.getPhone());
        existingUser.setAddress(request.getAddress());
        existingUser.setActive(request.getActive() != null ? request.getActive() : existingUser.getActive());

        userMapper.update(existingUser);
        return existingUser;
    }

    public boolean deleteUser(Long id) {
        if (userMapper.selectById(id) == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return userMapper.deleteById(id) > 0;
    }
}
