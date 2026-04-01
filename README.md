# ShadowSurf

在 IntelliJ IDEA 里轻量打开网页，减少白底网页与暗色 IDE 之间的割裂感。

`ShadowSurf` 是一个基于 `JBCefBrowser` 的轻量级 IDEA 内嵌浏览器插件。它不是完整浏览器，而是一个更适合日常轻浏览、临时查资料和低打扰网页访问的 Tool Window：尽量少切应用，尽量少被刺眼白底打断。

## 为什么用它

很多 IDEA 用户在暗色主题下工作，但打开网页时常会遇到两个问题：

- 需要频繁在 IDE 和浏览器之间切换
- 白底网页和暗色 IDE 之间反差很大，视觉上容易跳脱

`ShadowSurf` 的目标不是替代浏览器，而是在 IDEA 里补上一个足够轻、足够顺手、足够克制的内嵌浏览入口。

## 亮点

- 直接在 IDEA Tool Window 中打开网页
- 支持地址栏、打开页面、前进 / 后退 / 刷新
- 支持少量标签页，适合轻量浏览
- 插件 UI 跟随 IDEA 主题
- 提供网页“暗色增强”，尽量压暗常见白底区域
- 支持阅读时选中内容后快速摘录到本地 Markdown
- `JCEF` 不可用时有明确降级提示，不会直接失效

## 适合谁

适合：

- 长时间使用 IntelliJ IDEA / JetBrains IDE 的开发者
- 想在 IDE 里快速看文档、查链接、临时打开页面的人
- 使用暗色主题，希望减少白底网页突兀感的人

不适合：

- 想把它当成完整浏览器的人
- 需要下载管理、扩展系统、DevTools、同步能力的人
- 追求复杂网页完美暗黑适配的人

## 当前状态

当前仓库已完成 MVP，并已完成以下验证：

- `./gradlew test --stacktrace` 通过
- `./gradlew buildPlugin --stacktrace` 通过
- `./gradlew runIde` 可启动插件沙箱
- 已验证 `ShadowSurf` Tool Window 可打开
- 已验证打开页面、前进 / 后退 / 刷新可用
- 已验证新建 / 关闭 / 切换标签页可用
- 已验证暗色增强开关可触发页面样式变化
- 已补充阅读摘录的设置、快捷键与本地 Markdown 写入能力

打包产物位置：

- `build/distributions/shadow-surf-0.1.0.zip`

## 快速开始

### 本地试用

```bash
./gradlew runIde --stacktrace
```

启动后，在 IDEA 中打开：

- `View` → `Tool Windows` → `ShadowSurf`

### 构建插件包

```bash
./gradlew buildPlugin --stacktrace
```

构建成功后，插件 zip 位于：

```text
build/distributions/shadow-surf-0.1.0.zip
```

### 从磁盘安装

1. 打开 `Settings` / `Preferences`
2. 进入 `Plugins`
3. 点击右上角齿轮
4. 选择 `Install Plugin from Disk...`
5. 选择 `build/distributions/shadow-surf-0.1.0.zip`
6. 安装后重启 IDE

## 它能做什么

当前 MVP 已实现：

- Tool Window 形式集成到 IDEA
- 默认以右侧分割小窗方式停靠，更适合右下角轻量阅读
- 地址栏输入并打开网页
- 前进 / 后退 / 刷新
- 最多 5 个轻量标签页
- 跟随 IDEA 主题的插件 UI
- 网页“暗色增强”开关
- 选中文本后用快捷键打开紧凑摘录条
- 将摘录、标题、链接、备注、标签追加到本地 Markdown
- 通过设置页指定笔记目录和文件名
- `JCEF` 不可用时的降级提示

明确不做：

- 完整浏览器能力
- 下载管理
- 浏览器扩展体系
- DevTools
- 账号同步
- 复杂书签系统

## 在 IDEA 中使用

### 打开 Tool Window

启动带插件的 IDEA 后，可以通过以下方式打开：

- 右侧 Tool Window 栏找到 `ShadowSurf`
- 或从菜单进入 `View` → `Tool Windows` → `ShadowSurf`

### 打开网页

1. 在顶部地址栏输入网址
2. 点击 `Open`，或直接按回车

地址栏支持两种输入：

- 完整 URL：如 `https://example.com`
- 省略协议：如 `example.com`

当省略协议时，插件会自动补成：

```text
https://example.com
```

### 导航按钮

顶部工具栏包含以下操作：

- `←`：后退
- `→`：前进
- `⟳`：刷新当前页面
- `+`：新建标签页
- `×`：关闭当前标签页
- `Dark Page`：切换网页暗色增强
- `Note`：打开阅读摘录，tooltip 会提示快捷键
- `Open`：打开地址栏中的网址

### 阅读摘录

1. 在网页里选中一段文本
2. 点击工具栏 `Note`，或按 `Cmd/Ctrl + Shift + M`
3. 在底部摘录条里补充备注和标签
4. 第一次点击 `Save` 时先选择保存文件
5. 之后点击 `Save` 会继续写入上次选择的文件
6. 如需换文件，点击 `Save As...`

保存内容包括：

- 当前时间
- 页面标题
- 页面 URL
- 选中文本
- 备注
- 标签（可空）

如果没有选中文本，插件只会给出轻提示，不会弹出大窗口。
备注输入区支持多行自动换行，按 `Cmd/Ctrl + Enter` 可直接保存。

保存规则：

