LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_MODULE    := native-lib
LOCAL_SRC_FILES := ../cpp/native-lib.cpp \
    ../cpp/jclassReference.cpp \
    ../cpp/main.cpp

LOCAL_CPPFLAGS += -std=c++11 -fexceptions -pthread

include $(BUILD_SHARED_LIBRARY)