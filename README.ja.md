# @pocketsign/expo-downloads

Expo 用モジュールで、Android および iOS のネイティブ Downloads フォルダにファイルを直接ダウンロードするためのライブラリです。

## 目次

- [インストール](#インストール)
- [使用方法](#使用方法)
- [API 仕様](#api-仕様)
  - [関数](#関数)
  - [権限関連関数](#権限関連関数)
  - [例外](#例外)
- [プラットフォームごとの動作](#プラットフォームごとの動作)

## インストール

```bash
npx expo install @pocketsign/expo-downloads
```

## 使用方法

下記のサンプルコードは、`example.txt` という名前のテキストファイルを Base64 エンコードされたデータからネイティブの Downloads フォルダへ保存する例です。  
Android 9 以下の場合は、`WRITE_EXTERNAL_STORAGE` 権限の確認も行っています。

```javascript:README.md
import { saveToDownloads, getPermissionsAsync, requestPermissionsAsync } from "@pocketsign/expo-downloads";

const fileName = "example.txt";
const mimeType = "text/plain";
const base64Data = "SGVsbG8sIFdvcmxkIQ=="; // "Hello, World!" の Base64 エンコード

// Android 9 以下の場合、WRITE_EXTERNAL_STORAGE の権限を取得します。
const permissions = await getPermissionsAsync();
if (!permissions.granted) {
  const newPermissions = await requestPermissionsAsync();
  if (!newPermissions.granted) {
    console.error("権限が取得できませんでした");
    return;
  }
}

const result = await saveToDownloads(fileName, mimeType, base64Data);
if (result.cancelled) {
  console.log("ダウンロードがキャンセルされました");
} else {
  console.log(`ファイルが保存されました: ${result.uri}`);
}
```

## API 仕様

### 関数

#### `saveToDownloads(fileName: string, mimeType: string, base64Data: string): Promise<DownloadResponse>`

- **説明**:  
  Base64 形式の文字列データを、指定されたファイル名と MIME タイプでネイティブの Downloads フォルダに保存します。

- **引数**:
  - `fileName`: 保存するファイル名
  - `mimeType`: ファイルの MIME タイプ
  - `base64Data`: Base64 でエンコードされたファイルデータ

- **戻り値**:
  - `DownloadResponse` オブジェクト  
    - `uri`: 保存されたファイルの URI (成功時)
    - `cancelled`: ユーザーがキャンセルした場合に `true`

#### `openDownloadFile(uri: string, mimeType: string): Promise<void>`

- **説明**:  
  保存されたファイルの URIを、ネイティブのファイルビューアで開きます。

- **引数**:
  - `uri`: 開くファイルの URI（`saveToDownloads` の戻り値の `uri` を指定してください）
  - `mimeType`: ファイルの MIME タイプ

### 権限関連関数

- **`requestPermissionsAsync`**:  
  Android 9 以下の場合、`WRITE_EXTERNAL_STORAGE` の権限をリクエストします。（Android 10 以上、iOS では不要です）

- **`getPermissionsAsync`**:  
  現在のストレージ書き込み権限の状態を取得します。

### 例外

#### 共通例外

- **ERR_INVALID_ARGUMENT**  
  - `fileName` が空、`mimeType` のフォーマットが不正、または `base64Data` の形式に問題がある場合に発生します。

- **FileOpenException**  
  - ファイルを開く際に発生する例外です。指定されたファイルが存在しない場合、または対応するビューアが見つからない場合にスローされます。

#### iOS 固有の例外

- **ERR_DOWNLOAD_IN_PROGRESS**  
  - 既にダウンロード処理が進行中の場合に発生します。複数のダウンロード実行を防止します。

- **ERR_MISSING_VIEW_CONTROLLER**  
  - 現在の表示中の ViewController を取得できない場合に発生します。（ファイル保存用のダイアログを表示できない場合）

#### Android 固有の例外

- **ERR_CONTENT_URI_CREATION** (Android 10 以上)  
  - MediaStore API を使ってコンテンツ URI の作成に失敗した場合に発生します。

- **ERR_OUTPUT_STREAM_CREATION** (Android 10 以上)  
  - ファイル書き込み用の OutputStream の作成に失敗した場合に発生します。

- **ERR_DIRECTORY_CREATION** (Android 9 以下)  
  - Downloads フォルダが存在しない、または作成に失敗した場合に発生します。

- **ERR_OUT_OF_MEMORY**  
  - ファイルサイズが大きすぎる場合、メモリ不足が発生しファイル保存に失敗した場合に発生します。

## プラットフォームごとの動作

### iOS

- **実装**:  
  `UIDocumentPickerViewController` を使用し、ユーザーに保存先の選択を促します。

### Android

#### Android 10以上 (Q+)

- **実装**:  
  MediaStore API を使って、システム管理下の Downloads フォルダにファイルを保存します。
- **権限**:  
  API レベル 29 以上では、特別なストレージアクセス権限は不要です。

#### Android 9以下

- **実装**:  
  従来の方法でデバイスの Downloads フォルダに直接ファイルを書き込みます。
- **権限**:  
  `WRITE_EXTERNAL_STORAGE` の権限が必要です。`requestPermissionsAsync` と `getPermissionsAsync` による権限の管理を行ってください。

### Web

- **実装**:  
  HTML の `Blob` と `<a>` タグを利用して、ファイルをダウンロードします。
