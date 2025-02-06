// Reexport the native module. On web, it will be resolved to DownloadsModule.web.ts
// and on native platforms to DownloadsModule.ts
export { default } from "./DownloadsModule";
export { default as DownloadsView } from "./DownloadsView";
export * from "./Downloads.types";
