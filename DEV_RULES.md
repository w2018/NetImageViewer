# 图图项目 - 开发自动化规则

## 规则1：编译后自动安装APK
- 每次 `./gradlew assembleDebug` 编译成功（BUILD SUCCESSFUL）后
- 自动执行：`/usr/lib/android-sdk/platform-tools/adb install -r app/build/outputs/apk/debug/app-debug.apk`
- 目标设备：emulator-5554

## 规则2：编译安装成功后发送通知
- **触发条件**：规则1执行完毕（编译成功 + APK安装完成）
- **通知方式**：在对话中主动输出通知，包含：
  - ✅ 编译结果（BUILD SUCCESSFUL + 耗时）
  - ✅ 安装结果（Success/Fail）
  - ✅ 本次改动的简要说明
- **创建时间**：2026-06-03 10:48

## 项目信息
- 包名：com.operit.netimageviewer
- 应用名：图图 NetImageViewer
- 编译命令：./gradlew assembleDebug
- ADB路径：/usr/lib/android-sdk/platform-tools/adb
