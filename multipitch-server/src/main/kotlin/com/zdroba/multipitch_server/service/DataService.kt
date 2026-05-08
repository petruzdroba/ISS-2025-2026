package com.zdroba.multipitch_server.service

import com.zdroba.multipitch_server.dao.DataDAO
import com.zdroba.multipitch_server.dao.UserDAO
import com.zdroba.multipitch_server.dto.SyncResponse
import com.zdroba.multipitch_server.entity.Data
import com.zdroba.multipitch_server.exceptions.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DataService(
    private val repository: DataDAO,
    private val userRepo: UserDAO
) : IDataService {

    @Transactional
    override fun upload(userId: Long, blob: ByteArray) {
        val user = userRepo.findById(userId)
            .orElseThrow { NotFoundException("User with id: $userId not found") }

        repository.save(Data(user, blob))
        repository.deleteOldestExceptLast3(userId)
    }

    @Transactional(readOnly = true)
    override fun download(userId: Long): SyncResponse {
        val sync = repository.findTop1ByUserIdOrderByCreatedAtDesc(userId)
            .orElseThrow { NotFoundException("No synced data for user $userId") }

        return SyncResponse(
            data = sync.data,
            createdAt = sync.createdAt
        )
    }

    override fun download(userId: Long, id: Long): SyncResponse {
        val sync = repository.findByIdAndUserId(id, userId)
            .orElseThrow { NotFoundException("Data $id not found for user $userId") }

        return SyncResponse(
            data = sync.data,
            createdAt = sync.createdAt
        )
    }

    override fun downloadAll(userId: Long): List<SyncResponse> {
        return repository.findByUserIdOrderByCreatedAtDesc(userId)
            .map {
                SyncResponse(
                    data = it.data,
                    createdAt = it.createdAt
                )
            }
    }
}