package jp.co.pocketsign.expo.downloads

import expo.modules.kotlin.records.Field
import expo.modules.kotlin.records.Record
import expo.modules.kotlin.types.Enumerable

data class SaveFileOptions(
    @Field val name: String,
    @Field val type: String,
    @Field val data: String,
    @Field val encoding: Encoding?
) : Record

data class OpenFileOptions(
    @Field val uri: String,
    @Field val type: String
) : Record

enum class Encoding(val value: String) : Enumerable {
    utf8("utf8"),
    base64("base64")
}
