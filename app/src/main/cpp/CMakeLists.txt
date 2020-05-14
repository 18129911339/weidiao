# CMake版本信息
cmake_minimum_required(VERSION 3.4.1)


set(opencv-dir ../../../opencv)

#link library dir
link_directories(
        ${opencv-dir}/libs/${ANDROID_ABI}
        ${opencv-dir}/3rdparty/${ANDROID_ABI}
        ${opencv-dir}/staticlibs/${ANDROID_ABI}
)

#include header
include_directories(
        .
        ../../../opencv/include)


# 添加待编译的cpp文件
add_library(opencv SHARED opencv-jni.cpp)

target_link_libraries(opencv c++_shared android jnigraphics m log opencv_java4)