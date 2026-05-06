package com.zdroba.multipitch_server.dao

import com.zdroba.multipitch_server.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserDAO: JpaRepository<User, Long> {
}