name: "Spring Boot + Spring Cloud Alibaba Microservices Distribution System"
description: |

## Purpose
Build a production-ready microservices-based distribution system with Spring Boot + Spring Cloud Alibaba, supporting multi-level agent distribution, commission tracking, and comprehensive business management features.

## Core Principles
1. **Context is King**: Include ALL necessary documentation, examples, and caveats
2. **Validation Loops**: Provide executable tests/lints the AI can run and fix
3. **Information Dense**: Use keywords and patterns from the codebase
4. **Progressive Success**: Start simple, validate, then enhance
5. **Global rules**: Be sure to follow all rules in CLAUDE.md

---

## Goal
Create a complete microservices architecture distribution system with:
- 9 microservices (gateway, auth, lead, deal, product, promotion, commission, audit, web)
- Spring Cloud Alibaba with Nacos for service discovery and configuration
- JWT + RBAC authentication with 5 roles
- Multi-level commission system (3 levels)
- Web admin panel + WeChat mini-program support
- Docker + Kubernetes deployment ready

## Why
- **Business value**: Enables efficient multi-level sales distribution management
- **Integration**: Complete solution for lead tracking, deal management, and commission settlement
- **Problems solved**: Manual commission calculation, duplicate leads, lack of audit trails

## What
A microservices-based system where:
- Agents submit customer leads through mobile/web interfaces
- System automatically deduplicates and assigns leads to sales
- Sales track deals and generate promotion links
- Commission automatically calculated across 3 levels
- Complete audit trail and reporting capabilities

### Success Criteria
- [ ] All 9 microservices running and registered with Nacos
- [ ] JWT authentication working across all services
- [ ] RBAC with 5 roles properly enforced
- [ ] Lead deduplication by phone number functional
- [ ] 3-level commission calculation accurate
- [ ] All Maven tests pass with >80% coverage
- [ ] API documentation accessible via Swagger

## All Needed Context

### Documentation & References
```yaml
# MUST READ - Include these in your context window
- url: https://spring.io/projects/spring-boot#learn
  why: Spring Boot 3.x documentation for latest features and patterns
  
- url: https://spring.io/projects/spring-cloud
  why: Spring Cloud architecture patterns, circuit breakers, load balancing
  
- url: https://github.com/alibaba/spring-cloud-alibaba/wiki
  why: Nacos configuration, service discovery patterns, examples
  
- url: https://nacos.io/en-us/docs/quick-start-spring-cloud.html
  why: Nacos Spring Cloud integration quick start guide
  
- url: https://mybatis.org/mybatis-3/getting-started.html
  why: MyBatis configuration and mapper patterns
  
- url: https://github.com/jwtk/jjwt#quickstart
  why: JWT token generation and validation patterns

- file: CLAUDE.md
  why: Project development rules and conventions
  
- file: INITIAL.md
  why: Complete feature requirements and business logic
```

### Current Codebase tree
```bash
.
├── CLAUDE.md
├── INITIAL.md
├── LICENSE
├── PRPs/
│   ├── EXAMPLE_multi_agent_prp.md
│   └── templates/
│       └── prp_base.md
├── README.md
└── examples/
```

