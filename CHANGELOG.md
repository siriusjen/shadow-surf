# Changelog

本文件记录 ShadowSurf 的版本变化。当前阶段以 MVP 演进为主，条目会优先反映用户可感知的能力、已完成验证以及明确的范围边界。

## 0.1.0

### Added

- 新增 `ShadowSurf` IDEA Tool Window
- 新增地址栏与页面打开能力
- 新增前进 / 后退 / 刷新操作
- 新增最多 5 个轻量标签页能力
- 新增跟随 IDEA 主题的插件 UI
- 新增网页暗色增强开关
- 新增阅读摘录快捷键 `Cmd/Ctrl + Shift + M`
- 新增紧凑摘录条，可填写备注和标签
- 新增本地 Markdown 追加写入能力
- 新增阅读摘录设置页，可配置目录和文件名
- 新增摘录条 `Save As...` 按钮
- 新增首次 `Save` 选择保存文件能力
- 新增已存在文件的 `Append / Overwrite` 选择
- 新增工具栏 `Note` 入口与快捷键提示
- 新增备注多行自动换行输入与 `Cmd/Ctrl + Enter` 保存
- 新增默认右侧分割小窗停靠方式，更适合右下角阅读
- 新增 `JCEF` 不可用时的降级提示

### Verified

- `./gradlew test --stacktrace` 通过
- `./gradlew buildPlugin --stacktrace` 通过
- `./gradlew runIde` 可启动插件沙箱
- 已验证 Tool Window、网页打开、导航、标签页切换与暗色增强可用
- 已补充阅读摘录相关单测与编译验证

### Limitations

- 当前版本不是完整浏览器
- 不包含下载管理、扩展系统、DevTools、账号同步与复杂书签
- 阅读摘录当前只写入单个本地 Markdown 文件
- 暗色增强为尽力而为，不保证所有网站都获得理想效果
- 当前默认首页固定为 `https://www.baidu.com`
