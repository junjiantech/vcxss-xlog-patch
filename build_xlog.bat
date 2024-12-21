@echo off
$env:NDK_ROOT="D:\environment\Android\ndk\20.0.5594570\"
@REM clone mars
git clone https://github.com/Tencent/mars.git
@REM 应用变更
cd mars
git apply ../0001-local-change-xlog.patch
@REM 进入xlog编译目录
CD mars
@REM 编译xlog二进制文件
python build_android.py "--target libzstd_static marsxlog" "armeabi-v7a" "arm64-v8a"
@REM 移动libs二进制文件
cp -r libraries/mars_android_sdk/libs/* ../../libs-xlog/src/main/jniLibs/
@REM 删除仓库
cd ../../
rmdir /s/q mars
@REM 删除 stn so
rm libs-xlog/src/main/jniLibs/*/libmarsstn.so