### Desired Codebase tree with files to be added
```bash
distribution-system/
├── README.md                        # Project documentation
├── pom.xml                         # Parent POM with dependency management
├── docker-compose.yml              # Local development environment
├── .env.example                    # Environment variables template
├── common/                         # Common utilities module
│   ├── pom.xml
│   └── src/main/java/com/distribution/common/
│       ├── config/
│       │   ├── RedisConfig.java
│       │   └── MyBatisConfig.java
│       ├── dto/
│       │   └── CommonResult.java
│       ├── exception/
│       │   ├── BusinessException.java
│       │   └── GlobalExceptionHandler.java
│       ├── utils/
│       │   ├── JwtUtils.java
│       │   └── PasswordEncoder.java
│       └── enums/
│           ├── UserRole.java
│           └── ErrorCode.java
├── gateway/                        # API Gateway service
│   ├── pom.xml
│   └── src/main/java/com/distribution/gateway/
│       ├── GatewayApplication.java
│       ├── config/
│       │   └── GatewayConfig.java
│       └── filter/
│           └── AuthenticationFilter.java
├── auth-service/                   # Authentication service
│   ├── pom.xml
│   └── src/main/java/com/distribution/auth/
│       ├── AuthServiceApplication.java
│       ├── controller/
│       │   └── AuthController.java
│       ├── service/
│       │   └── AuthService.java
│       └── entity/
│           └── User.java
├── lead-service/                   # Lead management service
│   ├── pom.xml
│   └── src/main/java/com/distribution/lead/
│       ├── LeadServiceApplication.java
│       ├── controller/
│       ├── service/
│       └── mapper/
├── deal-service/                   # Deal tracking service
│   ├── pom.xml
│   └── src/main/java/com/distribution/deal/
├── product-service/                # Product management service
│   ├── pom.xml
│   └── src/main/java/com/distribution/product/
├── promotion-service/              # Promotion task service
│   ├── pom.xml
│   └── src/main/java/com/distribution/promotion/
├── commission-service/             # Commission calculation service
│   ├── pom.xml
│   └── src/main/java/com/distribution/commission/
├── audit-service/                  # Audit logging service
│   ├── pom.xml
│   └── src/main/java/com/distribution/audit/
├── admin-web/                      # Admin web interface
│   ├── pom.xml
│   └── src/main/
├── agent-miniapp/                  # WeChat mini-program
│   └── README.md
├── scripts/                        # Deployment scripts
│   ├── docker/
│   │   └── Dockerfile.template
│   └── k8s/
│       ├── namespace.yaml
│       └── deployment-template.yaml
└── sql/                           # Database schemas
    ├── init.sql
    └── tables/
        ├── user.sql
        ├── lead.sql
        ├── deal.sql
        └── commission.sql
```

### Known Gotchas & Library Quirks
```java
// CRITICAL: Spring Boot 3.x requires Java 17+
// CRITICAL: Nacos server must be running before starting microservices
// CRITICAL: MyBatis mappers must be in same package or explicitly scanned
// CRITICAL: JWT secret must be at least 256 bits for HS256
// CRITICAL: Feign clients need @EnableFeignClients on main class
// CRITICAL: Redis connection pool must be configured to avoid connection leaks
// CRITICAL: Transaction boundaries don't work across microservice calls
// CRITICAL: Use @RefreshScope for dynamic configuration updates from Nacos
```

## Implementation Blueprint

### Data models and structure

```java
// User entity with RBAC roles
@Data
@TableName("sys_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @NotNull(message = "角色不能为空")
    private UserRole role; // SUPER_ADMIN, DIRECTOR, LEADER, SALES, AGENT
    
    private Long parentId; // 上级用户ID，用于计算多级佣金
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

// Lead (客资) entity
@Data
@TableName("biz_lead")
public class Lead {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @NotBlank(message = "客户手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$")
    private String customerPhone; // 唯一标识，防重复
    
    private String customerName;
    private Long agentId; // 提交代理ID
    private Long salesId; // 分配销售ID
    
    @NotNull
    private LeadStatus status; // NEW, FOLLOWING, DEAL, LOST
    
    private LocalDateTime createTime;
    private LocalDateTime assignTime;
}

// Deal (成交) entity
@Data
@TableName("biz_deal")
public class Deal {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long leadId;
    private Long productId;
    private BigDecimal amount;
    
    private String promotionLink; // 自动生成的推广链接
    
    private LocalDateTime dealTime;
}

// Commission (佣金) entity
@Data
@TableName("biz_commission")
public class Commission {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long dealId;
    private Long userId;
    private Integer level; // 1=直接(10%), 2=二级(5%), 3=三级(2%)
    private BigDecimal rate;
    private BigDecimal amount;
    
    private CommissionStatus status; // PENDING, SETTLED, CANCELLED
    
    private LocalDateTime createTime;
    private LocalDateTime settleTime;
}
```

### List of tasks to be completed

