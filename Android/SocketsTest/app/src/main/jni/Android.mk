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
LOCAL_SRC_FILES := ../cpp/JNI/JNI_Helper.cpp \
    ../cpp/URL/NetworkExecutor.cpp \
    ../cpp/JNI/com_example_iyuro_socketstest_URL_NetworkExecutor.cpp \
    ../cpp/URL/NetworkExecutorAdapter.cpp \
    ../cpp/URL/HTTP_Client.cpp \
    ../cpp/URL/HTTPS_Client.cpp \
    ../cpp/Connection/Basic_Connection.cpp \
    ../cpp/Connection/SSL_Connection.cpp \
    ../cpp/JNI/com_example_iyuro_socketstest_Messenger_MessageHandler.cpp \
    ../cpp/Chat/MessageHandlerAdapter.cpp \
    ../cpp/Chat/MessageHandler.cpp

LOCAL_C_INCLUDES = ../include

LOCAL_STATIC_LIBRARIES := ssl_prebuilt crypto_prebuilt

LOCAL_CPPFLAGS += -std=c++11 -fexceptions -pthread -lssl -lcrypto

include $(BUILD_SHARED_LIBRARY)