- 第一次 `Save`：先选文件位置
- 文件不存在：自动新建
- 文件已存在：可选 `Append` 或 `Overwrite`
- 后续 `Save`：直接保存到上次选定的文件
- `Save As...`：随时重新选择目标文件

### 阅读摘录设置

进入：

- `Settings/Preferences` → `Tools` → `ShadowSurf Reading Notes`

可配置项只有 3 个：

- `Notes Directory`
- `Notes File Name`
- `Resolved Path`

默认值：

- `~/Documents/ShadowSurf`
- `reading-notes.md`

### 标签页行为

- `ShadowSurf` 默认位于右侧，并以分割小窗方式和其他右侧工具窗共存
- 默认启动时会打开一个首页标签页
- 当前默认首页是：`https://www.baidu.com`
- 最多支持 5 个标签页
- 超过 5 个时，会弹出提示，不再继续创建
- 关闭标签页后：
  - 若还有其他标签页，会切换到相邻左侧标签
  - 若已关闭最后一个标签页，会自动重新打开默认首页

### 暗色增强

`Dark Page` 开关并不是网页原生暗黑模式，而是 ShadowSurf 的“暗色增强”：

- 尝试压暗页面背景
- 调整正文文字颜色
- 调整输入框 / 按钮等表单元素背景
- 调整链接颜色，避免过亮或不可读

它的目标是：

- 减少网页大白块
- 降低和 IDEA 暗色主题之间的突兀感

需要注意：

- 不同网站 DOM 结构差异很大
- 该功能是“尽量增强”，不是对所有网站都完美适配
- 某些站点仍可能出现局部区域过亮、样式冲突或颜色不理想

## 运行环境

### 开发环境

- macOS / Linux / Windows 均可，本文示例默认 macOS / Linux Shell
- JDK 21
- Gradle Wrapper（仓库已自带）
- IntelliJ IDEA Community 2024.3 兼容

### 运行依赖

- 目标 IDE 需要支持 `JCEF`
- 如果当前 IDE Runtime 不支持 `JCEF`，插件不会崩溃，而是显示提示：
  - `JCEF is not available in this IDE runtime.`

## 开发说明

### 常用命令

运行测试：

```bash
./gradlew test --stacktrace
```

构建插件：

```bash
./gradlew buildPlugin --stacktrace
```

启动沙箱：

```bash
./gradlew runIde --stacktrace
```

### 关键目录

- `build.gradle.kts`：Gradle 与 IntelliJ Platform 插件配置
- `src/main/resources/META-INF/plugin.xml`：插件声明与 Tool Window 注册
- `src/main/kotlin/com/shadowsurf/plugin/ShadowSurfToolWindowFactory.kt`：Tool Window 工厂
- `src/main/kotlin/com/shadowsurf/plugin/ui/ShadowSurfPanel.kt`：主 UI、地址栏、导航、标签页、暗色开关
- `src/main/kotlin/com/shadowsurf/plugin/browser/BrowserTabManager.kt`：轻量标签页状态管理
- `src/main/kotlin/com/shadowsurf/plugin/browser/DarkModeInjector.kt`：暗色增强脚本注入
- `src/main/kotlin/com/shadowsurf/plugin/browser/SelectionCaptureBridge.kt`：网页选中文本抓取桥接
- `src/main/kotlin/com/shadowsurf/plugin/ui/ReadingNoteBar.kt`：紧凑摘录条 UI
- `src/main/kotlin/com/shadowsurf/plugin/notes/ReadingNoteWriter.kt`：Markdown 摘录写入
- `src/main/kotlin/com/shadowsurf/plugin/settings/ReadingNotesSettings.kt`：摘录目录与文件名设置
- `src/test/kotlin/com/shadowsurf/plugin/browser/BrowserTabManagerTest.kt`：标签页逻辑测试
- `src/test/kotlin/com/shadowsurf/plugin/notes/ReadingNoteWriterTest.kt`：摘录写入测试
- `docs/plans/2026-03-31-shadowsurf-mvp.md`：MVP 计划文档

## 已知限制

- 仅适合轻量浏览，不适合替代完整浏览器
- 暗色增强依赖注入样式，对复杂站点不保证完美
- 当前标签上限固定为 5
- 当前没有书签、历史记录管理、下载管理和 DevTools
- 默认首页当前写死为 `https://www.baidu.com`
- 是否支持网页取决于 IDE 内置 `JCEF` 环境与目标站点兼容性

## 故障排查

### Tool Window 没出现

先确认插件已经成功加载：

- 开发模式下，确认 `./gradlew runIde --stacktrace` 正常启动
- 安装模式下，确认插件已在 IDE 的 `Plugins` 中启用

然后从菜单尝试手动打开：

- `View` → `Tool Windows` → `ShadowSurf`

### 页面打不开

可以按顺序检查：

- 地址是否有效
- 当前网络是否正常
- 目标站点是否限制嵌入式浏览器环境
- IDEA Runtime 是否支持 `JCEF`

### 显示 JCEF 不可用

如果看到：

```text
JCEF is not available in this IDE runtime.
```

说明当前 IDE 运行环境不支持 `JCEF`。这不是插件崩溃，而是插件的预期降级行为。

### 暗色效果不明显

这是预期内的可能情况，因为当前实现是“暗色增强”而不是完整主题适配：

- 某些网页结构过于复杂
- 某些区域使用了强样式覆盖
- 某些站点使用 Canvas / Shadow DOM / 动态渲染，可能无法完全覆盖

## License

当前仓库包含 `LICENSE` 文件。使用或分发前，请以仓库中的许可证内容为准。