```yaml
Task 1: Initialize Parent Project and Common Module
CREATE distribution-system/pom.xml:
  - Define Spring Boot 3.x parent
  - Add Spring Cloud and Spring Cloud Alibaba BOMs
  - Configure common dependencies and plugins
  
CREATE common module:
  - CommonResult for unified API responses
  - GlobalExceptionHandler with @RestControllerAdvice
  - JwtUtils using io.jsonwebtoken:jjwt
  - UserRole enum with permission definitions
  
Task 2: Setup Nacos Configuration
CREATE docker-compose.yml:
  - Nacos server container (port 8848)
  - MySQL 8.0 container
  - Redis 6.2 container
  
CREATE bootstrap.yml templates:
  - spring.cloud.nacos.discovery.server-addr=127.0.0.1:8848
  - spring.cloud.nacos.config.server-addr=127.0.0.1:8848
  - Pattern for each microservice

Task 3: Implement API Gateway
CREATE gateway service:
  - Spring Cloud Gateway configuration
  - AuthenticationFilter for JWT validation
  - Route definitions for all microservices
  - CORS configuration
  - Rate limiting with Redis

Task 4: Implement Auth Service
CREATE auth-service:
  - User registration with phone validation
  - Login endpoint returning JWT token
  - Password encoding with BCrypt
  - Token refresh mechanism
  - Role-based permissions

Task 5: Implement Lead Service
CREATE lead-service:
  - Lead submission with phone deduplication
  - Automatic assignment to sales
  - Lead status tracking
  - Query APIs with pagination
  - Integration with audit service

Task 6: Implement Deal Service
CREATE deal-service:
  - Deal recording from leads
  - Promotion link generation
  - Commission trigger to commission-service
  - Deal statistics APIs
  - Product association

Task 7: Implement Commission Service
CREATE commission-service:
  - 3-level commission calculation
  - Batch settlement by month
  - Commission query APIs
  - Report generation
  - Transactional consistency

Task 8: Implement Supporting Services
CREATE product-service:
  - Basic CRUD for products
  - Category management
  
CREATE promotion-service:
  - Promotion task submission
  - Content review workflow
  
CREATE audit-service:
  - Operation logging
  - Audit trail queries

Task 9: Add Comprehensive Tests
CREATE test suites:
  - Unit tests for each service
  - Integration tests with TestContainers
  - API tests with RestAssured
  - Load tests for commission calculation

Task 10: Create Deployment Configuration
CREATE Docker and K8s configs:
  - Dockerfile for each service
  - K8s deployments with health checks
  - ConfigMaps for environment variables
  - Service definitions
  - Ingress configuration
```

### Per task pseudocode

