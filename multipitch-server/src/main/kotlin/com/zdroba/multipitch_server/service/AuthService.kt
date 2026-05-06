package com.zdroba.multipitch_server.service

import com.zdroba.multipitch_server.dao.UserDAO
import com.zdroba.multipitch_server.dto.AuthResponse
import com.zdroba.multipitch_server.dto.UserDto
import com.zdroba.multipitch_server.entity.User
import com.zdroba.multipitch_server.exceptions.AlreadyExistsException
import com.zdroba.multipitch_server.exceptions.InvalidCredentialsException
import com.zdroba.multipitch_server.exceptions.NotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(private val repository: UserDAO, private val passwordEncoder: PasswordEncoder) : IAuthService {

    override fun register(
        email: String,
        username: String,
        password: String
    ): AuthResponse {
        repository.findByEmail(email)
            .ifPresent { throw AlreadyExistsException("User with email: $email already exists") }

        repository.findByUsername(username)
            .ifPresent { throw AlreadyExistsException("User with username: $username already exists") }

        val saved: User = repository.save(User(email, username, passwordEncoder.encode(password)))

        return AuthResponse(
            "access",
            "refresh",
            saved.toDto()
        )
    }

    override fun login(email: String, password: String): AuthResponse {
        val user = repository.findByEmail(email)
            .orElseThrow { NotFoundException("User with email: $email does not exist") }

        if (!passwordEncoder.matches(password, user.password)) {
            throw InvalidCredentialsException()
        }

        return AuthResponse(
            "access",
            "refresh",
            user.toDto()
        )
    }

    override fun me(id: Long): UserDto {
        val user = repository.findById(id)
            .orElseThrow { NotFoundException("User with id: $id not found") }

        return user.toDto()
    }

    override fun delete(id: Long) {
        val user = repository.findById(id)
            .orElseThrow { NotFoundException("User with id: $id not found") }

        repository.delete(user)
    }
}