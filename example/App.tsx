import React, { useState } from "react";
import Downloads from "@pocketsign/expo-downloads";
import {
  Button,
  SafeAreaView,
  ScrollView,
  Text,
  View,
  StyleSheet,
} from "react-native";

export default function App() {
  const [result, setResult] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const saveFile = async () => {
    setError(null);
    setResult(null);
    // 静的なパラメーターでファイル保存
    const fileName = "hello.txt";
    const mimeType = "text/plain";
    const base64Data = "SGVsbG8sIHdvcmxkIQ=="; // "Hello, world!" の Base64

    try {
      const result = await Downloads.saveToDownloads(
        fileName,
        mimeType,
        base64Data
      );
      if (result.cancelled) {
        setError("Download cancelled");
      } else {
        setResult(result.uri);
      }
    } catch (err: any) {
      setError(err.message || "An error occurred");
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.content}>
        <Text style={styles.header}>Downloads Module Sample</Text>
        <Button title="Save File" onPress={saveFile} />
        {result && (
          <View style={styles.resultContainer}>
            <Text style={styles.resultTitle}>Saved File URL:</Text>
            <Text style={styles.resultText}>{result}</Text>
          </View>
        )}
        {error && (
          <View style={styles.errorContainer}>
            <Text style={styles.errorText}>{error}</Text>
          </View>
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
  },
  content: {
    padding: 20,
    alignItems: "center",
    justifyContent: "center",
  },
  header: {
    fontSize: 24,
    marginBottom: 20,
  },
  resultContainer: {
    marginTop: 20,
    padding: 10,
    backgroundColor: "#e0ffe0",
    borderRadius: 5,
  },
  resultTitle: {
    fontSize: 18,
    fontWeight: "bold",
  },
  resultText: {
    fontSize: 16,
    marginTop: 5,
  },
  errorContainer: {
    marginTop: 20,
    padding: 10,
    backgroundColor: "#ffe0e0",
    borderRadius: 5,
  },
  errorText: {
    fontSize: 16,
    color: "red",
  },
});
