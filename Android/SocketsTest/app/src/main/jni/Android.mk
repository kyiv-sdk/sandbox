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
LOCAL_SRC_FILES := ../cpp/jni_helpers/JNI_Helper.cpp \
                   ../cpp/url/NetworkExecutor.cpp \
                   ../cpp/url/NetworkExecutorAdapter.cpp \
                   ../cpp/url/HTTP_Client.cpp \
                   ../cpp/url/HTTPS_Client.cpp \
                   ../cpp/connection/Basic_Connection.cpp \
                   ../cpp/connection/SSL_Connection.cpp \
                   ../cpp/jni_helpers/com_example_iyuro_socketstest_chat_messenger_MessageHandler.cpp \
                   ../cpp/chat/MessageHandlerAdapter.cpp \
                   ../cpp/jni_helpers/com_example_iyuro_socketstest_url_NetworkExecutor.cpp \
                   ../cpp/chat/MessageHandler.cpp \
                   ../cpp/logger/Logger.cpp




LOCAL_STATIC_LIBRARIES := ssl_prebuilt crypto_prebuilt

LOCAL_CPPFLAGS += -std=c++11 -fexceptions -pthread -lssl -lcrypto


INC = -I$(LOCAL_PATH)/../cpp/url
INC += -I$(LOCAL_PATH)/../cpp/jni_helpers
INC += -I$(LOCAL_PATH)/../cpp/connection
INC += -I$(LOCAL_PATH)/../cpp/chat
INC += -I$(LOCAL_PATH)/../cpp/logger
LOCAL_CFLAGS := $(INC)


include $(BUILD_SHARED_LIBRARY)