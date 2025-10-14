package com.abuamar.hotel_management_service.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "mst_product")
data class MasterProductEntity(
    @Id
    @Column(name = "id", insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column(name = "name")
    var name: String,

    @Column(name = "price")
    var price: Long,

    @Column(name = "user_id")
    var userId: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    val brand: MasterBrandEntity?
): BaseEntity()