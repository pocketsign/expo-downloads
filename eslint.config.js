const { defineConfig, globalIgnores } = require("eslint/config");
const universeNodeConfig = require("eslint-config-universe/flat/node");
const universeNativeConfig = require("eslint-config-universe/flat/native");
const eslintPluginPrettierRecommended = require("eslint-plugin-prettier/recommended");

module.exports = defineConfig([
  universeNodeConfig,
  universeNativeConfig,
  eslintPluginPrettierRecommended,
  globalIgnores(["**/build"]),
]);
