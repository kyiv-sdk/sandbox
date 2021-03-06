//
// Created by Ivan Yurovych on 12/19/18.
//

#include "JNI_Helper.h"

#include <Logger.h>

static JavaVM* gJvm = nullptr;
jmethodID JNI_Helper::mNetworkExecutorOnSuccessMethodId;
jmethodID JNI_Helper::mMessageHandlerOnSuccessMethodId;

JNI_Helper::JNI_Helper()
{
    mEnv = nullptr;
}

JNIEnv* JNI_Helper::attachEnv()
{
    int status = gJvm->GetEnv(reinterpret_cast<void**>(&mEnv), JNI_VERSION_1_6);
    if(status < 0)
    {
        status = gJvm->AttachCurrentThread(&mEnv, NULL);
        if(status < 0)
        {
            return nullptr;
        }
    }
    return mEnv;
}

JNIEnv* JNI_Helper::getEnv()
{
    return mEnv;
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

    jclass networkSingleton = env->FindClass("com/example/iyuro/socketstest/url/NetworkExecutor");
    jmethodID globalMethodIdNetworkSingleton = nullptr;
    if (networkSingleton != nullptr)
    {
        globalMethodIdNetworkSingleton = env->GetMethodID(networkSingleton, "onSuccessDownload", "([B)V");
    } else {
        Logger::log("Failed to find NetworkManager class");
    }

    if (globalMethodIdNetworkSingleton == nullptr)
    {
        Logger::log("Failed to get method id");
    }

    JNI_Helper::mNetworkExecutorOnSuccessMethodId = globalMethodIdNetworkSingleton;


    jclass messageHandlerSingleton = env->FindClass("com/example/iyuro/socketstest/chat/messenger/NetworkManager");
    jmethodID globalMethodIdMessageHandler = nullptr;
    if (messageHandlerSingleton != nullptr)
    {
        globalMethodIdMessageHandler = env->GetMethodID(messageHandlerSingleton, "onMessageReceive", "([B)V");
    } else {
        Logger::log("Failed to find NetworkManager class");
    }

    if (globalMethodIdMessageHandler == nullptr)
    {
        Logger::log("Failed to get method id");
    }

    JNI_Helper::mMessageHandlerOnSuccessMethodId = globalMethodIdMessageHandler;

    return JNI_VERSION_1_4;
}

void JNI_Helper::checkPendingExceptions(JNIEnv *env, std::string s)
{
    jboolean flag = env->ExceptionCheck();
    if (flag)
    {
        Logger::log(s);
    }
}