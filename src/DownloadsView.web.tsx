import * as React from "react";

import { DownloadsViewProps } from "./Downloads.types";

export default function DownloadsView(props: DownloadsViewProps) {
  return (
    <div>
      <iframe
        style={{ flex: 1 }}
        src={props.url}
        onLoad={() => props.onLoad({ nativeEvent: { url: props.url } })}
      />
    </div>
  );
}
