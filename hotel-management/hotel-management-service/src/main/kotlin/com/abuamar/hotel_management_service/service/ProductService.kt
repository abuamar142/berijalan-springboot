package com.abuamar.hotel_management_service.service

import com.abuamar.hotel_management_service.domain.dto.req.ReqProduct
import com.abuamar.hotel_management_service.domain.dto.res.ResProduct

interface ProductService {
    fun getProducts(): List<ResProduct>
    fun getProductById(id: Int): ResProduct?
    fun updateProduct(req: ReqProduct): ResProduct
}