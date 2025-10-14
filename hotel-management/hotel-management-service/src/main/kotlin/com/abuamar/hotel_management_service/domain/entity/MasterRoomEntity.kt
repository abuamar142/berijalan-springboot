package com.abuamar.hotel_management_service.domain.entity

import com.abuamar.hotel_management_service.domain.enum.RoomStatus
import jakarta.persistence.*

@Entity
@Table(name = "mst_room")
data class MasterRoomEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    var id: Int = 0,

    @Column(name = "room_number", nullable = false, length = 20)
    var roomNumber: String,

    @Column(name = "type", nullable = false, length = 50)
    var type: String,

    @Column(name = "price", nullable = false)
    var price: Int,

    @Column(name = "capacity", nullable = false)
    var capacity: Int,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: RoomStatus = RoomStatus.AVAILABLE,

    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    var roomAmenities: MutableSet<RoomAmenityEntity> = HashSet()
) : BaseEntity() {
    val amenities: Set<MasterAmenityEntity>
        get() = roomAmenities.map { it.amenity }.toSet()
}