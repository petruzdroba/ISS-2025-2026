package com.zdroba.multipitch_server.service

import com.zdroba.multipitch_server.dto.SyncResponse

interface IDataService {

    fun upload(userId:Long, blob: ByteArray)

    fun download(userId:Long): SyncResponse

    fun download(userId:Long, id:Long): SyncResponse

    fun downloadAll(userId:Long):List<SyncResponse>
}