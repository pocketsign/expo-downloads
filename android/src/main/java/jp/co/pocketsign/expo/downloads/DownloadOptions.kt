package jp.co.pocketsign.expo.downloads

import expo.modules.kotlin.records.Field
import expo.modules.kotlin.records.Record

data class SaveFileOptions(
    @Field val fileName: String,
    @Field val mimeType: String,
    @Field val base64Data: String
) : Record

data class OpenFileOptions(
    @Field val uri: String,
    @Field val mimeType: String
) : Record
