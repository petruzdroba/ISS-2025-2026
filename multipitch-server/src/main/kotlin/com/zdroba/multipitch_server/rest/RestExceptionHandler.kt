package com.zdroba.multipitch_server.rest

import com.zdroba.multipitch_server.dto.ErrorResponse
import com.zdroba.multipitch_server.exceptions.AlreadyExistsException
import com.zdroba.multipitch_server.exceptions.InvalidCredentialsException
import com.zdroba.multipitch_server.exceptions.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(exception: NotFoundException): ErrorResponse? {
        return ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            exception.message,
            System.currentTimeMillis()
        )
    }

    @ExceptionHandler(AlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleAlreadyExists(exception: AlreadyExistsException): ErrorResponse? {
        return ErrorResponse(
            HttpStatus.CONFLICT.value(),
            exception.message,
            System.currentTimeMillis()
        )
    }

    @ExceptionHandler(InvalidCredentialsException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleInvalidCredentials(exception: InvalidCredentialsException): ErrorResponse? {
        return ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Unauthorized",
            System.currentTimeMillis()
        )
    }
}