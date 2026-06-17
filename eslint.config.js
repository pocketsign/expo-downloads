const { defineConfig, globalIgnores } = require("eslint/config");
const universeNodeConfig = require("eslint-config-universe/flat/node");
const universeNativeConfig = require("eslint-config-universe/flat/native");

module.exports = defineConfig([universeNodeConfig, universeNativeConfig, globalIgnores(["**/build"])]);
