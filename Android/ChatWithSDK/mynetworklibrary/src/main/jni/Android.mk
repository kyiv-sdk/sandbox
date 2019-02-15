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
LOCAL_MODULE    := libgdndk_prebuilt
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libgdndk_my.so
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../include
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog
LOCAL_MODULE    := libnative-lib
LOCAL_SRC_FILES := ../cpp/jni_helpers/com_example_mynetworklibrary_chat_NativeNetworkManager.cpp \
                   ../cpp/jni_helpers/com_example_mynetworklibrary_url_URL_1NetworkExecutor.cpp \
                   ../cpp/jni_helpers/JNI_Helper.cpp \
                   ../cpp/connection/Basic_Connection.cpp \
                   ../cpp/connection/SSL_Connection.cpp \
                   ../cpp/logger/Logger.cpp \
                   ../cpp/chat/MessageHandlerAdapter.cpp \
                   ../cpp/chat/MessageHandler.cpp \
                   ../cpp/chat/RawMessage.cpp \
                   ../cpp/url/NetworkExecutor.cpp \
                   ../cpp/url/NetworkExecutorAdapter.cpp \
                   ../cpp/url/HTTP_Client.cpp \
                   ../cpp/url/HTTPS_Client.cpp \
                   ../cpp/connection/Dynamics_Connection.cpp \
                   ../cpp/connection/Dynamics_SSL_Connection.cpp

LOCAL_STATIC_LIBRARIES := ssl_prebuilt crypto_prebuilt
LOCAL_SHARED_LIBRARIES := libgdndk_prebuilt

LOCAL_CPPFLAGS += -std=c++11 -fexceptions -lssl -lcrypto

INC = -I$(LOCAL_PATH)/../cpp/chat
INC += -I$(LOCAL_PATH)/../cpp/jni_helpers
INC += -I$(LOCAL_PATH)/../cpp/connection
INC += -I$(LOCAL_PATH)/../cpp/logger
INC += -I$(LOCAL_PATH)/../cpp/url
LOCAL_CFLAGS := $(INC)


include $(BUILD_SHARED_LIBRARY)