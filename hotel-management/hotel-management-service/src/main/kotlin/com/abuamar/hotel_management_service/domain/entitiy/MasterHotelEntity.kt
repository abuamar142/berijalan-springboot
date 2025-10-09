package com.abuamar.hotel_management_service.domain.entitiy

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(name = "mst_hotel")
data class MasterHotelEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    var id: Int = 0,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "address", nullable = true)
    val address: String? = null,

    @Column(name = "phone_number", nullable = true)
    val phoneNumber: String? = null,

    @Column(name = "email", nullable = true)
    val email: String? = null,

    @Column(name = "rating", nullable = true)
    val rating: Double? = null,

    @OneToMany(mappedBy = "hotel", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val rooms: List<MasterRoomEntity> = emptyList(),

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "hotel_facility",
        joinColumns = [JoinColumn(name = "hotel_id")],
        inverseJoinColumns = [JoinColumn(name = "facility_id")]
    )
    val facilities: Set<MasterFacilityEntity> = HashSet(),

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Timestamp = Timestamp(System.currentTimeMillis()),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Timestamp = Timestamp(System.currentTimeMillis()),

    @Column(name = "deleted_at", nullable = true)
    val deletedAt: Timestamp?,
)
