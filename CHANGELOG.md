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
- 新增 `JCEF` 不可用时的降级提示

### Verified

- `./gradlew test --stacktrace` 通过
- `./gradlew buildPlugin --stacktrace` 通过
- `./gradlew runIde` 可启动插件沙箱
- 已验证 Tool Window、网页打开、导航、标签页切换与暗色增强可用

### Limitations

- 当前版本不是完整浏览器
- 不包含下载管理、扩展系统、DevTools、账号同步与复杂书签
- 暗色增强为尽力而为，不保证所有网站都获得理想效果
- 当前默认首页固定为 `https://www.baidu.com`
