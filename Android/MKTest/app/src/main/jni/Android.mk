LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_MODULE    := hello-jni
LOCAL_SRC_FILES := ../cpp/main.cpp \
    ../cpp/MyLog.cpp \
    ../cpp/TestData.cpp \
    ../cpp/MyThread.cpp \
    ../cpp/jclassReference.cpp \
    ../cpp/WebLoader.cpp

LOCAL_CPPFLAGS += -std=c++11 -fexceptions -pthread

include $(BUILD_SHARED_LIBRARY)