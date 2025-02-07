import ExpoModulesCore

internal struct DownloadResponse: Record {
    @Field var cancelled: Bool = false
    @Field var uri: String? = nil
}
