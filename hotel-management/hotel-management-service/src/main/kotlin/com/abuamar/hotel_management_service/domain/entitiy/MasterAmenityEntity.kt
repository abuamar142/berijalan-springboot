package com.abuamar.hotel_management_service.domain.entitiy

import jakarta.persistence.*

@Entity
@Table(name = "mst_amenity")
data class MasterAmenityEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    var id: Int = 0,

    @Column(name = "name", nullable = false, unique = true, length = 100)
    var name: String,

    @Column(name = "description", length = 500)
    var description: String? = null,

    @Column(name = "icon", length = 50)
    var icon: String? = null,

    @OneToMany(mappedBy = "amenity", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    val roomAmenities: MutableSet<RoomAmenityEntity> = HashSet()
) : BaseEntity() {
    val rooms: Set<MasterRoomEntity>
        get() = roomAmenities.map { it.room }.toSet()
}