import ExpoModulesCore

struct SaveFileOptions: Record {
    @Field var name: String
    @Field var type: String
    @Field var data: String
    @Field var encoding: Encoding?
}

struct OpenFileOptions: Record {
    @Field var uri: String
    @Field var type: String
}

enum Encoding: String, Enumerable {
    case base64
    case utf8
}
