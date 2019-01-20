# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

#set(pathToProject D:/School/PRO/CrossPlatformDecoder/src/DecoderApp)
#set(pathToOpenCv D:/Software/OpenCV-android-sdk)

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

#set(CMAKE_VERBOSE_MAKEFILE on)
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

#include_directories(${pathToOpenCv}/sdk/native/jni/include)

file(GLOB decoder_sources
        ../../../deps/djinni/support-lib/jni/*.cpp
        ../../../generated/jni/*.cpp
        ../../../src/cpp/*.cpp
        ../../../src/cpp/shifra/*.cpp
        )

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        decoder

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${decoder_sources} )

#add_library( lib_opencv SHARED IMPORTED )

#set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION D:/School/PRO/CrossPlatformDecoder/src/DecoderApp/openCVLibrary343/src/main/jniLibs/x86_64)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

#target_link_libraries(
##                       # Specifies the target library.
##                       native-lib
##
##                       # Links the target library to the log library
##                       # included in the NDK.
##                       ${log-lib})

include_directories(
        ../../../deps/djinni/support-lib/
        ../../../deps/djinni/support-lib/jni/
        ../../../generated/cpp/
        ../../../generated/jni/
        ../../../src/cpp/
        ../../../src/cpp/shifra
)

target_link_libraries(decoder)