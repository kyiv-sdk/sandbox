//
// Created by Ivan Yurovych on 12/19/18.
//

#include <android/log.h>
#include "Main.h"

std::map<std::string, jclassReference> main::jclassesGlobalReferences;
static JavaVM* gJvm = nullptr;

main::main()
{
    env = nullptr;
}

jclassReference main::getJclassReferenceByName(std::string jclassName){
    for (auto entity : main::jclassesGlobalReferences){
        if (entity.first == jclassName){
            return entity.second;
        }
    }
    throw "My: No such class";
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

    jclass mainActivity = env->FindClass("com/example/iyuro/socketstest/NetworkSingleton");
    jclass globalMainActivity = nullptr;
    jmethodID globalMethodIdMainActivity = nullptr;
    if (mainActivity != nullptr){
        globalMainActivity = (jclass) env->NewGlobalRef(mainActivity);
        globalMethodIdMainActivity = env->GetMethodID(mainActivity, "onSuccessDownload", "(Ljava/lang/String;)V");
    } else {
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", "Failed to find NetworkSingleton class");
    }

    if (globalMethodIdMainActivity == nullptr){
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", "Failed to get method id");
    }

    main::jclassesGlobalReferences.insert(std::pair<std::string, jclassReference>("com/example/iyuro/mktest/NetworkSingleton/onSuccessDownload", jclassReference(globalMainActivity, globalMethodIdMainActivity)));

    return JNI_VERSION_1_4;
}