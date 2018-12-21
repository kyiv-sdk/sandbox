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
    jclazz = env->FindClass("com/example/iyuro/mktest/TestData");
    jmethodID mjmethodID = nullptr;
    jobject newObject = nullptr;
    if (jclazz != nullptr)
        mjmethodID = env->GetMethodID(jclazz, "<init>", "(I)V");
    if (mjmethodID != nullptr) {
        try {
            newObject = env->NewObject(jclazz, mjmethodID, x);
        } catch (const std::exception &e){
            std::cout << e.what();
        }
    }

    // start test code

//    jclass testClass = main::findClass("com/example/iyuro/mktest/TestData");
//    if (testClass != nullptr)
//        jmethodID = env->GetMethodID(testClass, "<init>", "(I)V");
//    if (jmethodID != nullptr) {
//        try {
//            newObject = env->NewObject(testClass, jmethodID, x);
//        } catch (const std::exception &e){
//            std::cout << e.what();
//        }
//    }

    // end test code

//    jobject newObject;
//    jclass jclazz;
//    jmethodID mjmethodID;
////    if (newObject == nullptr) {
//        jclassReference testDataReference = main::getJclassReferenceByName(
//                "com/example/iyuro/mktest/TestData");
//
//        jclazz = testDataReference.getJclazz();
//        if (jclazz == nullptr)
//            return nullptr;
//
////        mjmethodID = testDataReference.getJconstructorID();
//        mjmethodID = env->GetMethodID(jclazz, "<init>", "(I)V");
//        if (mjmethodID != nullptr) {
//            try {
//                newObject = env->NewObject(jclazz, mjmethodID, x);
//            } catch (const std::exception &e) {
//                std::cout << e.what();
//            }
//        }
//    }

//    jclass cls = env->GetObjectClass(type);
//    jboolean flag = env->ExceptionCheck();
//    if (flag) {
//        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", "error1");
//    }
//    jmethodID mid = env->GetMethodID(cls, "logg", "(Ljava/lang/String;)V");
//    flag = env->ExceptionCheck();
//    if (flag) {
//        __android_log_print(ANDROID_LOG_DEBUG, "--------MY_LOG--------", ":%s", "error2");
//    }
//    if (mid == 0) {
//        return nullptr;
//    }
//    jstring text = env->NewStringUTF("it works");
//    env->CallVoidMethod(type, mid, text);

//    env->DeleteLocalRef(jclazz);

//    env->DeleteGlobalRef(newObject);

    return newObject;
}

void TestData::deleteGlobalRef(JNIEnv *env, jobject obj) {
    env->DeleteGlobalRef(obj);
}