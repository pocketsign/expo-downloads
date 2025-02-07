package jp.co.pocketsign.expo.downloads

import expo.modules.kotlin.records.Field
import expo.modules.kotlin.records.Record

data class DownloadResponse(
    @Field
    val cancelled: Boolean = false,

    @Field
    val uri: String? = null
) : Record
