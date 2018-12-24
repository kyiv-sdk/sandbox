#include <iostream>
#include <android/log.h>
#include "TestData.h"

extern "C"
JNIEXPORT jobject JNICALL
Java_com_example_iyuro_mktest_MainActivity_getNewTestData(JNIEnv *env, jclass type,
                                                      jint x) {
    return TestData::getNewTestData(env, type, x);
}

jobject TestData::getNewTestData(JNIEnv *env, jobject type,
                                 jint x) {
    jclass jclazz = nullptr;
    jmethodID mjmethodID = nullptr;
    jobject newObject = nullptr;
    jclassReference jclassR = main::getJclassReferenceByName("com/example/iyuro/mktest/TestData/<init>");

    jclazz = jclassR.getJclazz();
    mjmethodID = jclassR.getJmID();
    newObject = nullptr;
    if (mjmethodID != nullptr) {
        try {
            newObject = env->NewGlobalRef(env->NewObject(jclazz, mjmethodID, x));
        } catch (const std::exception &e){
            std::cout << e.what();
        }
    }

    jboolean flag = env->ExceptionCheck();
    if (flag) {
        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", "error when creating new object");
    }

    return newObject;
}

void TestData::deleteGlobalRef(JNIEnv *env, jobject obj) {
    env->DeleteGlobalRef(obj);
}