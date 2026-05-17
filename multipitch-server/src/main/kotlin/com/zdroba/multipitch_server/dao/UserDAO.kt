package com.zdroba.multipitch_server.dao

import com.zdroba.multipitch_server.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserDAO: JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>

    fun findByUsername(username: String): Optional<User>
}