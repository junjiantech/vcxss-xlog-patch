package com.vcxss.xlog.patch.sample

import android.os.Bundle
import android.os.Looper
import android.os.Process
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.facebook.soloader.SoLoader
import com.tencent.mars.xlog.Xlog
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // 初始化
        SoLoader.init(applicationContext, true)
        // 安全加载仓库
        Xlog.safeModelLoadLibrary()
        // 创建Xlog实例
        val xlog = Xlog()
        // 开启console
        xlog.setConsoleLogOpen(0, true)
        // 设置单文件大小
        xlog.setMaxFileSize(0, 5 * 1024 * 1024)
        // 设置最长有效天数
        xlog.setMaxAliveTime(0, 7 * 24 * 60 * 60)
        // 日志Appender配置
        val logConfig = Xlog.XLogConfig()
        logConfig.logdir = File(cacheDir, "logs").absolutePath
        logConfig.cachedir = File(cacheDir, "log_cache").absolutePath
        logConfig.nameprefix = "Xlog"
        logConfig.compresslevel = 6
        logConfig.compressmode = Xlog.ZLIB_MODE
        logConfig.level = Xlog.LEVEL_INFO
        logConfig.cachedays = 1
        logConfig.mode = Xlog.AppednerModeAsync
        // 开启日志追加
        xlog.appenderOpen(logConfig)
        val pid = Process.myPid()
        val threadId = Thread.currentThread().id
        val mainTid = Looper.getMainLooper().thread.id
        Thread {
            while (true) {
                try {
                    xlog.logI("Vlog", pid, threadId, mainTid, "sys time: ${System.currentTimeMillis()}")
                    var i = 1/ 0
                }catch (ex: Exception){
                    xlog.logE("Vlog", pid, threadId, mainTid, ex.message + "\n" + ex.stackTraceToString())
                }
                Thread.sleep(100)
            }
        }.start()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}