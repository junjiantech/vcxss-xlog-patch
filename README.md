# 编译
## Windows
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
# License
该项目使用的 MIT 协议，详细请参考 [LICENSE](https://github.com/junjiantech/vcxss-xlog-patch/blob/master/LICENSE)。

# Thank
[Mars](https://github.com/Tencent/mars): Mars is a cross-platform network component developed by WeChat.
