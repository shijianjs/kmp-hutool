---
sidebar_position: 4
title: Licensing
---

# Licensing

Two licences apply, and they cover different things.

## This project: MIT

The Kotlin code in `kmp-hutool` is released under the MIT License. See `LICENSE` in the repository
root for the full text. You may use it commercially, modify it and redistribute it, provided the
copyright notice and permission notice are kept.

## Upstream Hutool: Mulan PSL v2

`kmp-hutool` is a derivative work: class names, algorithms, constants and documentation wording come
from [Hutool](https://github.com/chinabugotech/hutool), which is licensed under the **Mulan Permissive
Software License v2** (木兰宽松许可证第 2 版), not MIT.

`NOTICE` records the upstream project, version (`5.8.48`), copyright holder and licence. The
obligations that matter for users of this library:

* keep the upstream copyright notice and the Mulan PSL v2 text with any redistribution of the
  derived code;
* the Mulan PSL v2 grants patent peace and does not require copyleft-style disclosure of your own
  source, so a commercial product may depend on this library;
* do not relicense the derived portions as MIT-only: the MIT licence here covers the original Kotlin
  work, while the derived parts remain under Mulan PSL v2.

If you need a single-licence answer for legal review, treat the combined work as
**MIT AND Mulan PSL v2** and keep both notices.

## What is *not* derived

Configuration, Gradle build logic, the documentation site, the demo and the tooling under `scripts/`
are original to this repository and covered by MIT alone.

## Third-party code inside Hutool

Hutool itself embeds or derives from a few permissively licensed projects (for example
`mozilla/rhino` in `hutool-script` and Apache-licensed helpers). This port does not include those
modules, so their notices are not reproduced here. If a future port adds one, its notice belongs in
`NOTICE` before release.
