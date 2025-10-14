package com.abuamar.hotel_management_service.domain.entity

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "trn_room_amenity")
data class RoomAmenityEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    var id: Int = 0,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable = false)
    var room: MasterRoomEntity,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "amenity_id", nullable = false)
    var amenity: MasterAmenityEntity,

    @Column(name = "created_at", nullable = false)
    var createdAt: Timestamp = Timestamp(System.currentTimeMillis()),

    @Column(name = "created_by", length = 100)
    var createdBy: String? = null
)
