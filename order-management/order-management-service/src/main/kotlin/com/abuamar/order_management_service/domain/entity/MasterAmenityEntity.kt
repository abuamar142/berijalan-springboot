package com.abuamar.order_management_service.domain.entity

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "mst_amenity")
data class MasterAmenityEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    var id: Int = 0,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "description", nullable = true)
    val description: String? = null,

    @ManyToMany(mappedBy = "amenities")
    val rooms: Set<MasterRoomEntity> = HashSet(),

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Timestamp = Timestamp(System.currentTimeMillis()),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Timestamp = Timestamp(System.currentTimeMillis()),

    @Column(name = "deleted_at", nullable = true)
    val deletedAt: Timestamp?,
)