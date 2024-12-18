
@REM clone mars
git clone https://github.com/Tencent/mars.git
@REM 进入xlog编译目录
CD mars/mars
@REM 编译xlog二进制文件
python build_android.py "--target libzstd_static marsxlog" "armeabi-v7a" "arm64-v8a"
@REM 移动libs二进制文件
cp -r libraries/mars_android_sdk/libs/* ../../libs-xlog/src/main/jniLibs/