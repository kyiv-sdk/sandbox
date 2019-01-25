LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_MODULE    := native-lib
LOCAL_SRC_FILES := ../cpp/jni_helpers/JNI_Helper.cpp \
                   ../cpp/url/NetworkExecutor.cpp \
                   ../cpp/url/NetworkExecutorAdapter.cpp \
                   ../cpp/url/HTTP_Client.cpp \
                   ../cpp/url/HTTPS_Client.cpp \
                   ../cpp/jni_helpers/com_example_iyuro_socketstest_url_NetworkExecutor.cpp

LOCAL_CPPFLAGS += -std=c++11 -fexceptions -pthread


INC = -I$(LOCAL_PATH)/../cpp/url
INC += -I$(LOCAL_PATH)/../cpp/jni_helpers
INC += -I$(LOCAL_PATH)/../cpp/jni_helpers
INC += -I$(LOCAL_PATH)/../../../../mynetworklibrary/src/main/cpp/connection
INC += -I$(LOCAL_PATH)/../../../../mynetworklibrary/src/main/cpp/logger
LOCAL_CFLAGS := $(INC)


include $(BUILD_SHARED_LIBRARY)