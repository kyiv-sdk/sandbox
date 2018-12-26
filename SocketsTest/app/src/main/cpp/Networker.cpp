//
// Created by Ivan Yurovych on 12/26/18.
//

#include <thread>
#include "Networker.h"
#include "NetworkExecutor.h"

void checkPendingExceptions(JNIEnv *env, std::string s){
    jboolean flag = env->ExceptionCheck();
    if (flag) {
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", s.c_str());
    }
}

void Networker::makeRequest(std::string hostname, jobject instance){

    NetworkExecutor networkExecutor;
    std::string resultData = networkExecutor.loadData(hostname);

    __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", resultData.c_str());

    main m;
    m.attachEnv();
    JNIEnv *new_env = m.getEnv();
    checkPendingExceptions(new_env, "1");

    jmethodID mjmethodID = main::NetworkSingletonOnSuccessMethodId;
    jclass objectMainActivity = (jclass) instance;

    jstring result = (jstring)new_env->NewGlobalRef(new_env->NewStringUTF(resultData.c_str()));
    new_env->CallVoidMethod(objectMainActivity, mjmethodID, result);

    m.detachMyThread();

    pthread_exit(NULL);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_iyuro_socketstest_NetworkSingleton_makeRequest(JNIEnv *env, jobject instance,
                                                                jstring s) {
    const char* chostname = env->GetStringUTFChars(s, 0);

    jobject gInstance = env->NewGlobalRef(instance);
    Networker networker;
    networker.myThread = std::thread(Networker::makeRequest, std::string(chostname), gInstance);
    networker.myThread.join();
}