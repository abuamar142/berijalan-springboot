package com.abuamar.hotel_management_service.domain.entitiy

import jakarta.persistence.*

@Entity
@Table(name = "mst_amenity")
data class MasterAmenityEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    var id: Int = 0,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "description", nullable = true)
    var description: String? = null,

    @ManyToMany(mappedBy = "amenities")
    val rooms: Set<MasterRoomEntity> = HashSet()
) : BaseEntity()