package com.abuamar.hotel_management_service.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "mst_brand")
data class MasterBrandEntity(
    @Id
    @Column(name = "id", insertable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    @Column(name = "name")
    val name: String,
): BaseEntity()