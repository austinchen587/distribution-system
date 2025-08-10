package com.example.data.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * String列表类型处理器
 * 
 * 处理List<String>与VARCHAR之间的转换
 * 使用逗号作为分隔符进行序列化和反序列化
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-07
 */
public class StringListTypeHandler extends BaseTypeHandler<List<String>> {
    
    private static final String DELIMITER = ",";
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) 
            throws SQLException {
        if (parameter == null || parameter.isEmpty()) {
            ps.setString(i, "");
        } else {
            // 使用逗号连接字符串列表
            String result = String.join(DELIMITER, parameter);
            ps.setString(i, result);
        }
    }
    
    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String result = rs.getString(columnName);
        return parseStringToList(result);
    }
    
    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String result = rs.getString(columnIndex);
        return parseStringToList(result);
    }
    
    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String result = cs.getString(columnIndex);
        return parseStringToList(result);
    }
    
    /**
     * 解析字符串为列表
     * 
     * @param str 字符串
     * @return 字符串列表
     */
    private List<String> parseStringToList(String str) {
        if (str == null || str.trim().isEmpty()) {
            return Arrays.asList();
        }
        
        return Arrays.stream(str.split(DELIMITER))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}