@echo off
chcp 65001 >nul
echo ==========================================
echo   🦐 安卓开发虾 - 记账本 APP 编译脚本
echo ==========================================
echo.

set PROJECT_DIR=D:\yangdong\AICode\AccountBook
set SDK_DIR=D:\yangdong\android-sdk

echo [1/3] 检查 Android SDK...
if not exist "%SDK_DIR%" (
    echo ❌ Android SDK 未找到: %SDK_DIR%
    echo 请先安装 Android SDK，或修改 SDK_DIR 变量
    pause
    exit /b 1
)
echo ✅ SDK 路径: %SDK_DIR%

echo.
echo [2/3] 更新 local.properties...
echo sdk.dir=%SDK_DIR:\=\\% > "%PROJECT_DIR%\local.properties"
echo ✅ local.properties 已更新

echo.
echo [3/3] 开始编译 APK...
cd /d "%PROJECT_DIR%"
call gradlew.bat assembleDebug --stacktrace

if %ERRORLEVEL% equ 0 (
    echo.
    echo ==========================================
    echo ✅ 编译成功！
    echo APK 路径:
    echo   %PROJECT_DIR%\app\build\outputs\apk\debug\app-debug.apk
    echo ==========================================
    
    rem 打开 APK 所在目录
    explorer "%PROJECT_DIR%\app\build\outputs\apk\debug\"
) else (
    echo.
    echo ==========================================
    echo ❌ 编译失败，请查看上方错误信息
    echo ==========================================
)

pause
