#!/usr/bin/env bash

set -Eeuo pipefail

echo "[INFO] Configuring commitlint"

command -v npm >/dev/null 2>&1 || {
	echo "[ERROR] npm is required but was not found." >&2
	exit 1
}

npm install --no-save \
			@commitlint/cli \
			@commitlint/config-conventional || {
    echo "[ERROR] Failed to install commitlint dependencies." >&2
    exit 1
  }

githooks_path="$(dirname "$(realpath $0)")"
git config core.hooksPath "$githooks_path"

echo "[INFO] Configuration finished"
