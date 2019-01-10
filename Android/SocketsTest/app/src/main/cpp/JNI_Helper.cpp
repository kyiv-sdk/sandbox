//
// Created by Ivan Yurovych on 12/19/18.
//

#include "JNI_Helper.h"

#include <android/log.h>

static JavaVM* gJvm = nullptr;
jmethodID JNI_Helper::NetworkExecutorOnSuccessMethodId;
jmethodID JNI_Helper::MessageHandlerOnSuccessMethodId;

JNI_Helper::JNI_Helper()
{
    env = nullptr;
}

JNIEnv* JNI_Helper::attachEnv()
{
    int status = gJvm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6);
    if(status < 0) {
        status = gJvm->AttachCurrentThread(&env, NULL);
        if(status < 0) {
            return nullptr;
        }
    }
    return env;
}

JNIEnv* JNI_Helper::getEnv()
{
    return env;
}

JavaVM *JNI_Helper::getJVM()
{
    return gJvm;
}

void JNI_Helper::detachMyThread()
{
    gJvm->DetachCurrentThread();
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *pjvm, void *reserved)
{
    gJvm = pjvm;
    JNIEnv *env = nullptr;

    if (gJvm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }

    jclass networkSingleton = env->FindClass("com/example/iyuro/socketstest/URL/NetworkExecutor");
    jmethodID globalMethodIdNetworkSingleton = nullptr;
    if (networkSingleton != nullptr){
        globalMethodIdNetworkSingleton = env->GetMethodID(networkSingleton, "onSuccessDownload", "([B)V");
    } else {
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", "Failed to find NetworkManager class");
    }

    if (globalMethodIdNetworkSingleton == nullptr){
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", "Failed to get method id");
    }

    JNI_Helper::NetworkExecutorOnSuccessMethodId = globalMethodIdNetworkSingleton;


    jclass messageHandlerSingleton = env->FindClass("com/example/iyuro/socketstest/Messenger/MessageHandler");
    jmethodID globalMethodIdMessageHandler = nullptr;
    if (messageHandlerSingleton != nullptr){
        globalMethodIdMessageHandler = env->GetMethodID(messageHandlerSingleton, "onMessageReceive", "([B)V");
    } else {
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", "Failed to find NetworkManager class");
    }

    if (globalMethodIdMessageHandler == nullptr){
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", "Failed to get method id");
    }

    JNI_Helper::MessageHandlerOnSuccessMethodId = globalMethodIdMessageHandler;

    return JNI_VERSION_1_4;
}

void JNI_Helper::checkPendingExceptions(JNIEnv *env, std::string s)
{
    jboolean flag = env->ExceptionCheck();
    if (flag) {
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", s.c_str());
    }
}