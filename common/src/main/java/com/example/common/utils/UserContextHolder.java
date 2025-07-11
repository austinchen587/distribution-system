package com.example.common.utils;

public class UserContextHolder {
    
    private static final ThreadLocal<UserContext> CONTEXT_HOLDER = new ThreadLocal<>();
    
    public static void setContext(UserContext userContext) {
        CONTEXT_HOLDER.set(userContext);
    }
    
    public static UserContext getContext() {
        return CONTEXT_HOLDER.get();
    }
    
    public static void clear() {
        CONTEXT_HOLDER.remove();
    }
    
    public static String getCurrentUserId() {
        UserContext context = getContext();
        return context != null ? context.getUserId() : null;
    }
    
    public static String getCurrentUserRole() {
        UserContext context = getContext();
        return context != null ? context.getRole() : null;
    }
    
    public static class UserContext {
        private String userId;
        private String role;
        
        public UserContext(String userId, String role) {
            this.userId = userId;
            this.role = role;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public String getRole() {
            return role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
    }
}