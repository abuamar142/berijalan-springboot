package com.abuamar.user_management_service.domain.constant

object AppConstants {
    // System Constants
    const val SYSTEM_USER = "SYSTEM"
    
    // HTTP Headers
    const val HEADER_USER_ID = "X-USER-ID"
    const val HEADER_USER_AUTHORITY = "X-USER-AUTHORITY"
    
    // Roles
    const val ROLE_ADMIN = "admin"
    
    // Error Messages
    const val ERR_UNAUTHORIZED = "You are not authorized to access this resource"
    const val ERR_INVALID_CREDENTIALS = "Username or password is incorrect"
    const val ERR_USERNAME_EXISTS = "Username already registered"
    const val ERR_USER_NOT_FOUND = "User not found"
    const val ERR_USER_DELETED = "User is deleted"
    const val ERR_USER_ALREADY_DELETED = "User is already deleted"
    const val ERR_USER_INACTIVE = "User is inactive"
    const val ERR_ALREADY_ACTIVE = "User is already active"
    const val ERR_ADMIN_NOT_FOUND = "Admin not found"
    const val ERR_ROLE_NOT_FOUND = "Role not found"
    const val ERR_AUTH_HEADER_MISSING = "Authentication header missing"
}