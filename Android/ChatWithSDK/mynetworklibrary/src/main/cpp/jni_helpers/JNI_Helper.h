//
// Created by Ivan Yurovych on 12/19/18.
//

#ifndef MKTEST_MAIN_H
#define MKTEST_MAIN_H

#include <jni.h>
#include <string>

class JNI_Helper
{
public:
    static jmethodID mMessageHandlerOnSuccessMethodId;
    static jmethodID mNetworkExecutorOnSuccessMethodId;

    static JavaVM* getJVM();
    static jmethodID getGlobalMethodIdNetworkSingleton(JNIEnv *env);
    static jmethodID getGlobalMethodIdMessageHandler(JNIEnv *env);

    JNI_Helper();

    JNIEnv *getEnv();
    JNIEnv *attachEnv();
    void detachMyThread();
    void checkPendingExceptions(JNIEnv *env, std::string s);
private:
    JNIEnv *mEnv;
};


#endif //MKTEST_MAIN_H
