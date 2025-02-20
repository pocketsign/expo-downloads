import React, { useState } from "react";
import { encode } from "base-64";
import {
  saveToDownloads,
  getPermissionsAsync,
  requestPermissionsAsync,
  openDownloadFile,
} from "@pocketsign/expo-downloads";
import { Button, SafeAreaView, ScrollView, Text, View, StyleSheet } from "react-native";

export default function App() {
  const [result, setResult] = useState<{
    uri: string;
    mimeType: string;
  } | null>(null);
  const [error, setError] = useState<string | null>(null);

  const saveFile = async (fileName: string, mimeType: string, base64Data: string) => {
    setError(null);
    setResult(null);
    try {
      const permissions = await getPermissionsAsync();
      if (!permissions.granted) {
        const newPermissions = await requestPermissionsAsync();
        if (!newPermissions.granted) {
          setError("Permission not granted");
          return;
        }
      }

      const result = await saveToDownloads(fileName, mimeType, base64Data);
      if (result.cancelled) {
        setError("Download cancelled");
      } else {
        setResult({ uri: result.uri, mimeType });
      }
    } catch (err: any) {
      setError(err.message || "An error occurred");
    }
  };

  const saveTextFile = async () => {
    const fileName = "hello.txt";
    const mimeType = "text/plain";
    const base64Data = "SGVsbG8sIHdvcmxkIQ=="; // "Hello, world!"
    await saveFile(fileName, mimeType, base64Data);
  };

  const saveImageFile = async () => {
    const fileName = "icon.png";
    const mimeType = "image/png";
    const base64Data =
      "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";
    await saveFile(fileName, mimeType, base64Data);
  };

  const saveLargeFile = async () => {
    const fileName = "large.txt";
    const mimeType = "text/plain";
    const baseText = "This is a large file content. ";
    const targetBytes = 104857600; // 100MB
    const repeats = Math.floor(targetBytes / baseText.length);
    const largeText = baseText.repeat(repeats);
    const base64Data = encode(largeText);
    await saveFile(fileName, mimeType, base64Data);
  };

  const openFile = async () => {
    if (!result) return;
    try {
      await openDownloadFile(result.uri, result.mimeType);
    } catch (err: any) {
      setError(err.message || "An error occurred");
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={styles.content}>
        <Text style={styles.header}>Downloads Module Sample</Text>
        <View style={styles.buttons}>
          <Button title="Save Text File" onPress={saveTextFile} />
          <Button title="Save Image File" onPress={saveImageFile} />
          <Button title="Save Large File" onPress={saveLargeFile} />
        </View>
        {result && (
          <View style={styles.resultContainer}>
            <Text style={styles.resultTitle}>Saved File:</Text>
            <Text style={styles.resultText}>{result.uri}</Text>
            <Text style={styles.resultText}>{result.mimeType}</Text>
            <Button title="Open File" onPress={openFile} />
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
  buttons: {
    gap: 10,
  },
});
