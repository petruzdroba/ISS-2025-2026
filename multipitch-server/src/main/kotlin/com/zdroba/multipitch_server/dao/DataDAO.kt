package com.zdroba.multipitch_server.dao

import com.zdroba.multipitch_server.entity.Data
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

interface DataDAO: JpaRepository<Data, Long> {

    @Transactional
    @Modifying
    @Query(
        value = """
        DELETE FROM data
        WHERE id NOT IN (
            SELECT id FROM data
            WHERE user_id = :userId
            ORDER BY created_at DESC
            LIMIT 3
        )
        AND user_id = :userId
    """,
        nativeQuery = true
    )
    fun deleteOldestExceptLast3(@Param("userId") userId: Long)

    fun findTop1ByUserIdOrderByCreatedAtDesc(userId: Long): Optional<Data>

    fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<Data>
    fun findByIdAndUserId(id: Long, userId: Long): Optional<Data>
}