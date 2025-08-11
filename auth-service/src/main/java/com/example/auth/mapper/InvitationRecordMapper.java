package com.example.auth.mapper;

import com.example.auth.entity.InvitationRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InvitationRecordMapper {
    int insert(InvitationRecord record);
}

