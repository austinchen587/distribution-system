package com.example.data.mapper;

import com.example.data.entity.User;
import com.example.data.permission.DataPermission;
import com.example.data.permission.OperationType;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问接口
 *
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-03
 */
@Repository
@Mapper
public interface UserMapper {

    /**
     * 插入新用户
     *
     * @param user 用户实体
     * @return 影响行数
     */
    @DataPermission(table = "users", operation = OperationType.CREATE, description = "创建新用户")
    @Insert("INSERT INTO users (username, email, phone, password, role, status, commission_rate, parent_id, " +
            "last_login_at, created_at, updated_at) VALUES (#{username}, #{email}, #{phone}, #{password}, " +
            "#{role}, #{status}, #{commissionRate}, #{parentId}, #{lastLoginAt}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 根据ID查找用户
     *
     * @param id 用户ID
     * @return 用户实体
     */
    @DataPermission(table = "users", operation = OperationType.READ, description = "根据ID查询用户")
    Optional<User> findById(@Param("id") Long id);

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    @DataPermission(table = "users", operation = OperationType.READ, description = "根据用户名查询用户")
    Optional<User> findByUsername(@Param("username") String username);

    /**
     * 根据邮箱查找用户
     *
     * @param email 邮箱地址
     * @return 用户实体
     */
    @DataPermission(table = "users", operation = OperationType.READ, description = "根据邮箱查询用户")
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * 根据手机号查找用户
     *
     * @param phone 手机号
     * @return 用户实体
     */
    @DataPermission(table = "users", operation = OperationType.READ, description = "根据手机号查询用户")
    Optional<User> findByPhone(@Param("phone") String phone);

    /**
     * 根据角色查找用户列表
     *
     * @param role 用户角色
     * @return 用户列表
     */
    @DataPermission(table = "users", operation = OperationType.READ, description = "根据角色查询用户列表")
    List<User> findByRole(@Param("role") String role);

    /**
     * 根据状态查找用户列表
     *
     * @param status 用户状态
     * @return 用户列表
     */
    @DataPermission(table = "users", operation = OperationType.READ, description = "根据状态查询用户列表")
    List<User> findByStatus(@Param("status") String status);

    /**
     * 查找所有用户（分页）
     *
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 用户列表
     */
    @DataPermission(table = "users", operation = OperationType.READ, description = "分页查询所有用户")
    List<User> findAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 统计用户总数
     *
     * @return 用户总数
     */
    @DataPermission(table = "users", operation = OperationType.STATS, description = "统计用户总数")
    @Select("SELECT COUNT(*) FROM users")
    long count();

    /**
     * 根据条件统计用户数量（支持角色/状态/parentId/keyword/时间范围）
     */
    @DataPermission(table = "users", operation = OperationType.STATS, description = "按条件统计用户数量")
    long countByConditions(@Param("role") String role,
                           @Param("status") String status,
                           @Param("parentId") Long parentId,
                           @Param("keyword") String keyword,
                           @Param("dateFrom") String dateFrom,
                           @Param("dateTo") String dateTo);

    /**
     * 更新用户信息
     *
     * @param user 用户实体
     * @return 影响行数
     */
    @DataPermission(table = "users", operation = OperationType.UPDATE, description = "更新用户信息")
    @Update("UPDATE users SET username = #{username}, email = #{email}, phone = #{phone}, " +
            "role = #{role}, status = #{status}, commission_rate = #{commissionRate}, " +
            "parent_id = #{parentId}, last_login_at = #{lastLoginAt}, updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    int update(User user);

    /**
     * 更新用户密码
     *
     * @param id 用户ID
     * @param password 新密码
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @DataPermission(table = "users", operation = OperationType.UPDATE, description = "更新用户密码")
    @Update("UPDATE users SET password = #{password}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password,
                      @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 更新用户最后登录时间
     *
     * @param id 用户ID
     * @param lastLoginAt 最后登录时间
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @DataPermission(table = "users", operation = OperationType.UPDATE, description = "更新用户最后登录时间")
    @Update("UPDATE users SET last_login_at = #{lastLoginAt}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateLastLogin(@Param("id") Long id, @Param("lastLoginAt") LocalDateTime lastLoginAt,
                       @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 更新用户状态
     *
     * @param id 用户ID
     * @param status 新状态
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @DataPermission(table = "users", operation = OperationType.UPDATE, description = "更新用户状态")
    @Update("UPDATE users SET status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status,
                    @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 根据ID删除用户（硬删除）
     *
     * @param id 用户ID
     * @return 影响行数
     */
    @DataPermission(table = "users", operation = OperationType.DELETE, description = "删除用户")
    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @param excludeId 排除的用户ID（用于更新时检查）
     * @return 是否存在
     */
    @DataPermission(table = "users", operation = OperationType.READ, description = "检查用户名是否存在")
    @Select("SELECT COUNT(*) > 0 FROM users WHERE username = #{username} " +
            "AND (#{excludeId} IS NULL OR id != #{excludeId})")
    boolean existsByUsername(@Param("username") String username, @Param("excludeId") Long excludeId);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @param excludeId 排除的用户ID（用于更新时检查）
     * @return 是否存在
     */
    @DataPermission(table = "users", operation = OperationType.READ, description = "检查邮箱是否存在")
    @Select("SELECT COUNT(*) > 0 FROM users WHERE email = #{email} " +
            "AND (#{excludeId} IS NULL OR id != #{excludeId})")
    boolean existsByEmail(@Param("email") String email, @Param("excludeId") Long excludeId);

    /**
     * 检查手机号是否存在
     *
     * @param phone 手机号
     * @param excludeId 排除的用户ID（用于更新时检查）
     * @return 是否存在
     */
    @DataPermission(table = "users", operation = OperationType.READ, description = "检查手机号是否存在")
    @Select("SELECT COUNT(*) > 0 FROM users WHERE phone = #{phone} " +
            "AND (#{excludeId} IS NULL OR id != #{excludeId})")
    boolean existsByPhone(@Param("phone") String phone, @Param("excludeId") Long excludeId);

    /**
     * 根据上级ID查找下级用户
     *
     * @param parentId 上级用户ID
     * @return 下级用户列表
     */
    @DataPermission(table = "users", operation = OperationType.READ, description = "查询下级用户列表")
    @Select("SELECT * FROM users WHERE parent_id = #{parentId} ORDER BY created_at DESC")
    List<User> findByParentId(@Param("parentId") Long parentId);

    /**
     * 根据关键词搜索用户
     *
     * @param keyword 关键词
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 用户列表
     */
    @DataPermission(table = "users", operation = OperationType.READ, description = "根据关键词搜索用户")
    List<User> searchUsers(@Param("keyword") String keyword,
                           @Param("parentId") Long parentId,
                           @Param("dateFrom") String dateFrom,
                           @Param("dateTo") String dateTo,
                           @Param("offset") int offset,
                           @Param("limit") int limit);

    /**
     * 根据条件查找用户列表
     *
     * @param role 角色（可选）
     * @param status 状态（可选）
     * @param parentId 上级ID（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 用户列表
     */
    @DataPermission(table = "users", operation = OperationType.READ, description = "按条件查询用户列表")
    List<User> findByConditions(@Param("role") String role,
                                @Param("status") String status,
                                @Param("parentId") Long parentId,
                                @Param("keyword") String keyword,
                                @Param("dateFrom") String dateFrom,
                                @Param("dateTo") String dateTo,
                                @Param("offset") int offset,
                                @Param("limit") int limit);

    /** 统计：最近N天每日新增用户 */
    @DataPermission(table = "users", operation = OperationType.STATS, description = "最近N天每日新增用户")
    java.util.List<java.util.Map<String, Object>> countDailyNewUsers(@Param("days") int days);

    /** 统计：最近N天每日新增用户（按角色分组） */
    @DataPermission(table = "users", operation = OperationType.STATS, description = "最近N天每日新增用户-按角色分组")
    java.util.List<java.util.Map<String, Object>> countDailyNewUsersByRole(@Param("days") int days);

    /** 统计：最近N天每日新增用户（按状态分组） */
    @DataPermission(table = "users", operation = OperationType.STATS, description = "最近N天每日新增用户-按状态分组")
    java.util.List<java.util.Map<String, Object>> countDailyNewUsersByStatus(@Param("days") int days);
}