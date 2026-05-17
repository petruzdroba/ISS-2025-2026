package com.zdroba.multipitch_server.service

import com.zdroba.multipitch_server.dto.UserDto
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${jwt.secret:SUPER_SECRET_KEY}")
    private val secret: String,
    @Value("\${jwt.expiration:86400000}")
    private val expiration: Long,
    @Value("\${jwt.refresh-expiration:604800000}")
    private val refreshExpiration: Long,
) : IJwtService {

    override fun getSecretKey(): SecretKey {
        return Keys.hmacShaKeyFor(secret.toByteArray())
    }

    override fun generateAccessToken(user: UserDto): String {
        return Jwts.builder()
            .subject(user.email)
            .claim("id", user.id)
            .claim("username", user.username)
            .claim("email", user.email)
            .claim("token", "access")
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + this.expiration))
            .signWith(getSecretKey())
            .compact()
    }

    override fun generateRefreshToken(user: UserDto): String {
        return Jwts.builder()
            .subject(user.email)
            .claim("id", user.id)
            .claim("username", user.username)
            .claim("email", user.email)
            .claim("token", "refresh")
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + this.refreshExpiration))
            .signWith(getSecretKey())
            .compact()
    }

    private fun parseClaims(token: String) = Jwts.parser()
        .verifyWith(getSecretKey())
        .build()
        .parseSignedClaims(token)
        .payload

    override fun getIdFromToken(token: String): Long = parseClaims(token).get("id", Int::class.java).toLong()

    override fun getUsernameFromToken(token: String): String = parseClaims(token).get("username", String::class.java)

    override fun getEmailFromToken(token: String): String = parseClaims(token).get("email", String::class.java)

    override fun getTokenType(token: String): String = parseClaims(token).get("token", String::class.java)
}