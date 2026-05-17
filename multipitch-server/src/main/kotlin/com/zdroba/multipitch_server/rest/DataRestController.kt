package com.zdroba.multipitch_server.rest

import com.zdroba.multipitch_server.dto.SyncRequest
import com.zdroba.multipitch_server.dto.SyncResponse
import com.zdroba.multipitch_server.service.IDataService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sync")
class DataRestController(
    private val service: IDataService
) {

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    fun sync(@Valid @RequestBody request: SyncRequest, authentication: Authentication){
        service.upload(authentication.name.toLong(), request.data)
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    fun latest(authentication: Authentication): SyncResponse {
        val userId = authentication.name.toLong()
        return service.download(userId)
    }
}