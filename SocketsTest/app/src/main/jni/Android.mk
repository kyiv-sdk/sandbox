LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_MODULE    := native-lib
LOCAL_SRC_FILES := ../cpp/JNI_Helper.cpp \
    ../cpp/NetworkExecutor.cpp \
    ../cpp/com_example_iyuro_socketstest_NetworkExecutor.cpp \
    ../cpp/NetworkExecutorAdapter.cpp

LOCAL_CPPFLAGS += -std=c++11 -fexceptions -pthread

include $(BUILD_SHARED_LIBRARY)