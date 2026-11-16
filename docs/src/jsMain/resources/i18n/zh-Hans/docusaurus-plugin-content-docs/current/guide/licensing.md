---
sidebar_position: 4
title: 许可证
---

# 许可证

这里同时涉及两个许可证，覆盖的对象不同。

## 本项目：MIT

`kmp-hutool` 里的 Kotlin 代码以 MIT 许可证发布，全文见仓库根目录 `LICENSE`。可商用、可修改、可再分发，前提是保留版权声明与许可声明。

## 上游 Hutool：Mulan PSL v2

`kmp-hutool` 是衍生作品：类名、算法、常量与文档表述来自 [Hutool](https://github.com/chinabugotech/hutool)，而上游采用 **木兰宽松许可证第 2 版**（Mulan Permissive Software License v2），不是 MIT。

`NOTICE` 记录了上游项目、版本（`5.8.48`）、版权人与许可证。使用方需要注意的义务：

* 再分发衍生代码时保留上游版权声明与 Mulan PSL v2 全文；
* Mulan PSL v2 提供专利互不侵犯条款，且没有传染性开源要求，商业产品可以依赖本库；
* 不要把衍生部分单独按 MIT 重新授权：本仓库的 MIT 覆盖原创的 Kotlin 改写，衍生部分仍是 Mulan PSL v2。

法务复核需要单一结论时，把整体视为 **MIT AND Mulan PSL v2**，两份声明都保留。

## 哪些不是衍生

构建脚本、文档站、演示页与 `scripts/` 下的工具是本仓库原创，只受 MIT 约束。

## Hutool 内部的三方代码

Hutool 自身引用了若干宽松许可的项目（例如 `hutool-script` 内的 `mozilla/rhino` 与 Apache 许可的辅助实现）。本库没有移植这些模块，因此不在此重复其声明；将来若移植，相关声明必须先写进 `NOTICE` 再发布。
