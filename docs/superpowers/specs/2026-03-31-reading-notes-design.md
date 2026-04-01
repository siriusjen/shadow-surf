# Reading Notes Design

**Goal:** 为 ShadowSurf 增加一个低存在感、低打扰的阅读摘录能力，让用户在 IDEA 右下角轻量阅读时，能快速把选中文本与页面信息保存到本地 Markdown 文件中。

## User Context
- 使用场景是 IDEA 右下角低存在感阅读
- 重点不是完整笔记系统，而是边读边快速摘录
- 产品优先级是：隐蔽性 > 速度 > 稳定性 > 后续整理能力

## Product Positioning
- 这是一个“阅读摘录”功能，不是知识库产品
- 核心目标是减少阅读过程中的打断
- 记录先够用，整理放到用户自己的 Markdown / 笔记体系中完成

## Core Interaction
1. 用户在网页中选中一段文本
2. 用户按快捷键触发“添加笔记”
3. 插件弹出一个很小的笔记条，而不是大窗口
4. 笔记条自动带入：
   - 选中文本
   - 当前页面标题
   - 当前页面链接
   - 当前时间
5. 用户只补充：
   - 简短备注
   - 可选标签
6. 用户按回车保存
7. 笔记条立即关闭，回到阅读状态

## UI Shape
建议采用底部窄条或贴近当前浏览区域的小输入条，风格尽量接近 IDEA 轻量输入组件。

### MVP Fields
- 只读摘录预览（显示选中文本，可截断）
- 备注输入框
- 标签输入框（可为空）
- 保存按钮
- 取消按钮

### UX Rules
- 不自动弹出
- 不打开大面板
- 不进入新页面
- 保存完成后立即收起
- 第一版不做复杂动画

## Storage Model
### Save Target
笔记保存到一个本地 Markdown 文件。

### Settings
提供一个非常简单的设置页，仅包含：
- `Notes Directory`
- `Notes File Name`
- `Resolved Path`（只读预览）

### Default Values
如果用户未修改设置，建议默认值为：
- `Notes Directory`: `~/Documents/ShadowSurf`
- `Notes File Name`: `reading-notes.md`

最终写入路径为两者拼接后的结果。

## Note Format
每条笔记采用统一 Markdown 结构，便于阅读、搜索和后续迁移。

```md
## 2026-03-31 14:20

- Title: 中国社会各阶级的分析
- URL: https://example.com/article
- Tags: 阶级分析, 历史

> 选中的原文内容……

Note:
这里写用户自己的简短备注。
```

## Scope
### MVP Includes
- 网页选中文本读取
- 快捷键触发添加笔记
- 小型笔记条交互
- 自动采集页面标题 / URL / 时间
- 本地 Markdown 追加写入
- 备注输入
- 可选标签
- 简单设置：目录 + 文件名

### MVP Excludes
- 多文件策略
- 自动分类 / 自动归档
- 笔记管理面板
- 搜索 / 过滤 / 二次编辑
- 云同步
- 与 Obsidian / Notion 的直接集成
- 富文本笔记

## Risks
- 不同网页对选中内容的获取方式可能不一致
- 内嵌浏览器中 JS 注入获取选区时，需要考虑兼容性
- 文件写入需要确保目录不存在时可以自动创建
- 设置项必须尽量简洁，避免把阅读插件做成笔记系统

## Success Criteria
- 用户能在不离开阅读状态的前提下快速记下一条摘录
- 保存动作足够轻，不明显破坏“低存在感”阅读体验
- 生成的 Markdown 文件可以直接被用户后续整理和迁移
