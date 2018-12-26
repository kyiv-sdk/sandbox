//
// Created by Ivan Yurovych on 12/19/18.
//

#include <android/log.h>
#include "Main.h"

static JavaVM* gJvm = nullptr;
jmethodID main::NetworkSingletonOnSuccessMethodId;

main::main()
{
    env = nullptr;
}

JNIEnv* main::attachEnv() {
    int status = gJvm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6);
    if(status < 0) {
        status = gJvm->AttachCurrentThread(&env, NULL);
        if(status < 0) {
            return nullptr;
        }
    }
    return env;
}

JNIEnv* main::getEnv() {
    return env;
}

JavaVM *main::getJVM() {
    return gJvm;
}

void main::detachMyThread() {
    gJvm->DetachCurrentThread();
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *pjvm, void *reserved) {
    gJvm = pjvm;
    JNIEnv *env = nullptr;

    if (gJvm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }

    jclass networkSingleton = env->FindClass("com/example/iyuro/socketstest/NetworkSingleton");
    jmethodID globalMethodIdNetworkSingleton = nullptr;
    if (networkSingleton != nullptr){
        globalMethodIdNetworkSingleton = env->GetMethodID(networkSingleton, "onSuccessDownload", "(Ljava/lang/String;)V");
    } else {
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", "Failed to find NetworkSingleton class");
    }

    if (globalMethodIdNetworkSingleton == nullptr){
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", "Failed to get method id");
    }

    main::NetworkSingletonOnSuccessMethodId = globalMethodIdNetworkSingleton;

    return JNI_VERSION_1_4;
}