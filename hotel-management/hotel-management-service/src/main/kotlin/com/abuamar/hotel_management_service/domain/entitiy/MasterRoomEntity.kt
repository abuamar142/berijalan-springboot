package com.abuamar.hotel_management_service.domain.entitiy

import com.abuamar.hotel_management_service.domain.enum.RoomStatus
import jakarta.persistence.*

@Entity
@Table(name = "mst_room")
data class MasterRoomEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    var id: Int = 0,

    @Column(name = "room_number", nullable = false)
    var roomNumber: String,

    @Column(name = "type", nullable = false)
    var type: String,

    @Column(name = "price", nullable = false)
    var price: Double,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: RoomStatus = RoomStatus.AVAILABLE,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    var hotel: MasterHotelEntity,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "room_amenity",
        joinColumns = [JoinColumn(name = "room_id")],
        inverseJoinColumns = [JoinColumn(name = "amenity_id")]
    )
    var amenities: MutableSet<MasterAmenityEntity> = HashSet()
) : BaseEntity()