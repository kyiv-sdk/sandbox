LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := ssl_prebuilt
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libssl.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := crypto_prebuilt
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libcrypto.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_MODULE    := native-lib
LOCAL_SRC_FILES := ../cpp/JNI_Helper.cpp \
    ../cpp/NetworkExecutor.cpp \
    ../cpp/com_example_iyuro_socketstest_NetworkExecutor.cpp \
    ../cpp/NetworkExecutorAdapter.cpp \
    ../cpp/HTTP_Client.cpp \
    ../cpp/HTTPS_Client.cpp \
    ../cpp/Basic_Connection.cpp \
    ../cpp/SSL_Connection.cpp

LOCAL_C_INCLUDES = ../include

LOCAL_STATIC_LIBRARIES := ssl_prebuilt crypto_prebuilt

LOCAL_CPPFLAGS += -std=c++11 -fexceptions -pthread -lssl -lcrypto

include $(BUILD_SHARED_LIBRARY)