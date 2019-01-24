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
    JNI_Helper();
    static jmethodID mMessageHandlerOnSuccessMethodId;

    JNIEnv *getEnv();
    JNIEnv *attachEnv();
    void detachMyThread();
    static JavaVM* getJVM();
    void checkPendingExceptions(JNIEnv *env, std::string s);
private:
    JNIEnv *mEnv;
};


#endif //MKTEST_MAIN_H
