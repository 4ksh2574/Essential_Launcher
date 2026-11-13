#!/usr/bin/env python3
"""Apply AX Home product identity to the vendored Android launcher source.

This script changes the application identity, release artifact name and
user-facing product strings. Internal upstream package namespaces and
required copyright/licence notices remain unchanged.
"""
from __future__ import annotations

import os
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
UPSTREAM = ROOT / "launcher-core"
PROPERTIES = ROOT / "launcher.properties"


def read_properties(path: Path) -> dict[str, str]:
    values: dict[str, str] = {}
    for raw_line in path.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        key, value = line.split("=", 1)
        values[key.strip()] = value.strip()
    return values


def replace_once(text: str, pattern: str, replacement: str, label: str) -> str:
    updated, count = re.subn(pattern, replacement, text, count=1, flags=re.MULTILINE)
    if count != 1:
        raise RuntimeError(
            f"Expected one {label} in launcher-core/build.gradle, found {count}"
        )
    return updated


def main() -> int:
    if not (UPSTREAM / "build.gradle").is_file():
        print(
            "Missing launcher-core/build.gradle. Ensure the launcher source is present.",
            file=sys.stderr,
        )
        return 2

    props = read_properties(PROPERTIES)
    app_name = props.get("name", "AX Home")
    application_id = props.get("applicationId", "com.axo.axhome")
    version = props.get("version", "1.0.0")

    # A vMAJOR.MINOR.PATCH Git tag controls the release build version.
    if os.environ.get("GITHUB_REF", "").startswith("refs/tags/v"):
        version = os.environ.get("GITHUB_REF_NAME", "")[1:]

    parts = version.split(".")
    if len(parts) != 3 or not all(part.isdigit() for part in parts):
        raise ValueError("version must use numeric MAJOR.MINOR.PATCH format, e.g. 1.0.0")
    major, minor, patch = map(int, parts)
    if any(part > 99 for part in (major, minor, patch)):
        raise ValueError("each version component must be between 0 and 99")
    version_code = major * 10_000 + minor * 100 + patch

    build_file = UPSTREAM / "build.gradle"
    build = build_file.read_text(encoding="utf-8")

    build = replace_once(
        build,
        r'^final def version = "[^"]+"$',
        f'final def version = "{version}"',
        "launcher version",
    )
    build = replace_once(
        build,
        r"^final def versionDisplayName = .*$",
        'final def versionDisplayName = "${version}"',
        "display version",
    )
    build = replace_once(
        build,
        r"^\s*versionCode\s+[^\n]+$",
        f"        versionCode {version_code}",
        "versionCode",
    )

    # The internal Gradle flavor and package namespaces remain untouched.
    build = replace_once(
        build,
        r"applicationId 'app\.lawnchair'$",
        f"applicationId '{application_id}'",
        "GitHub applicationId",
    )
    build = replace_once(
        build,
        r"applicationId 'app\.lawnchair\.nightly'",
        f"applicationId '{application_id}.nightly'",
        "nightly applicationId",
    )
    build = replace_once(
        build,
        r'applicationId "app\.lawnchair\.play"',
        f'applicationId "{application_id}.play"',
        "Play applicationId",
    )

    build = replace_once(
        build,
        r'resValue\("string", "derived_app_name", "Lawnchair \(Debug\)"\)',
        f'resValue("string", "derived_app_name", "{app_name}")',
        "debug app label",
    )
    build = replace_once(
        build,
        r'resValue\("string", "derived_app_name", "Lawnchair"\)',
        f'resValue("string", "derived_app_name", "{app_name}")',
        "release app label",
    )
    build = replace_once(
        build,
        r'output\.outputFileName\.set\("Lawnchair\.\$\{output\.versionName\.get\(\)\}\.\$channel\.\$\{variant\.buildType\}\.apk"\)',
        f'output.outputFileName.set("AXHome{version}.apk")',
        "APK filename",
    )

    build_file.write_text(build, encoding="utf-8")

    # Replace the brand only in actual string resources, not copyright headers
    # or comments. This also updates translated settings/onboarding copy.
    strings_dir = UPSTREAM / "lawnchair" / "res"
    replaced_files = 0
    for file_path in strings_dir.glob("values*/strings.xml"):
        original = file_path.read_text(encoding="utf-8")

        def replace_brand(match: re.Match[str]) -> str:
            value = re.sub(
                r"lawnchair",
                lambda _: app_name,
                match.group(2),
                flags=re.IGNORECASE,
            )
            return match.group(1) + value + match.group(3)

        updated = re.sub(
            r"(<string\s+[^>]*>)(.*?)(</string>)",
            replace_brand,
            original,
            flags=re.DOTALL,
        )
        if updated != original:
            file_path.write_text(updated, encoding="utf-8")
            replaced_files += 1

    print(f"Prepared {app_name} {version} ({application_id})")
    print(f"Updated launcher build config and {replaced_files} translated string files")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
