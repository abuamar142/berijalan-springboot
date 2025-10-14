package com.abuamar.hotel_management_service.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "mst_facility")
data class MasterFacilityEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    var id: Int = 0,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "description", nullable = true)
    var description: String? = null,

    @ManyToMany(mappedBy = "facilities")
    val hotels: Set<MasterHotelEntity> = HashSet()
) : BaseEntity()