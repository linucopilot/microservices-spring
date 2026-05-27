package com.prasac.authservice.mapper;

import com.prasac.authservice.entity.RefreshToken;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefreshTokenMapper {
    RefreshToken selectByToken(String token);

    int insert(RefreshToken refreshToken);

    int revokeByToken(String token);

    int revokeAllByUserId(Long userId);
}
