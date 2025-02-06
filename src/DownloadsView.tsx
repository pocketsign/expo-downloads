import { requireNativeView } from "expo";
import * as React from "react";

import { DownloadsViewProps } from "./Downloads.types";

const NativeView: React.ComponentType<DownloadsViewProps> =
  requireNativeView("Downloads");

export default function DownloadsView(props: DownloadsViewProps) {
  return <NativeView {...props} />;
}
