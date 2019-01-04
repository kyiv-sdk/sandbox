//
// Created by Ivan Yurovych on 12/26/18.
//

#include <jni.h>
#include <android/log.h>

#include "JNI_Helper.h"
#include "NetworkExecutorAdapter.h"
#include "NetworkExecutor.h"

class NetworkExecutorImplementation: public NetworkExecutorAdapter{
private:
    void (*JNIcallback)(jobject, std::string *);
    jobject instance;
    JNIEnv *m_env;
public:
    void runCallback(std::string *) override;
    NetworkExecutorImplementation(JNIEnv *env, jobject ninstance,
                                  void (*ncallback)(jobject, std::string *));
    virtual ~NetworkExecutorImplementation();
};

void NetworkExecutorImplementation::runCallback(std::string *resultData)
{
    JNIcallback(instance, resultData);
}

NetworkExecutorImplementation::NetworkExecutorImplementation(JNIEnv *t_env, jobject ninstance,
                                                             void (*ncallback)(jobject,
                                                                               std::string*))
{
    JNIcallback = ncallback;
    instance = ninstance;
    m_env = t_env;
}

NetworkExecutorImplementation::~NetworkExecutorImplementation() {
    m_env->DeleteGlobalRef(instance);
}

void checkPendingExceptions(JNIEnv *env, std::string s)
{
    jboolean flag = env->ExceptionCheck();
    if (flag) {
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", s.c_str());
    }
}

void jni_sendDataToJava(jobject instance, std::string *resultData)
{
    main m;
    m.attachEnv();
    JNIEnv *new_env = m.getEnv();
    checkPendingExceptions(new_env, "1");

    jmethodID mjmethodID = main::NetworkExecutorOnSuccessMethodId;
    jclass objectMainActivity = (jclass) instance;

    int size = (*resultData).size();
    jbyteArray result = new_env->NewByteArray(size);
    new_env->SetByteArrayRegion(result, 0, (*resultData).size(), (const jbyte*)(*resultData).c_str());


    new_env->CallVoidMethod(objectMainActivity, mjmethodID, result);

    new_env->DeleteLocalRef(result);

    m.detachMyThread();
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_iyuro_socketstest_NetworkExecutor_cppStartDownloading(JNIEnv *env, jobject instance,
                                                                jstring t_protocol, jstring t_host, jint t_port)
{
    jobject globalInstance = env->NewGlobalRef(instance);

    NetworkExecutorImplementation *networkExecutorImplementation = new NetworkExecutorImplementation(env, globalInstance, jni_sendDataToJava);

    NetworkExecutor *networkExecutor = new NetworkExecutor(networkExecutorImplementation);
    const char* m_protocol = env->GetStringUTFChars(t_protocol, 0);
    const char* m_host = env->GetStringUTFChars(t_host, 0);

    networkExecutor->download(m_protocol, m_host, (int)t_port);

    return (jlong)networkExecutor;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_iyuro_socketstest_NetworkExecutor_cppCloseDownloading(JNIEnv *env, jobject instance, jlong obj)
{
    NetworkExecutor* networkExecutor = (NetworkExecutor*) obj;
    delete networkExecutor;
}