package com.zdroba.multipitch_server.rest

import com.zdroba.multipitch_server.dto.AuthResponse
import com.zdroba.multipitch_server.dto.LoginRequest
import com.zdroba.multipitch_server.dto.RegisterRequest
import com.zdroba.multipitch_server.service.IAuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthRestController(
    private val service: IAuthService
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): AuthResponse {
        return service.register(
            request.email,
            request.username,
            request.password,
        )
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): AuthResponse {
        return service.login(
            request.email,
            request.password
        )
    }
}