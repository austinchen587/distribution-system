package com.example.auth.mapper;

import com.example.auth.entity.InvitationCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface InvitationCodeMapper {
    InvitationCode selectByCodeForUpdate(@Param("code") String code);
    int increaseUsage(@Param("code") String code);
    int deactivate(@Param("code") String code);
}

