//
// Created by Ivan Yurovych on 12/18/18.
//

#ifndef MKTEST_TESTDATA_H
#define MKTEST_TESTDATA_H

#include <jni.h>
#include "jclassReference.h"
#include "main.h"

class TestData {
public:
    static jobject getNewTestData(JNIEnv *env, jobject type,
                           jint x);

    static void deleteGlobalRef(JNIEnv *env, jobject obj);
};

extern "C"
JNIEXPORT jobject JNICALL
Java_com_example_iyuro_mktest_MainActivity_getNewTestData(JNIEnv *env, jclass type,
                                                          jint x);


#endif //MKTEST_TESTDATA_H
