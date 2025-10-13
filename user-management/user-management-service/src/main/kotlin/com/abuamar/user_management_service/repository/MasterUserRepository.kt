package com.abuamar.user_management_service.repository

import com.abuamar.user_management_service.domain.entity.MasterUserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface MasterUserRepository: JpaRepository<MasterUserEntity, Int> {
    @Query(
        """
        SELECT * FROM mst_user
        WHERE is_delete = false
        AND is_active = true
        """,
        nativeQuery = true
    )
    override fun findAll(): List<MasterUserEntity>

    @Query(
        """
        SELECT * FROM mst_user
        WHERE id = :id
        AND is_delete = false
        AND is_active = true
        """,
        nativeQuery = true
    )
    fun findUserActiveById(id: Int): Optional<MasterUserEntity>

    @Query(
        """
        SELECT * FROM mst_user
        WHERE username = :username
        AND is_delete = false
        AND is_active = true
        """,
        nativeQuery = true
    )
    fun findByUsername(username: String): MasterUserEntity?

    @Query(
        """
        SELECT * FROM mst_user
        WHERE id IN :userIds
        AND is_delete = false
        AND is_active = true
        """,
        nativeQuery = true
    )
    fun findUsersByIds(userIds: List<Int>): List<MasterUserEntity>
}