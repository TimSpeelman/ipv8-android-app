set ANDROID_HOME=C:\Users\Tim\AppData\Local\Android\Sdk
set ANDROID_NDK_HOME=C:\Users\Tim\AppData\Local\Android\Sdk\ndk\android-ndk-r13b
call gradlew assembleDebug --refresh-dependencies --info
adb install -r app\build\outputs\apk\app-debug.apk
pause
