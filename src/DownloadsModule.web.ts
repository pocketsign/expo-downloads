import { registerWebModule, NativeModule } from 'expo';

import { DownloadsModuleEvents } from './Downloads.types';

class DownloadsModule extends NativeModule<DownloadsModuleEvents> {
  PI = Math.PI;
  async setValueAsync(value: string): Promise<void> {
    this.emit('onChange', { value });
  }
  hello() {
    return 'Hello world! 👋';
  }
}

export default registerWebModule(DownloadsModule);
