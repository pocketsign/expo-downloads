import ExpoModulesCore

struct SaveFileOptions: Record {
    @Field var fileName: String
    @Field var mimeType: String
    @Field var base64Data: String
}

struct OpenFileOptions: Record {
    @Field var uri: String
    @Field var mimeType: String
}
