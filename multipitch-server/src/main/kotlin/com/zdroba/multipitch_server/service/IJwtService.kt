package com.zdroba.multipitch_server.service

import com.zdroba.multipitch_server.dto.UserDto
import javax.crypto.SecretKey

interface IJwtService {

    fun getSecretKey(): SecretKey
    fun generateAccessToken(user: UserDto): String

    fun generateRefreshToken(user: UserDto): String

    fun getIdFromToken(token: String): Long

    fun getUsernameFromToken(token: String): String

    fun getEmailFromToken(token: String): String
}