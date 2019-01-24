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
LOCAL_SRC_FILES := ../cpp/jni_helpers/com_example_iyuro_ssl_chat_network_NetworkManager.cpp \
                   ../cpp/jni_helpers/JNI_Helper.cpp \
                   ../cpp/connection/Basic_Connection.cpp \
                   ../cpp/connection/SSL_Connection.cpp \
                   ../cpp/chat/MessageHandlerAdapter.cpp \
                   ../cpp/chat/MessageHandler.cpp \
                   ../cpp/logger/Logger.cpp \
                   ../cpp/chat/RawMessage.cpp

LOCAL_STATIC_LIBRARIES := ssl_prebuilt crypto_prebuilt

LOCAL_CPPFLAGS += -std=c++11 -fexceptions -pthread -lssl -lcrypto


INC = -I$(LOCAL_PATH)/../cpp/jni_helpers
INC += -I$(LOCAL_PATH)/../cpp/connection
INC += -I$(LOCAL_PATH)/../cpp/chat
INC += -I$(LOCAL_PATH)/../cpp/logger
LOCAL_CFLAGS := $(INC)


include $(BUILD_SHARED_LIBRARY)