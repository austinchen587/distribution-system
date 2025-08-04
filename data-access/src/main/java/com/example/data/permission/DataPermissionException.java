package com.example.data.permission;

/**
 * 数据访问权限异常
 * 
 * @author Data Access Generator
 * @version 1.0
 * @since 2025-08-03
 */
public class DataPermissionException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private final String serviceName;
    private final String table;
    private final OperationType operation;
    
    public DataPermissionException(String message) {
        super(message);
        this.serviceName = null;
        this.table = null;
        this.operation = null;
    }
    
    public DataPermissionException(String message, Throwable cause) {
        super(message, cause);
        this.serviceName = null;
        this.table = null;
        this.operation = null;
    }
    
    public DataPermissionException(String serviceName, String table, OperationType operation) {
        super(String.format("Service %s does not have %s permission for table %s", 
                           serviceName, operation, table));
        this.serviceName = serviceName;
        this.table = table;
        this.operation = operation;
    }
    
    public DataPermissionException(String serviceName, String table, OperationType operation, String message) {
        super(message);
        this.serviceName = serviceName;
        this.table = table;
        this.operation = operation;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public String getTable() {
        return table;
    }
    
    public OperationType getOperation() {
        return operation;
    }
}