```java
// Task 4: JWT Authentication
@Service
public class AuthService {
    @Autowired
    private UserMapper userMapper;
    
    public LoginResponse login(LoginRequest request) {
        // PATTERN: Validate input first
        validatePhone(request.getPhone());
        
        // GOTCHA: MyBatis returns null if not found
        User user = userMapper.selectByPhone(request.getPhone());
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        
        // PATTERN: Use BCrypt for password comparison
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
        
        // CRITICAL: Include role in JWT claims for gateway filtering
        String token = JwtUtils.generateToken(user.getId(), user.getPhone(), user.getRole());
        
        return LoginResponse.builder()
            .token(token)
            .role(user.getRole())
            .expiresIn(JwtUtils.EXPIRATION_TIME)
            .build();
    }
}

// Task 5: Lead Deduplication
@Service
@Transactional
public class LeadService {
    @Autowired
    private LeadMapper leadMapper;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    public Lead submitLead(LeadSubmitRequest request) {
        // PATTERN: Use Redis distributed lock for deduplication
        String lockKey = "lead:phone:" + request.getCustomerPhone();
        Boolean locked = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, "1", 5, TimeUnit.SECONDS);
        
        if (!locked) {
            throw new BusinessException(ErrorCode.OPERATION_TOO_FREQUENT);
        }
        
        try {
            // GOTCHA: Check existing lead by phone
            Lead existing = leadMapper.selectByCustomerPhone(request.getCustomerPhone());
            if (existing != null) {
                throw new BusinessException(ErrorCode.LEAD_ALREADY_EXISTS);
            }
            
            // PATTERN: Auto-assign to available sales
            Long salesId = assignmentService.getNextAvailableSales();
            
            Lead lead = new Lead();
            lead.setCustomerPhone(request.getCustomerPhone());
            lead.setAgentId(getCurrentUserId());
            lead.setSalesId(salesId);
            lead.setStatus(LeadStatus.NEW);
            
            leadMapper.insert(lead);
            
            // PATTERN: Async notification to sales
            eventPublisher.publishEvent(new LeadAssignedEvent(lead));
            
            return lead;
        } finally {
            redisTemplate.delete(lockKey);
        }
    }
}

// Task 7: 3-Level Commission Calculation
@Service
@Transactional
public class CommissionService {
    private static final BigDecimal LEVEL_1_RATE = new BigDecimal("0.10"); // 10%
    private static final BigDecimal LEVEL_2_RATE = new BigDecimal("0.05"); // 5%
    private static final BigDecimal LEVEL_3_RATE = new BigDecimal("0.02"); // 2%
    
    public void calculateCommissions(Deal deal) {
        // PATTERN: Get agent who submitted the lead
        Lead lead = leadService.getById(deal.getLeadId());
        User agent = userService.getById(lead.getAgentId());
        
        // Level 1: Direct commission to agent
        createCommission(deal, agent.getId(), 1, LEVEL_1_RATE);
        
        // GOTCHA: Check parent exists before calculating
        if (agent.getParentId() != null) {
            // Level 2: Commission to agent's parent
            createCommission(deal, agent.getParentId(), 2, LEVEL_2_RATE);
            
            User parent = userService.getById(agent.getParentId());
            if (parent.getParentId() != null) {
                // Level 3: Commission to grandparent
                createCommission(deal, parent.getParentId(), 3, LEVEL_3_RATE);
            }
        }
    }
    
    private void createCommission(Deal deal, Long userId, Integer level, BigDecimal rate) {
        Commission commission = new Commission();
        commission.setDealId(deal.getId());
        commission.setUserId(userId);
        commission.setLevel(level);
        commission.setRate(rate);
        commission.setAmount(deal.getAmount().multiply(rate));
        commission.setStatus(CommissionStatus.PENDING);
        
        commissionMapper.insert(commission);
    }
}
```

### Integration Points
```yaml
DATABASE:
  - Create schema: distribution_system
  - Run sql/init.sql for all tables
  - Create indexes for phone lookups
  
NACOS CONFIG:
  - Create namespace: distribution-system
  - Import configs for each service:
    - auth-service-dev.yaml
    - lead-service-dev.yaml
    - etc.
  
ENVIRONMENT VARIABLES (.env):
  # Nacos
  NACOS_SERVER_ADDR=127.0.0.1:8848
  NACOS_NAMESPACE=distribution-system
  
  # MySQL
  MYSQL_HOST=localhost
  MYSQL_PORT=3306
  MYSQL_DATABASE=distribution_system
  MYSQL_USERNAME=root
  MYSQL_PASSWORD=root123
  
  # Redis
  REDIS_HOST=localhost
  REDIS_PORT=6379
  REDIS_PASSWORD=
  
  # JWT
  JWT_SECRET=your-256-bit-secret-key-for-jwt-signing
  JWT_EXPIRATION=86400000
  
  # Services Ports
  GATEWAY_PORT=8080
  AUTH_SERVICE_PORT=8081
  LEAD_SERVICE_PORT=8082
  DEAL_SERVICE_PORT=8083
  
MAVEN PROFILES:
  - dev: Local development with embedded servers
  - test: Integration testing with TestContainers
  - prod: Production with external dependencies
```

## Validation Loop

### Level 1: Syntax & Build
```bash
# Run from root directory
mvn clean compile

# Expected: BUILD SUCCESS for all modules
# If errors: Check Java 17+, fix compilation errors
```

