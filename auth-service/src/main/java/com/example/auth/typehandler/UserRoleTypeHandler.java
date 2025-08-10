package com.example.auth.typehandler;

import com.example.common.enums.UserRole;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 将数据库中的角色字符串代码（如 "agent"）与枚举 UserRole 互相转换的类型处理器。
 */
public class UserRoleTypeHandler extends BaseTypeHandler<UserRole> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UserRole parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter == null ? null : parameter.getCode());
    }

    @Override
    public UserRole getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String code = rs.getString(columnName);
        return toEnum(code);
    }

    @Override
    public UserRole getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String code = rs.getString(columnIndex);
        return toEnum(code);
    }

    @Override
    public UserRole getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String code = cs.getString(columnIndex);
        return toEnum(code);
    }

    private UserRole toEnum(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        return UserRole.fromCode(code);
    }
}

