package com.tencent.mars.xlog;

import android.os.Looper;
import android.os.Process;

public class Xlog {

    private static final int LOG_INSTANCE_PTR = 0;

    public static class XLogConfig {
        // 日志输出等级，低于该值将不会输出到文件中
        public int level = LogLevel.LEVEL_INFO;
        // 写入模式， 推荐 Xlog.AppednerModeAsync
        public int mode = AppenderModel.ASYNC;
        // 日志存储地址
        public String logdir;
        // 日志缓存空间
        public String cachedir;
        // 日志前缀
        public String nameprefix;
        // 加密公钥
        public String pubkey = "";
        // 压缩模式
        public int compressmode = CompressMode.ZLIB_MODE;
        // 压缩等级, [0 - 9]
        public int compresslevel = 0;
        // 缓存天数，该值控制 cacheDir 中存放日志的天数，
        // 如果日志文件最后修改时间大于该配置的参数则将日志文件移动到 logDir 中
        public int cachedays = 0;
    }

    private Xlog(){}

    /**
     * instance
     * @return instance
     */
    private static Xlog getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 加载动态链接库
     */
    public static void loadLibrary() {
        System.loadLibrary("c++_shared");
        System.loadLibrary("marsxlog");
    }

    private static String decryptTag(String tag) {
        return tag;
    }

    public static void logV(String tag, String log) {
        logWriteProxy(LogLevel.LEVEL_VERBOSE, decryptTag(tag), log);
    }

    public static void logD(String tag, String log) {
        logWriteProxy(LogLevel.LEVEL_DEBUG, decryptTag(tag), log);
    }

    public static void logI(String tag, String log) {
        logWriteProxy(LogLevel.LEVEL_INFO, decryptTag(tag), log);
    }

    public static void logW(String tag, String log) {
        logWriteProxy(LogLevel.LEVEL_WARNING, decryptTag(tag), log);
    }

    public static void logE(String tag, String log) {
        logWriteProxy(LogLevel.LEVEL_ERROR, decryptTag(tag), log);
    }

    public static void logF(String tag, String log) {
        logWriteProxy(LogLevel.LEVEL_FATAL, decryptTag(tag), log);
    }

    /**
     * 开启日志
     * @param logConfig 日志配置
     * @param fileSizeByte 单日志文件大小
     * @param aliveSeconds 日志存活时间
     */
    public static void appenderOpen(XLogConfig logConfig, long fileSizeByte, long aliveSeconds) {
        if (logConfig == null){
            throw new IllegalArgumentException("please input config");
        }
        setMaxAliveTime(LOG_INSTANCE_PTR, aliveSeconds);
        setMaxFileSize(LOG_INSTANCE_PTR, fileSizeByte);
        // 开启日志
        getInstance().appenderOpen(logConfig);
    }

    /**
     * 控制台日志开关
     * @param enable 日志开关
     */
    public static void setConsoleLog(boolean enable) {
        setConsoleLogOpen(LOG_INSTANCE_PTR, enable);
    }

    /**
     * 缓存落盘
     * @param syncFlush 是否同步刷入
     */
    public static void flush(boolean syncFlush) {
        getInstance().appenderFlush(LOG_INSTANCE_PTR, syncFlush);

    }

    /**
     * 释放资源
     */
    public static void release() {
        getInstance().appenderClose();
    }

    /**
     * 写入日志
     * @param level 等级
     * @param tag 标签
     * @param log 日志内容
     */
    private static void logWriteProxy(int level, String tag, String log) {
        int pid = Process.myPid();
        long threadId = Thread.currentThread().getId();
        long mainTid = Looper.getMainLooper().getThread().getId();
        // println
        getInstance().logWrite2(LOG_INSTANCE_PTR, level, tag, pid, threadId, mainTid, log);
    }

    private native int getLogLevel(long logInstancePtr);

    private native void setAppenderMode(long logInstancePtr, int mode);

    private native long newXlogInstance(XLogConfig logConfig);

    /**
     * 控制台日志开关
     * @param logInstancePtr
     * @param isOpen 日志开关
     */
    private static native void setConsoleLogOpen(long logInstancePtr, boolean isOpen);

    private native void appenderOpen(XLogConfig logConfig);

    private native void appenderClose();

    private native void appenderFlush(long logInstancePtr, boolean isSync);

    /**
     * 写入日志
     * @param logInstancePtr
     * @param level 日志等级
     * @param tag 标签
     * @param pid pid
     * @param tid thread id
     * @param maintid main thread id
     * @param log log content
     */
    private native void logWrite2(long logInstancePtr, int level, String tag, int pid, long tid, long maintid, String log);

    /**
     * 设置单个文件大小
     * @param logInstancePtr
     * @param fileSizeByte 文件大小 unit: byte
     */
    public static native void setMaxFileSize(long logInstancePtr, long fileSizeByte);

    /**
     * 设置日志文件保存时间，
     * 该值控制logDir中的日志最大保留时长，日志文件最后修改时间 + aliveSeconds > nowTime 则删除日志
     * @param logInstancePtr
     * @param aliveSeconds 保存时间 unit: seconds
     */
    public static native void setMaxAliveTime(long logInstancePtr, long aliveSeconds);

    private static final class Holder {
        static final Xlog INSTANCE = new Xlog();
    }

    /**
     * 日志等级
     */
    public interface LogLevel {
        int LEVEL_ALL = 0;
        int LEVEL_VERBOSE = 0;
        int LEVEL_DEBUG = 1;
        int LEVEL_INFO = 2;
        int LEVEL_WARNING = 3;
        int LEVEL_ERROR = 4;
        int LEVEL_FATAL = 5;
        int LEVEL_NONE = 6;
    }

    /**
     * 压缩等级
     */
    public interface CompressLevel {
        int COMPRESS_LEVEL1 = 1;
        int COMPRESS_LEVEL2 = 2;
        int COMPRESS_LEVEL3 = 3;
        int COMPRESS_LEVEL4 = 4;
        int COMPRESS_LEVEL5 = 5;
        int COMPRESS_LEVEL6 = 6;
        int COMPRESS_LEVEL7 = 7;
        int COMPRESS_LEVEL8 = 8;
        int COMPRESS_LEVEL9 = 9;
    }

    /**
     * 压缩模式
     */
    public interface CompressMode {
        int ZLIB_MODE = 0;
        int ZSTD_MODE = 1;
    }

    /**
     * 日志写入模式
     */
    public interface AppenderModel {
        int ASYNC = 0;
        int SYNC = 1;
    }
}