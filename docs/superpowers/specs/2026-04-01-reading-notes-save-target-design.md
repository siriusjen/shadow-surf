# Reading Notes Save Target Design

**Goal:** 让阅读摘录功能更容易被发现，并让首次保存时直接选择文件位置，避免用户先去设置里配置路径。

## Interaction

- 摘录条保留 `Save` 和 `Cancel`
- 摘录条新增 `Save As...`
- 用户第一次点 `Save` 时：
  - 弹出保存位置选择
  - 允许指定目录和文件名
- 如果目标文件不存在：
  - 直接新建并写入
- 如果目标文件已存在：
  - 弹出确认
  - 默认推荐 `Append`
  - 备选 `Overwrite`
- 一旦用户选定了目标文件：
  - 将该文件路径回写到现有阅读摘录设置
  - 后续点 `Save` 直接保存到该文件
- 点 `Save As...` 时：
  - 始终重新选择目标文件
  - 选择后覆盖当前记住的目标文件

## Scope

### Includes
- 摘录条增加 `Save As...` 按钮
- 首次 `Save` 触发文件选择
- 已存在文件时支持追加 / 覆盖
- 记住最近一次选定的摘录文件

### Excludes
- 最近文件列表
- 多文件管理
- 保存历史
- 自动分类

## Technical Notes

- 继续复用 `ReadingNotesSettings`
- 通过设置中新增“是否已选择过目标文件”的状态，区分首次保存和后续保存
- `ReadingNoteWriter` 增加写入模式：`Append` / `Overwrite`
- `ShadowSurfPanel` 负责：
  - 调起文件选择
  - 决定首次保存 / 另存为流程
  - 在必要时弹出追加 / 覆盖确认
- 优先保持现有 MVP 结构，不引入新的复杂管理层
