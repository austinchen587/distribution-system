package com.example.common.dto.reward;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RewardDto 单元测试
 * 
 * @author Test Generator
 * @version 1.0
 * @since 2025-08-04
 */
class RewardDtoTest {

    private Validator validator;
    private RewardDto rewardDto;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        rewardDto = new RewardDto();
    }

    @Test
    @DisplayName("测试RewardDto基本构造器和getter/setter")
    void testBasicConstructorAndGettersSetters() {
        // Test default constructor
        RewardDto dto = new RewardDto();
        assertNotNull(dto);
        
        // Test setters and getters
        dto.setId(1L);
        dto.setAgentId(10L);
        dto.setAgentUsername("agent001");
        dto.setAgentRealName("张三");
        dto.setType("COMMISSION");
        dto.setAmount(new BigDecimal("500.00"));
        dto.setRelatedOrderId(100L);
        dto.setStatus("CONFIRMED");
        dto.setDescription("订单佣金奖励");
        dto.setCalculationBase(new BigDecimal("10000.00"));
        dto.setRewardRate(new BigDecimal("0.05"));
        dto.setCreatedAt("2025-08-04 10:00:00");
        dto.setConfirmedAt("2025-08-04 12:00:00");
        dto.setSettledAt("2025-08-05 15:00:00");
        dto.setSource("ORDER");
        dto.setSourceId(1001L);
        dto.setCalculationRule("按订单金额5%计算");
        dto.setSettlementId(2001L);
        dto.setPaidAt("2025-08-05 16:00:00");
        
        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getAgentId());
        assertEquals("agent001", dto.getAgentUsername());
        assertEquals("张三", dto.getAgentRealName());
        assertEquals("COMMISSION", dto.getType());
        assertEquals(new BigDecimal("500.00"), dto.getAmount());
        assertEquals(100L, dto.getRelatedOrderId());
        assertEquals("CONFIRMED", dto.getStatus());
        assertEquals("订单佣金奖励", dto.getDescription());
        assertEquals(new BigDecimal("10000.00"), dto.getCalculationBase());
        assertEquals(new BigDecimal("0.05"), dto.getRewardRate());
        assertEquals("2025-08-04 10:00:00", dto.getCreatedAt());
        assertEquals("2025-08-04 12:00:00", dto.getConfirmedAt());
        assertEquals("2025-08-05 15:00:00", dto.getSettledAt());
        assertEquals("ORDER", dto.getSource());
        assertEquals(1001L, dto.getSourceId());
        assertEquals("按订单金额5%计算", dto.getCalculationRule());
        assertEquals(2001L, dto.getSettlementId());
        assertEquals("2025-08-05 16:00:00", dto.getPaidAt());
    }

    @Test
    @DisplayName("测试RewardDto参数构造器")
    void testParameterizedConstructor() {
        RewardDto dto = new RewardDto(1L, 10L, "COMMISSION", new BigDecimal("500.00"), "CONFIRMED");
        
        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getAgentId());
        assertEquals("COMMISSION", dto.getType());
        assertEquals(new BigDecimal("500.00"), dto.getAmount());
        assertEquals("CONFIRMED", dto.getStatus());
    }

    @Test
    @DisplayName("测试业务方法 - isCommission()")
    void testIsCommission() {
        rewardDto.setType("COMMISSION");
        assertTrue(rewardDto.isCommission());
        
        rewardDto.setType("PROMOTION_REWARD");
        assertFalse(rewardDto.isCommission());
        
        rewardDto.setType("REFERRAL_BONUS");
        assertFalse(rewardDto.isCommission());
        
        rewardDto.setType(null);
        assertFalse(rewardDto.isCommission());
    }

    @Test
    @DisplayName("测试业务方法 - isPromotionReward()")
    void testIsPromotionReward() {
        rewardDto.setType("PROMOTION_REWARD");
        assertTrue(rewardDto.isPromotionReward());
        
        rewardDto.setType("COMMISSION");
        assertFalse(rewardDto.isPromotionReward());
        
        rewardDto.setType("REFERRAL_BONUS");
        assertFalse(rewardDto.isPromotionReward());
        
        rewardDto.setType(null);
        assertFalse(rewardDto.isPromotionReward());
    }

    @Test
    @DisplayName("测试业务方法 - isConfirmed()")
    void testIsConfirmed() {
        rewardDto.setStatus("CONFIRMED");
        assertTrue(rewardDto.isConfirmed());
        
        rewardDto.setStatus("SETTLED");
        assertTrue(rewardDto.isConfirmed());
        
        rewardDto.setStatus("PENDING");
        assertFalse(rewardDto.isConfirmed());
        
        rewardDto.setStatus("CANCELLED");
        assertFalse(rewardDto.isConfirmed());
        
        rewardDto.setStatus(null);
        assertFalse(rewardDto.isConfirmed());
    }

    @Test
    @DisplayName("测试业务方法 - isSettled()")
    void testIsSettled() {
        rewardDto.setStatus("SETTLED");
        assertTrue(rewardDto.isSettled());
        
        rewardDto.setStatus("CONFIRMED");
        assertFalse(rewardDto.isSettled());
        
        rewardDto.setStatus("PENDING");
        assertFalse(rewardDto.isSettled());
        
        rewardDto.setStatus("CANCELLED");
        assertFalse(rewardDto.isSettled());
        
        rewardDto.setStatus(null);
        assertFalse(rewardDto.isSettled());
    }

    @Test
    @DisplayName("测试业务方法 - isPending()")
    void testIsPending() {
        rewardDto.setStatus("PENDING");
        assertTrue(rewardDto.isPending());
        
        rewardDto.setStatus("CONFIRMED");
        assertFalse(rewardDto.isPending());
        
        rewardDto.setStatus("SETTLED");
        assertFalse(rewardDto.isPending());
        
        rewardDto.setStatus("CANCELLED");
        assertFalse(rewardDto.isPending());
        
        rewardDto.setStatus(null);
        assertFalse(rewardDto.isPending());
    }

    @Test
    @DisplayName("测试toString方法")
    void testToString() {
        rewardDto.setId(1L);
        rewardDto.setAgentId(10L);
        rewardDto.setAgentUsername("agent001");
        rewardDto.setType("COMMISSION");
        rewardDto.setAmount(new BigDecimal("500.00"));
        rewardDto.setStatus("CONFIRMED");
        rewardDto.setDescription("订单佣金奖励");
        rewardDto.setCreatedAt("2025-08-04 10:00:00");
        rewardDto.setSettledAt("2025-08-05 15:00:00");
        
        String toString = rewardDto.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("RewardDto{"));
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("agentId=10"));
        assertTrue(toString.contains("agentUsername='agent001'"));
    }

    @Test
    @DisplayName("测试JSR-303验证 - 有效数据")
    void testValidationValid() {
        rewardDto.setId(1L);
        rewardDto.setAgentId(10L);
        rewardDto.setType("COMMISSION");
        rewardDto.setAmount(new BigDecimal("500.00"));
        rewardDto.setStatus("CONFIRMED");
        
        Set<ConstraintViolation<RewardDto>> violations = validator.validate(rewardDto);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("测试JSR-303验证 - ID不能为空")
    void testValidationIdNotNull() {
        rewardDto.setId(null);
        rewardDto.setAgentId(10L);
        rewardDto.setType("COMMISSION");
        rewardDto.setAmount(new BigDecimal("500.00"));
        rewardDto.setStatus("CONFIRMED");
        
        Set<ConstraintViolation<RewardDto>> violations = validator.validate(rewardDto);
        assertFalse(violations.isEmpty());
        
        boolean hasIdViolation = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("id"));
        assertTrue(hasIdViolation);
    }

    @Test
    @DisplayName("测试JSR-303验证 - agentId不能为空")
    void testValidationAgentIdNotNull() {
        rewardDto.setId(1L);
        rewardDto.setAgentId(null);
        rewardDto.setType("COMMISSION");
        rewardDto.setAmount(new BigDecimal("500.00"));
        rewardDto.setStatus("CONFIRMED");
        
        Set<ConstraintViolation<RewardDto>> violations = validator.validate(rewardDto);
        assertFalse(violations.isEmpty());
        
        boolean hasAgentIdViolation = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("agentId"));
        assertTrue(hasAgentIdViolation);
    }

    @Test
    @DisplayName("测试边界条件")
    void testEdgeCases() {
        // 测试空字符串类型
        rewardDto.setType("");
        assertFalse(rewardDto.isCommission());
        assertFalse(rewardDto.isPromotionReward());
        
        // 测试空字符串状态
        rewardDto.setStatus("");
        assertFalse(rewardDto.isConfirmed());
        assertFalse(rewardDto.isSettled());
        assertFalse(rewardDto.isPending());
        
        // 测试大小写敏感性
        rewardDto.setType("commission");
        assertFalse(rewardDto.isCommission()); // 应该是大写
        
        rewardDto.setStatus("confirmed");
        assertFalse(rewardDto.isConfirmed()); // 应该是大写
        
        // 测试各种无效类型和状态
        rewardDto.setType("UNKNOWN_TYPE");
        assertFalse(rewardDto.isCommission());
        assertFalse(rewardDto.isPromotionReward());
        
        rewardDto.setStatus("UNKNOWN_STATUS");
        assertFalse(rewardDto.isConfirmed());
        assertFalse(rewardDto.isSettled());
        assertFalse(rewardDto.isPending());
    }

    @Test
    @DisplayName("测试所有奖励类型")
    void testAllRewardTypes() {
        String[] types = {"COMMISSION", "PROMOTION_REWARD", "REFERRAL_BONUS", "PERFORMANCE_BONUS"};
        
        for (String type : types) {
            rewardDto.setType(type);
            
            switch (type) {
                case "COMMISSION":
                    assertTrue(rewardDto.isCommission(), "Type " + type + " should be commission");
                    assertFalse(rewardDto.isPromotionReward(), "Type " + type + " should not be promotion reward");
                    break;
                case "PROMOTION_REWARD":
                    assertFalse(rewardDto.isCommission(), "Type " + type + " should not be commission");
                    assertTrue(rewardDto.isPromotionReward(), "Type " + type + " should be promotion reward");
                    break;
                default:
                    assertFalse(rewardDto.isCommission(), "Type " + type + " should not be commission");
                    assertFalse(rewardDto.isPromotionReward(), "Type " + type + " should not be promotion reward");
            }
        }
    }

    @Test
    @DisplayName("测试所有奖励状态")
    void testAllRewardStatuses() {
        String[] statuses = {"PENDING", "CONFIRMED", "SETTLED", "CANCELLED"};
        
        for (String status : statuses) {
            rewardDto.setStatus(status);
            
            switch (status) {
                case "PENDING":
                    assertTrue(rewardDto.isPending(), "Status " + status + " should be pending");
                    assertFalse(rewardDto.isConfirmed(), "Status " + status + " should not be confirmed");
                    assertFalse(rewardDto.isSettled(), "Status " + status + " should not be settled");
                    break;
                case "CONFIRMED":
                    assertFalse(rewardDto.isPending(), "Status " + status + " should not be pending");
                    assertTrue(rewardDto.isConfirmed(), "Status " + status + " should be confirmed");
                    assertFalse(rewardDto.isSettled(), "Status " + status + " should not be settled");
                    break;
                case "SETTLED":
                    assertFalse(rewardDto.isPending(), "Status " + status + " should not be pending");
                    assertTrue(rewardDto.isConfirmed(), "Status " + status + " should be confirmed (settled)");
                    assertTrue(rewardDto.isSettled(), "Status " + status + " should be settled");
                    break;
                case "CANCELLED":
                    assertFalse(rewardDto.isPending(), "Status " + status + " should not be pending");
                    assertFalse(rewardDto.isConfirmed(), "Status " + status + " should not be confirmed");
                    assertFalse(rewardDto.isSettled(), "Status " + status + " should not be settled");
                    break;
            }
        }
    }

    @Test
    @DisplayName("测试BigDecimal字段的边界值")
    void testBigDecimalBoundaryValues() {
        // 测试零值
        rewardDto.setAmount(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, rewardDto.getAmount());
        
        // 测试负值
        rewardDto.setAmount(new BigDecimal("-100.00"));
        assertEquals(new BigDecimal("-100.00"), rewardDto.getAmount());
        
        // 测试很大的值
        rewardDto.setAmount(new BigDecimal("999999999.99"));
        assertEquals(new BigDecimal("999999999.99"), rewardDto.getAmount());
        
        // 测试精度
        rewardDto.setRewardRate(new BigDecimal("0.123456"));
        assertEquals(new BigDecimal("0.123456"), rewardDto.getRewardRate());
    }
}