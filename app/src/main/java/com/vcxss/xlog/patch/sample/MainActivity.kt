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
import com.tencent.mars.xlog.Xlog.XLogConfig
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // 初始化
        SoLoader.init(applicationContext, true)
        // 加载动态库
        SoLoader.loadLibrary("c++_shared")
        SoLoader.loadLibrary("marsxlog")

        val logConfig = XLogConfig()
        logConfig.logdir = File(cacheDir, "log").absolutePath
        logConfig.cachedir = File(cacheDir, "log_cache").absolutePath
        logConfig.nameprefix = "Xlog"
        logConfig.compresslevel = 6
        logConfig.compressmode = Xlog.ZLIB_MODE
        logConfig.level = Xlog.LEVEL_INFO
        logConfig.cachedays = 7
        logConfig.mode = Xlog.AppednerModeAsync
        // 创建Xlog实例
        val xlog = Xlog()
        // 开启console
        xlog.setConsoleLogOpen(0, true)
        // 设置单文件大小
        xlog.setMaxFileSize(0, 10 * 1024 * 1024)
        // 设置最长有效天数
        xlog.setMaxAliveTime(0, 7 * 24 * 60 * 60)
        // 开启xlogApper
        xlog.openAppender(logConfig)
        val filename = "vlog"
        val pid = Process.myPid()
        val threadId = Thread.currentThread().id
        val mainTid = Looper.getMainLooper().thread.id
        Thread {
            while (true) {
                xlog.logI(0, "Vlog", filename, "", 0, pid, threadId, mainTid, "IIIIIIIII")
                Thread.sleep(500)
            }
        }.start()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}