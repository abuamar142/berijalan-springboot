package com.abuamar.order_management_service.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.CreationTimestamp
import java.io.Serial
import java.io.Serializable
import java.sql.Timestamp

@MappedSuperclass
abstract class BaseEntity: Serializable {
    @CreationTimestamp
    @Column(nullable=false, updatable = false, name = "created_at")
    var createdAt: Timestamp? = null

    @Column(nullable=false, updatable = false, name = "created_by")
    var createdBy: String? = null

    @Column(name = "updated_at")
    var updatedAt: Timestamp? = null

    @Column(name = "updated_by")
    var updatedBy: String? = null

    @Column(name = "is_active")
    var isActive: Boolean = true

    @Column(name = "is_delete", nullable = false)
    var isDelete: Boolean = false

    @Column(name = "deleted_by", length = 36, nullable = true)
    var deletedBy: String? = null

    @Column(name = "deleted_at", nullable = true)
    var deletedAt: Timestamp? = null

    companion object {
        @Serial
        private const val serialVersionUID: Long = 7060258737997787729L
    }
}
