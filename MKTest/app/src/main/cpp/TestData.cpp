#include <iostream>
#include "TestData.h"

extern "C"
JNIEXPORT jobject JNICALL
Java_com_example_iyuro_mktest_MainActivity_getNewTestData(JNIEnv *env, jclass type,
                                                      jint x) {
    return TestData::getNewTestData(env, type, x);
}

jobject TestData::getNewTestData(JNIEnv *env, jclass type,
                                 jint x) {
    jclass jclazz = nullptr;
    jclazz = env->FindClass("com/example/iyuro/mktest/TestData");
    jmethodID jmethodID = nullptr;
    jobject newObject = nullptr;
    if (jclazz != nullptr)
        jmethodID = env->GetMethodID(jclazz, "<init>", "(I)V");
    if (jmethodID != nullptr) {
        try {
            newObject = env->NewObject(jclazz, jmethodID, x);
        } catch (const std::exception &e){
            std::cout << e.what();
        }
    }

    if (newObject == nullptr) {
        jclassReference testDataReference = main::getJclassReferenceByName(
                "com/example/iyuro/mktest/TestData");

        jclazz = testDataReference.getJclazz();
        if (jclazz == nullptr)
            return nullptr;

        jmethodID = testDataReference.getJconstructorID();
        if (jmethodID != nullptr) {
            try {
                newObject = env->NewObject(jclazz, jmethodID, x);
            } catch (const std::exception &e) {
                std::cout << e.what();
            }
        }
    }

    env->DeleteLocalRef(jclazz);
//    env->DeleteGlobalRef(newObject);

    return newObject;
}

void TestData::deleteGlobalRef(JNIEnv *env, jobject obj) {
    env->DeleteGlobalRef(obj);
}