package com.zdroba.multipitch_server.dao

import com.zdroba.multipitch_server.entity.Data
import org.springframework.data.jpa.repository.JpaRepository

interface DataDAO: JpaRepository<Data, Long> {
}