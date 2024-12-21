## 使用方式
``` kotlin
// Xlog 安全加载
Xlog.loadLibrary()
// 日志Appender配置
val logConfig = Xlog.XLogConfig()
logConfig.logdir = File(cacheDir, "logs").absolutePath
logConfig.cachedir = File(cacheDir, "log_cache").absolutePath
logConfig.nameprefix = "Xlog"
logConfig.compresslevel = Xlog.CompressLevel.COMPRESS_LEVEL6
logConfig.compressmode = Xlog.CompressMode.ZLIB_MODE
logConfig.level = Xlog.LogLevel.LEVEL_INFO
logConfig.cachedays = 1
logConfig.mode = Xlog.AppenderModel.ASYNC
// 单日志文件大小
val logFileSize = 10 * 1024 * 1024L
// 日志保留时间. 请注意理解 cacheDays 机制
val logFileAliveTime = 7 * 24 * 60 * 60L
// 开启日志
Xlog.appenderOpen(logConfig, logFileSize, logFileAliveTime)
 // 控制台日志开关
Xlog.setConsoleLog(true)
// 日志测试
val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA)
 Xlog.logD(TAG, MessageFormat.format("time: {0}", formatter.format(Date())))
Xlog.logV(TAG, MessageFormat.format("time: {0}", formatter.format(Date())))
Xlog.logI(TAG, MessageFormat.format("time: {0}", formatter.format(Date())))
Xlog.logW(TAG, MessageFormat.format("time: {0}", formatter.format(Date())))
Xlog.logE(TAG, MessageFormat.format("time: {0}", formatter.format(Date())))
Xlog.logF(TAG, MessageFormat.format("time: {0}", formatter.format(Date())))
```

## 编译
### Windows
``` shell
$env:NDK_ROOT="your ndk path"
# clone mars
git clone https://github.com/Tencent/mars.git
# 应用变更
cd mars
git apply ../0001-local-change-xlog.patch
# 进入xlog编译目录
cd mars
# 编译xlog二进制文件
python build_android.py "--target libzstd_static marsxlog" "armeabi-v7a" "arm64-v8a"
# 移动libs二进制文件
cp -r libraries/mars_android_sdk/libs/* ../../libs-xlog/src/main/jniLibs/
# 删除仓库
cd ../../
rmdir /s/q mars
# 删除 stn so
rm libs-xlog/src/main/jniLibs/*/libmarsstn.so
# 编译aar
./gradlew :libs-xlog:build
```

## License
该项目使用的 MIT 协议，详细请参考 [LICENSE](https://github.com/junjiantech/vcxss-xlog-patch/blob/master/LICENSE)。

## Thank
[[Mars](https://github.com/Tencent/mars)]: Mars is a cross-platform network component developed by WeChat.
