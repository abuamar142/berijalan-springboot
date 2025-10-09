package com.abuamar.hotel_management_service.controller

import com.abuamar.hotel_management_service.domain.dto.req.ReqProduct
import com.abuamar.hotel_management_service.domain.dto.res.BaseResponse
import com.abuamar.hotel_management_service.domain.dto.res.ResProduct
import com.abuamar.hotel_management_service.service.ProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService
) {
    @GetMapping
    fun getProducts(): ResponseEntity<BaseResponse<List<ResProduct>>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success get all products",
                data = productService.getProducts(),
            )
        )
    }

    @GetMapping("/{id}")
    fun getProductById(
        @PathVariable id: Int
    ): ResponseEntity<BaseResponse<ResProduct>> {
        return ResponseEntity.ok(
            BaseResponse(
                success = true,
                message = "Success get product by id $id",
                data = productService.getProductById(id),
            )
        )
    }

    @PutMapping("/{id}")
    fun updateProductById(
        @PathVariable id: Int,
        @RequestBody req: ReqProduct
    ): ResponseEntity<BaseResponse<ResProduct>> {
        req.id = id

        val response: ResProduct = productService.updateProduct(req)
        return ResponseEntity.ok(
            BaseResponse(
                message = "Success update product with id $id",
                data = response
            )
        )
    }
}