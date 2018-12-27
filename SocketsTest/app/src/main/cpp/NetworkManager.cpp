//
// Created by Ivan Yurovych on 12/26/18.
//

#include <thread>
#include "NetworkManager.h"
#include "NetworkExecutor.h"

void checkPendingExceptions(JNIEnv *env, std::string s)
{
    jboolean flag = env->ExceptionCheck();
    if (flag) {
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", s.c_str());
    }
}

NetworkManager::~NetworkManager()
{
    try
    {
        if (myThread.joinable())
        {
            myThread.join();
        }
    }
    catch (const std::exception& e)
    {
        printf("%s", e.what());
    }
}

void NetworkManager::jni_sendToJava(jobject instance, std::string resultData) {
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

void NetworkManager::makeRequest(std::string hostname, jobject instance,
                                 void (*callback)(jobject, std::string)) {
    NetworkExecutor networkExecutor;
    std::string resultData = networkExecutor.loadData(hostname);
    callback(instance, resultData);
    __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", resultData.c_str());
    pthread_exit(NULL);
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_iyuro_socketstest_NetworkExecutor_cppStartDownloading(JNIEnv *env, jobject instance,
                                                                jstring s)
{
    const char* chostname = env->GetStringUTFChars(s, 0);

    jobject gInstance = env->NewGlobalRef(instance);
    NetworkManager* networker = new NetworkManager;
    networker->myThread = std::thread(NetworkManager::makeRequest, std::string(chostname), gInstance, &NetworkManager::jni_sendToJava);

    return (jlong)networker;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_iyuro_socketstest_NetworkExecutor_cppCloseDownloading(JNIEnv *env, jobject instance, jlong obj)
{
    NetworkManager* networker = (NetworkManager*) obj;
    delete networker;
}