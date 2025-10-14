package com.abuamar.hotel_management_service.domain.entitiy

import jakarta.persistence.*

@Entity
@Table(name = "mst_hotel")
data class MasterHotelEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    var id: Int = 0,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "address", nullable = true)
    var address: String? = null,

    @Column(name = "phone_number", nullable = true)
    var phoneNumber: String? = null,

    @Column(name = "email", nullable = true)
    var email: String? = null,

    @Column(name = "rating", nullable = true)
    var rating: Double? = null,

    @OneToMany(mappedBy = "hotel", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val rooms: List<MasterRoomEntity> = emptyList(),

    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "hotel_facility",
        joinColumns = [JoinColumn(name = "hotel_id")],
        inverseJoinColumns = [JoinColumn(name = "facility_id")]
    )
    var facilities: MutableSet<MasterFacilityEntity> = HashSet()
) : BaseEntity()
