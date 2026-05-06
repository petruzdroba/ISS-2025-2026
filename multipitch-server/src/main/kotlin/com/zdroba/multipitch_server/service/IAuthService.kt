package com.zdroba.multipitch_server.service

import com.zdroba.multipitch_server.dto.AuthResponse
import com.zdroba.multipitch_server.dto.UserDto

interface IAuthService {

    fun register(email: String, username: String,password: String): AuthResponse

    fun login(email: String, password: String): AuthResponse

    fun me(id: Long): UserDto

    fun delete(id: Long)
}