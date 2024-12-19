package com.vcxss.xlog.patch.sample

import android.app.Application
import com.facebook.soloader.SoLoader
import com.jakewharton.processphoenix.ProcessPhoenix
import com.tencent.mars.xlog.Xlog
import java.io.File

class VcxssApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        if (ProcessPhoenix.isPhoenixProcess(this)) {
            return;
        }
        // 初始化SoLoader
        SoLoader.init(this, true)
        // Xlog 安全加载
        Xlog.safeModelLoadLibrary()

        // 初始化日志
        initLogAppender()
        // 发生异常时释放资源
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            e.printStackTrace()
            // 释放Xlog
            Xlog.release()
            // 重新拉起App
            ProcessPhoenix.triggerRebirth(this);
        }
    }

    private fun initLogAppender() {
        // 控制台日志开关
        Xlog.setConsoleLog(true)
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
    }
}