### Level 2: Unit Tests
```java
// Test auth service JWT generation
@SpringBootTest
class AuthServiceTest {
    @Test
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest("13800138000", "password");
        LoginResponse response = authService.login(request);
        
        assertNotNull(response.getToken());
        assertEquals(UserRole.AGENT, response.getRole());
        assertTrue(JwtUtils.validateToken(response.getToken()));
    }
    
    @Test
    void testLoginInvalidCredentials() {
        LoginRequest request = new LoginRequest("13800138000", "wrong");
        
        assertThrows(BusinessException.class, () -> authService.login(request));
    }
}

// Test lead deduplication
@SpringBootTest
class LeadServiceTest {
    @Test
    void testLeadDeduplication() {
        LeadSubmitRequest request = new LeadSubmitRequest();
        request.setCustomerPhone("13900139000");
        
        // First submission succeeds
        Lead lead1 = leadService.submitLead(request);
        assertNotNull(lead1.getId());
        
        // Duplicate submission fails
        assertThrows(BusinessException.class, () -> leadService.submitLead(request));
    }
}

// Test commission calculation
@SpringBootTest
class CommissionServiceTest {
    @Test
    void testThreeLevelCommission() {
        Deal deal = createTestDeal(new BigDecimal("10000"));
        
        commissionService.calculateCommissions(deal);
        
        List<Commission> commissions = commissionMapper.selectByDealId(deal.getId());
        assertEquals(3, commissions.size());
        
        // Verify amounts
        assertEquals(new BigDecimal("1000.00"), commissions.get(0).getAmount()); // 10%
        assertEquals(new BigDecimal("500.00"), commissions.get(1).getAmount());  // 5%
        assertEquals(new BigDecimal("200.00"), commissions.get(2).getAmount());  // 2%
    }
}
```

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report

# Expected: All tests pass, >80% coverage
# If failing: Debug specific test, fix logic, re-run
```

### Level 3: Integration Tests
```bash
# Start all services
docker-compose up -d
mvn spring-boot:run -pl gateway
mvn spring-boot:run -pl auth-service
mvn spring-boot:run -pl lead-service

# Test auth flow
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"phone": "13800138000", "password": "password"}'

# Expected: {"code": 200, "data": {"token": "eyJ...", "role": "AGENT"}}

# Test lead submission (with token from above)
curl -X POST http://localhost:8080/lead/submit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJ..." \
  -d '{"customerPhone": "13900139000", "customerName": "张三"}'

# Expected: {"code": 200, "data": {"id": 1, "status": "NEW"}}

# Check Nacos service registry
curl http://localhost:8848/nacos/v1/ns/service/list

# Expected: All services registered
```

### Level 4: Performance Test
```bash
# Load test commission calculation
mvn gatling:test -Dgatling.simulationClass=CommissionLoadTest

# Expected: 
# - 95th percentile < 500ms
# - No deadlocks or connection pool exhaustion
# - Memory usage stable
```

## Final Validation Checklist
- [ ] All modules compile: `mvn clean compile`
- [ ] All tests pass: `mvn test`
- [ ] Test coverage >80%: `mvn jacoco:report`
- [ ] All services register with Nacos
- [ ] JWT authentication works across services
- [ ] Lead deduplication prevents duplicates
- [ ] Commission calculation is accurate
- [ ] API documentation accessible at http://localhost:8080/swagger-ui.html
- [ ] Docker images build successfully
- [ ] K8s manifests are valid: `kubectl apply --dry-run=client -f scripts/k8s/`

---

## Anti-Patterns to Avoid
- ❌ Don't share databases between microservices
- ❌ Don't call services directly, use Feign clients
- ❌ Don't skip Redis configuration for distributed locks
- ❌ Don't hardcode service URLs, use Nacos discovery
- ❌ Don't use GET for state-changing operations
- ❌ Don't skip transaction boundaries for financial operations
- ❌ Don't store passwords in plain text
- ❌ Don't expose internal error details to clients
- ❌ Don't forget to handle Feign client failures

## Confidence Score: 9.5/10

High confidence due to:
- Clear microservices patterns from Spring Cloud documentation
- Well-defined business logic in INITIAL.md
- Comprehensive validation gates at multiple levels
- Detailed implementation pseudocode for complex parts
- Complete project structure with all necessary files

Minor uncertainty only on specific WeChat mini-program integration details, but backend APIs will support it fully.