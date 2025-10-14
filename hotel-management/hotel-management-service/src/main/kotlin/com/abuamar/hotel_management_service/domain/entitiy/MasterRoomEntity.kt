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

    @Column(name = "room_number", nullable = false, length = 20)
    var roomNumber: String,

    @Column(name = "type", nullable = false, length = 50)
    var type: String,

    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    var price: Double,

    @Column(name = "capacity", nullable = false)
    var capacity: Int,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: RoomStatus = RoomStatus.AVAILABLE,

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "trn_room_amenity",
        joinColumns = [JoinColumn(name = "room_id")],
        inverseJoinColumns = [JoinColumn(name = "amenity_id")]
    )
    var amenities: MutableSet<MasterAmenityEntity> = HashSet()
) : BaseEntity()