#!/usr/bin/env bash
set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "Usage: $0 <new-version>  (e.g. $0 2.1.0)" >&2
  exit 1
fi

NEW_VERSION="$1"
if ! [[ "$NEW_VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "Version must be in x.y.z format, got: $NEW_VERSION" >&2
  exit 1
fi

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
POM_FILE="$ROOT_DIR/indexcards/pom.xml"
DOCKERFILE="$ROOT_DIR/indexcards/Dockerfile"
UI_DIR="$ROOT_DIR/indexcards-ui"

# Only the project's own <version> is at 4-space indent; the parent
# spring-boot-starter-parent's <version> is nested one level deeper (8 spaces).
sed -i -E "s|^(    <version>)[^<]+(</version>)|\1${NEW_VERSION}\2|" "$POM_FILE"

sed -i -E "s|target/indexcards-[0-9]+\.[0-9]+\.[0-9]+\.jar|target/indexcards-${NEW_VERSION}.jar|" "$DOCKERFILE"

(cd "$UI_DIR" && npm version "$NEW_VERSION" --no-git-tag-version --allow-same-version >/dev/null)

echo "Bumped version to $NEW_VERSION:"
grep -n "^    <version>" "$POM_FILE"
grep -n "target/indexcards-" "$DOCKERFILE"
grep -n '"version"' "$UI_DIR/package.json" | head -1
