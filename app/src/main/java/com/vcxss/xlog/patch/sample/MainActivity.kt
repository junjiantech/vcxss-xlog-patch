package com.vcxss.xlog.patch.sample

import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.tencent.mars.xlog.Xlog
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.logging.SimpleFormatter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // 测试日志
        testRollLogging()

        // 测试异常
        findViewById<View>(R.id.test_crash).setOnClickListener {
            val i = 1 / 0
        }
    }

    private fun testRollLogging() {
        Thread {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA)
            while (true) {
                Xlog.logI(TAG, MessageFormat.format("time: {0}", formatter.format(Date())))
                Thread.sleep(50)
            }
        }.start()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}