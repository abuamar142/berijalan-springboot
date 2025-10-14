package com.abuamar.hotel_management_service.domain.constant

object AppConstants {
    // System Constants
    const val SYSTEM_USER = "SYSTEM"
    
    // HTTP Headers
    const val HEADER_USER_ID = "X-USER-ID"
    const val HEADER_USER_AUTHORITY = "X-USER-AUTHORITY"
    
    // Roles
    const val ROLE_ADMIN = "admin"
    const val ROLE_USER = "user"

    // Error Messages - General
    const val ERR_UNAUTHORIZED = "You are not authorized to access this resource"
    const val ERR_AUTH_HEADER_MISSING = "Authentication header missing"
    const val ERR_INVALID_DATA = "Invalid data provided"
    
    // Error Messages - Amenity
    const val ERR_AMENITY_NOT_FOUND = "Amenity not found"
    const val ERR_AMENITY_ALREADY_ACTIVE = "Amenity is already active"
    const val ERR_NO_AMENITIES_FOUND = "No amenities found"
    const val ERR_AMENITY_ALREADY_EXISTS = "Amenity with this name already exists"
    
    // Error Messages - Facility
    const val ERR_FACILITY_NOT_FOUND = "Facility not found"
    const val ERR_FACILITY_ALREADY_ACTIVE = "Facility is already active"
    const val ERR_NO_FACILITIES_FOUND = "No facilities found"
    const val ERR_FACILITY_ALREADY_EXISTS = "Facility with this name already exists"
    
    // Error Messages - Hotel
    const val ERR_HOTEL_NOT_FOUND = "Hotel not found"
    const val ERR_HOTEL_ALREADY_ACTIVE = "Hotel is already active"
    const val ERR_NO_HOTELS_FOUND = "No hotels found"
    const val ERR_HOTEL_ALREADY_EXISTS = "Hotel with this name already exists"
    
    // Error Messages - Room
    const val ERR_ROOM_NOT_FOUND = "Room not found"
    const val ERR_NO_ROOMS_FOUND = "No rooms found"
    
    // Error Messages - Product
    const val ERR_PRODUCT_NOT_FOUND = "Product not found"
    const val ERR_NO_PRODUCTS_FOUND = "No products found"
    
    // Error Messages - Brand
    const val ERR_BRAND_NOT_FOUND = "Brand not found"
    const val ERR_NO_BRANDS_FOUND = "No brands found"
}