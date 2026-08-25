# FRM-Proxy 1.1.0 — Patch Notes

**Release:** https://github.com/Le1tnyFRM/FRM-Proxy/releases/tag/1.1.0

### Added
- 3 new themes: **Midnight, Nord, Crimson** (total 6: Dark/Light/Ocean/Midnight/Nord/Crimson)
- Tooltip delay slider in UI Settings
- 6b6t/anarchy hardening

### Fixed
- **Ocean → Light/Dark switch lock** — now clears UIManager keys and updates all windows via FlatLaf.updateUI(), any→any switching works
- **6b6t `Badly compressed packet - size 4 below threshold 256`** — threshold <128 auto-corrected to 256 in CompressionPacketHandler (login + play)
- Workflows `ubuntu-26.04` → `ubuntu-latest` (build failures fixed)
- Modern UI arcs 12px + tab separators

### Changed
- Rebranded fully to **FRM-Proxy** (title 1.1.0, gradle `com.le1tny:FRM-Proxy:1.1.0`)
- README rewritten — custom FRM voice, tutorial https://youtu.be/oKGCrDZkbCQ, 6b6t tips, license clarified (GPL-3.0)
- Update checker now hits `Le1tnyFRM/FRM-Proxy` and opens `.../releases/new`

### Note
Stay GPL-3.0 (upstream requirement). Credit ViaProxy authors.

**Build:** `gradlew.bat build` → `build/libs/FRM-Proxy-1.1.0.jar`
