package com.example.data.mapper;

import com.example.data.entity.Product;
import com.example.data.permission.DataPermission;
import com.example.data.permission.OperationType;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 商品信息数据访问接口
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-03
 */
@Repository
@Mapper
public interface ProductMapper {
    
    /**
     * 插入新商品
     * 
     * @param product 商品实体
     * @return 影响行数
     */
    @DataPermission(table = "products", operation = OperationType.CREATE, description = "创建商品")
    @Insert("INSERT INTO products (product_name, description, price, category, status, created_at, updated_at) " +
            "VALUES (#{productName}, #{description}, #{price}, #{category}, #{status.code}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);
    
    /**
     * 根据ID查找商品
     * 
     * @param id 商品ID
     * @return 商品实体
     */
    @DataPermission(table = "products", operation = OperationType.READ, description = "查询商品信息")
    @Select("SELECT * FROM products WHERE id = #{id}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    Optional<Product> findById(@Param("id") Long id);
    
    /**
     * 根据商品名称查找商品
     * 
     * @param productName 商品名称
     * @return 商品实体
     */
    @Select("SELECT * FROM products WHERE product_name = #{productName}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    Optional<Product> findByProductName(@Param("productName") String productName);
    
    /**
     * 根据状态查找商品列表
     * 
     * @param status 商品状态
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 商品列表
     */
    @Select("SELECT * FROM products WHERE status = #{status.code} ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Product> findByStatus(@Param("status") Product.ProductStatus status, 
                              @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据分类查找商品列表
     * 
     * @param category 商品分类
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 商品列表
     */
    @Select("SELECT * FROM products WHERE category = #{category} ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Product> findByCategory(@Param("category") String category, 
                                @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据价格范围查找商品列表
     * 
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 商品列表
     */
    @Select("SELECT * FROM products WHERE price >= #{minPrice} AND price <= #{maxPrice} " +
            "ORDER BY price ASC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice,
                                  @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查找可用商品列表（状态为ACTIVE）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 商品列表
     */
    @Select("SELECT * FROM products WHERE status = 'ACTIVE' ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Product> findAvailableProducts(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查找所有商品（分页）
     * 
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 商品列表
     */
    @Select("SELECT * FROM products ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
    @Results({
        @Result(property = "status", column = "status", 
                typeHandler = org.apache.ibatis.type.EnumTypeHandler.class)
    })
    List<Product> findAll(@Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 查找所有分类
     * 
     * @return 分类列表
     */
    @Select("SELECT DISTINCT category FROM products WHERE category IS NOT NULL ORDER BY category")
    List<String> findAllCategories();
    
    /**
     * 统计商品总数
     * 
     * @return 商品总数
     */
    @Select("SELECT COUNT(*) FROM products")
    long count();
    
    /**
     * 根据状态统计商品数量
     * 
     * @param status 商品状态
     * @return 商品数量
     */
    @Select("SELECT COUNT(*) FROM products WHERE status = #{status.code}")
    long countByStatus(@Param("status") Product.ProductStatus status);
    
    /**
     * 根据分类统计商品数量
     * 
     * @param category 商品分类
     * @return 商品数量
     */
    @Select("SELECT COUNT(*) FROM products WHERE category = #{category}")
    long countByCategory(@Param("category") String category);
    
    /**
     * 根据条件统计商品数量
     * 
     * @param category 分类（可选）
     * @param status 状态（可选）
     * @param minPrice 最低价格（可选）
     * @param maxPrice 最高价格（可选）
     * @return 商品数量
     */
    long countByConditions(@Param("category") String category, @Param("status") String status,
                          @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    /**
     * 更新商品信息
     * 
     * @param product 商品实体
     * @return 影响行数
     */
    @Update("UPDATE products SET product_name = #{productName}, description = #{description}, price = #{price}, " +
            "category = #{category}, status = #{status.code}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(Product product);
    
    /**
     * 更新商品状态
     * 
     * @param id 商品ID
     * @param status 新状态
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE products SET status = #{status.code}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Product.ProductStatus status,
                    @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 更新商品价格
     * 
     * @param id 商品ID
     * @param price 新价格
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE products SET price = #{price}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updatePrice(@Param("id") Long id, @Param("price") BigDecimal price,
                   @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 根据ID删除商品（硬删除）
     * 
     * @param id 商品ID
     * @return 影响行数
     */
    @Delete("DELETE FROM products WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    /**
     * 检查商品名称是否存在
     * 
     * @param productName 商品名称
     * @param excludeId 排除的商品ID（用于更新时检查）
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM products WHERE product_name = #{productName} " +
            "AND (#{excludeId} IS NULL OR id != #{excludeId})")
    boolean existsByProductName(@Param("productName") String productName, @Param("excludeId") Long excludeId);
    
    /**
     * 根据关键词搜索商品
     * 
     * @param keyword 关键词
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 商品列表
     */
    List<Product> searchProducts(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 根据条件查找商品列表
     * 
     * @param category 分类（可选）
     * @param status 状态（可选）
     * @param minPrice 最低价格（可选）
     * @param maxPrice 最高价格（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 商品列表
     */
    List<Product> findByConditions(@Param("category") String category, @Param("status") String status,
                                  @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice,
                                  @Param("offset") int offset, @Param("limit") int limit);
    
    /**
     * 批量更新商品状态
     * 
     * @param ids 商品ID列表
     * @param status 新状态
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") Product.ProductStatus status,
                         @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 激活商品
     * 
     * @param id 商品ID
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE products SET status = 'ACTIVE', updated_at = #{updatedAt} WHERE id = #{id}")
    int activate(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 停用商品
     * 
     * @param id 商品ID
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE products SET status = 'INACTIVE', updated_at = #{updatedAt} WHERE id = #{id}")
    int deactivate(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);
}