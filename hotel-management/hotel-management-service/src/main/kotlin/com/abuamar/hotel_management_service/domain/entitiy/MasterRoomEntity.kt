package com.abuamar.hotel_management_service.domain.entitiy

import com.abuamar.hotel_management_service.domain.enum.RoomStatus
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "mst_room")
data class MasterRoomEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    var id: Int = 0,

    @Column(name = "room_number", nullable = false)
    val roomNumber: String,

    @Column(name = "type", nullable = false)
    val type: String,

    @Column(name = "price", nullable = false)
    val price: Double,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: RoomStatus = RoomStatus.AVAILABLE,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    val hotel: MasterHotelEntity,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "room_amenity",
        joinColumns = [JoinColumn(name = "room_id")],
        inverseJoinColumns = [JoinColumn(name = "amenity_id")]
    )
    val amenities: Set<MasterAmenityEntity> = HashSet(),

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Timestamp = Timestamp(System.currentTimeMillis()),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Timestamp = Timestamp(System.currentTimeMillis()),

    @Column(name = "deleted_at", nullable = true)
    val deletedAt: Timestamp?,
)