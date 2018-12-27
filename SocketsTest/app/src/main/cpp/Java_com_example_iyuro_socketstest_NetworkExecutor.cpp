//
// Created by Ivan Yurovych on 12/26/18.
//

#include <thread>
#include "NetworkExecutor.h"

jobject globalInstance;

class NetworkExecutorImplementation: public NetworkExecutorAdapter{
public:
    virtual void runCallback(std::string);
    NetworkExecutorImplementation(jobject ninstance,
                                  void (*ncallback)(jobject, std::string));
};

void NetworkExecutorImplementation::runCallback(std::string resultData) {
    JNIcallback(instance, resultData);
}

NetworkExecutorImplementation::NetworkExecutorImplementation(jobject ninstance,
                                                             void (*ncallback)(jobject,
                                                                               std::string))
        : NetworkExecutorAdapter(ninstance, ncallback) {
}

void checkPendingExceptions(JNIEnv *env, std::string s)
{
    jboolean flag = env->ExceptionCheck();
    if (flag) {
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", s.c_str());
    }
}

void jni_sendDataToJava(jobject instance, std::string resultData) {
    main m;
    m.attachEnv();
    JNIEnv *new_env = m.getEnv();
    checkPendingExceptions(new_env, "1");

    jmethodID mjmethodID = main::NetworkExecutorOnSuccessMethodId;
    jclass objectMainActivity = (jclass) instance;

    jstring result = (jstring)new_env->NewGlobalRef(new_env->NewStringUTF(resultData.c_str()));
    new_env->CallVoidMethod(objectMainActivity, mjmethodID, result);

    m.detachMyThread();
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_iyuro_socketstest_NetworkExecutor_cppStartDownloading(JNIEnv *env, jobject instance,
                                                                jstring s)
{
    const char* chostname = env->GetStringUTFChars(s, 0);

    globalInstance = env->NewGlobalRef(instance);

    NetworkExecutorImplementation *networkExecutorImplementation = new NetworkExecutorImplementation(globalInstance, jni_sendDataToJava);

    NetworkExecutor *networkExecutor = new NetworkExecutor();
    networkExecutor->start(std::string(chostname), networkExecutorImplementation);

    return (jlong)networkExecutor;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_iyuro_socketstest_NetworkExecutor_cppCloseDownloading(JNIEnv *env, jobject instance, jlong obj)
{
    NetworkExecutor* networkExecutor = (NetworkExecutor*) obj;
    delete networkExecutor;
}